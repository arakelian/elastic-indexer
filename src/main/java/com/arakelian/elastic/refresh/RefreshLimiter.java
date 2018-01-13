/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arakelian.elastic.refresh;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.ElasticClient;
import com.arakelian.elastic.model.Refresh;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.RateLimiter;

/**
 * Indexes or deletes a group of documents from one or more Elastic indexes using the Elastic Bulk
 * Index API.
 */
public class RefreshLimiter implements Closeable {
    /**
     * Maintains information about the state of each Elastic index that we are tracking.
     */
    protected static class Index extends RefreshStats {
        private final String name;

        /** Rate limiter that must be acquired before refreshing index **/
        private final RateLimiter rateLimiter;

        /** True if refresh is queued with executor **/
        private final AtomicBoolean running = new AtomicBoolean(false);

        /** True if refresh has been requested since originally enqueued **/
        private final AtomicBoolean requeue = new AtomicBoolean(false);

        /** Used as synchronization lock to make class thread safe **/
        private final Lock lock = new ReentrantLock();

        /** Semaphore for signaling complete **/
        private final Condition refreshed = lock.newCondition();

        public Index(final String name, final RateLimiter rateLimiter) {
            this.name = name;
            this.rateLimiter = rateLimiter;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshLimiter.class);

    /** Elastic API **/
    private final ElasticClient elasticClient;

    /** Configuration **/
    private final RefreshLimiterConfig config;

    /** Wraps flush executor with listenable futures **/
    private final ListeningExecutorService refreshExecutor;

    /**
     * We maintain state for each of the Elastic indexes that we are refreshing. Using a Guava cache
     * makes it easy to be thread-safe.
     */
    private final LoadingCache<String, Index> indexes = CacheBuilder.newBuilder()
            .maximumSize(Integer.MAX_VALUE) //
            .build(new CacheLoader<String, Index>() {
                @Override
                public Index load(final String name) {
                    final RateLimiter rateLimiter = config.getRateLimiter().get(name);
                    if (rateLimiter != null) {
                        return new Index(name, rateLimiter);
                    }
                    final double permitsPerSecond = config.getDefaultPermitsPerSecond();
                    return new Index(name, RateLimiter.create(permitsPerSecond));
                }
            });

    /** We can only be closed once **/
    private final AtomicBoolean closed = new AtomicBoolean();

    public RefreshLimiter(final RefreshLimiterConfig config, final ElasticClient elasticClient) {
        Preconditions.checkArgument(config != null, "config must not be null");
        Preconditions.checkArgument(elasticClient != null, "elasticClient must not be null");
        this.config = config;
        this.elasticClient = elasticClient;

        refreshExecutor = MoreExecutors.listeningDecorator(
                MoreExecutors.getExitingExecutorService( //
                        new ThreadPoolExecutor( //
                                config.getCoreThreads(), //
                                config.getMaximumThreads(), //
                                10L, TimeUnit.SECONDS, //
                                new LinkedBlockingQueue<>())));
    }

    /**
     * Called during shutdown to terminate the scheduled executor thread.
     */
    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            LOGGER.info("Closing {}", this);
            refreshExecutor.shutdown();
        }
    }

    private void complete(final Index index) {
        index.lock.lock();
        try {
            if (index.running.compareAndSet(true, false)) {
                index.refreshed.signalAll();
            }
            if (index.requeue.compareAndSet(true, false)) {
                enqueueRefresh(index.name);
            }
        } finally {
            index.lock.unlock();
        }
    }

    private void doEnqueue(final Index index) {
        final Stopwatch timer = Stopwatch.createStarted();

        final long id = index.getFutures().incrementAndGet();
        LOGGER.debug("Queuing refresh {} of index \"{}\"", id, index.name);

        ListenableFuture<Refresh> future = null;
        try {
            // process asynchronously
            future = doSubmit(index);

            // define callback when completed
            Futures.addCallback(future, new FutureCallback<Refresh>() {
                private void completeQuietly(final Index index) {
                    try {
                        complete(index);
                    } catch (final RejectedExecutionException e) {
                        // ignore
                    }
                }

                @Override
                public void onFailure(final Throwable t) {
                    LOGGER.warn("Refresh {} of index \"{}\" failed after {}", id, index.name, timer, t);
                    completeQuietly(index);
                }

                @Override
                public void onSuccess(final Refresh result) {
                    // refresh may have failed
                    LOGGER.debug(
                            "Refresh {} of index \"{}\" completed successfully after {}: {}",
                            id,
                            index.name,
                            timer,
                            result);
                    completeQuietly(index);
                }
            }, MoreExecutors.directExecutor());
        } finally {
            // if something went wrong during submit, reset flags!
            if (future == null) {
                complete(index);
            }
        }
    }

    /**
     * Refreshes the given Elastic index by calling Elastic client API.
     *
     * IMPORTANT: This method assumes that the rate limiter has been acquired.
     *
     * @param name
     *            index name
     * @return result of call to Elastic client if successful
     * @throws IOException
     */
    private Refresh doRefresh(final Index index) throws IOException {
        LOGGER.debug("Refreshing index \"{}\"", index.name);
        index.getAttempts().incrementAndGet();
        final Refresh response = elasticClient.refreshIndex(index.name);
        index.getSuccessful().incrementAndGet();
        return response;
    }

    /**
     * Refreshes the given Elastic index by calling Elastic client API, and returns true if the call
     * was successful. If the refresh fails for whatever reason, the error is logged and this method
     * returns false.
     *
     * IMPORTANT: This method assumes that the rate limiter has been acquired.
     *
     * @param name
     *            index name
     * @return true if index was refreshed successfully
     */
    private boolean doRefreshQuietly(final Index index) {
        try {
            final Refresh response = doRefresh(index);
            return response != null;
        } catch (final IOException e) {
            LOGGER.warn("Safetly ignoring refresh failure: {}", e.getMessage());
            return false;
        }
    }

    private ListenableFuture<Refresh> doSubmit(final Index index) {
        try {
            return refreshExecutor.submit(() -> {
                // acquire rate limiter before we do anything
                index.getAcquires().incrementAndGet();
                final double secondsWaited = index.rateLimiter.acquire();
                final long nanosWaited = (long) (secondsWaited * 1000000000);
                LOGGER.debug(
                        "Waited {} to acquire rate limiter for index \"{}\"",
                        MoreStringUtils.toString(nanosWaited, TimeUnit.NANOSECONDS),
                        index.name);

                return config.getRetryer().wrap(() -> {
                    // we are about to perform refresh, so we can clear the requeue flag
                    index.requeue.set(false);

                    // perform refresh
                    return doRefresh(index);
                }).call();
            });
        } catch (final RejectedExecutionException e) {
            throw new RejectedExecutionException("Unable to enqueue refresh of index " + index.name, e);
        }
    }

    /**
     * Refreshes the given index asynchronously.
     *
     * @param name
     *            index name
     * @throws RejectedExecutionException
     *             if exceptions occurs while refreshing index
     */
    public void enqueueRefresh(final String name) throws RejectedExecutionException {
        if (closed.get()) {
            throw new RejectedExecutionException("Refresh is closed");
        }

        final Index index = getIndex(name);
        index.lock.lock();
        try {
            if (index.running.compareAndSet(false, true)) {
                doEnqueue(index);
            } else {
                // if refresh is in progress, request requeue after it completes
                index.requeue.compareAndSet(false, true);
            }
        } finally {
            index.lock.unlock();
        }
    }

    /**
     * Returns a reference to our {@link ElasticClient}.
     *
     * @return reference to our {@link ElasticClient}.
     */
    public final ElasticClient getElasticClient() {
        return elasticClient;
    }

    private Index getIndex(final String name) {
        Preconditions.checkArgument(name != null, "name must be non-null");
        return indexes.getUnchecked(name);
    }

    public RefreshStats getStats(final String name) {
        return getIndex(name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .omitNullValues() //
                .add("config", config) //
                .toString();
    }

    public boolean tryRefresh(final String name) {
        if (closed.get()) {
            return false;
        }

        final Index index = getIndex(name);
        index.getAcquires().incrementAndGet();
        if (index.rateLimiter.tryAcquire()) {
            LOGGER.debug("Acquired rate limiter for index \"{}\"", name);
            return doRefreshQuietly(index);
        }
        return false;
    }

    public boolean tryRefresh(final String name, final long timeout, final TimeUnit unit)
            throws InterruptedException {
        if (closed.get()) {
            return false;
        }

        final Index index = getIndex(name);
        index.getAcquires().incrementAndGet();
        if (index.rateLimiter.tryAcquire(timeout, unit)) {
            LOGGER.info("Acquired rate limiter for index \"{}\"", name);
            return doRefreshQuietly(index);
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        return false;
    }

    /**
     * Returns true if Elastic refresh is required and occurred within the given time frame; also
     * returns true if refresh is not required.
     *
     * @param name
     *            index name
     * @param timeout
     *            Length of time in milliseconds to wait for a refresh to occur
     * @param unit
     *            timeout units
     * @return Returns true if there are no dirty indices, false otherwise after timeout period
     */
    public boolean waitForRefresh(final String name, final long timeout, final TimeUnit unit) {
        final Index index = getIndex(name);
        index.lock.lock();
        try {
            if (!index.running.get()) {
                return true;
            }

            // wait specified timeout
            LOGGER.info(
                    "Waiting up to {} for refresh of index \"{}\" to complete",
                    MoreStringUtils.toString(timeout, unit),
                    name);
            final Stopwatch stopWatch = Stopwatch.createStarted();

            // we use a loop to verify condition due to spurious wakeups
            // see: http://errorprone.info/bugpattern/WaitNotInLoop
            while (index.running.get()) {
                index.refreshed.await(timeout, unit);
            }

            // refresh signaled
            LOGGER.info("Waited {} for refresh of index \"{}\" to complete", stopWatch, name);
            return true;
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            index.lock.unlock();
        }
    }
}
