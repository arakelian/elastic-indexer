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

package com.arakelian.elastic.bulk;

import static com.arakelian.elastic.bulk.BulkOperation.Action.DELETE;
import static com.arakelian.elastic.bulk.BulkOperation.Action.INDEX;
import static com.google.common.util.concurrent.Uninterruptibles.getUninterruptibly;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.ExecutorUtils;
import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.ElasticClient;
import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.arakelian.elastic.bulk.event.IndexerListener;
import com.arakelian.elastic.model.BulkIndexerConfig;
import com.arakelian.elastic.model.BulkIndexerStats;
import com.arakelian.elastic.model.BulkResponse;
import com.arakelian.elastic.model.BulkResponse.BulkOperationResponse;
import com.arakelian.elastic.model.BulkResponse.Item;
import com.arakelian.elastic.model.ImmutableBulkIndexerStats;
import com.arakelian.elastic.refresh.RefreshLimiter;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Indexes or deletes a group of documents from one or more Elastic indexes using the Elastic Bulk
 * Index API.
 *
 * @param <T>
 *            document type
 */
public class BulkIndexer<T> implements Closeable {
    /**
     * Represents a single, asynchronous flushing of bulk operations to the Elastic Bulk API.
     */
    private final class Batch implements Callable<BulkResponse> {
        private final int id;
        private final ImmutableList<BulkOperation> operations;
        private final int totalBytes;
        private final int delayMillis;

        public Batch(
                final ImmutableList<BulkOperation> operations,
                final int totalBytes,
                final int delayMillis) {
            Preconditions.checkNotNull(operations);
            this.id = BULK_ID.incrementAndGet();
            this.operations = operations;
            this.totalBytes = totalBytes;
            this.delayMillis = delayMillis;
            BulkIndexer.this.totalBytes.addAndGet(totalBytes);
        }

        /**
         * Returns the payload for the Elastic bulk API endpoint.
         *
         * We compute this by concatenating all of the individual operations being performed on each
         * document.
         *
         * @return payload for the Elastic bulk API endpoint.
         */
        private String buildPayload() {
            final StringBuilder buf = new StringBuilder(totalBytes);
            for (final BulkOperation op : operations) {
                buf.append(op.getOperation());
            }
            final String ops = buf.toString();
            return ops;
        }

        @Override
        public BulkResponse call() throws IOException, InterruptedException {
            if (delayMillis != 0) {
                LOGGER.info(
                        "Waiting {} before sending {}",
                        MoreStringUtils.toString(delayMillis, TimeUnit.MILLISECONDS),
                        this);
                Thread.sleep(delayMillis);
            }

            LOGGER.info("Sending {}", this);
            final String ops = buildPayload();

            try {
                // we assume Retryer verifies that result.isSuccessful() is true
                final Retryer<BulkResponse> retryer = config.getRetryer();
                return retryer.call(() -> {
                    return elasticClient.bulk(ops, false);
                });
            } catch (final ExecutionException e) {
                throw new IOException("Unable to index " + this, e.getCause());
            } catch (final RetryException e) {
                throw new IOException("Unable to index " + this, e);
            } finally {
                refreshIndexes();
            }
        }

        private void failed(final Throwable t) {
            final IndexerListener listener = config.getListener();
            for (final BulkOperation op : operations) {
                failed.incrementAndGet();
                listener.onFailure(op, t);
            }
        }

        /**
         * Refresh all of the indexes that we've indexed data into.
         */
        private void refreshIndexes() {
            for (final BulkOperation op : operations) {
                final String name = op.getIndex().getName();
                try {
                    refreshLimiter.enqueueRefresh(name);
                } catch (final RejectedExecutionException e) {
                    LOGGER.warn("Unable to queue refresh of index \"{}\"", name, e);
                }
            }
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this) //
                    .omitNullValues() //
                    .add("id", id) //
                    .add("operations", operations.size()) //
                    .add("totalBytes", totalBytes) //
                    .toString();
        }
    }

    /**
     * Callback that handles response from a single {@link Batch}. This listener must iterate the
     * batch and ensure that all operaions are marked as successful or failed. It may also elect to
     * retry an individual operation if it's possible.
     */
    private final class BatchListener implements Runnable {
        private final Stopwatch queued;
        private final Batch batch;
        private final ListenableFuture<BulkResponse> future;

        private BatchListener(
                final ListenableFuture<BulkResponse> future,
                final Batch batch,
                final Stopwatch queued) {
            this.future = future;
            this.batch = batch;
            this.queued = queued;
        }

        public void onFailure(final Throwable t) {
            LOGGER.warn("{} failed after {}", batch, queued, t);
            batch.failed(t);
        }

        public void onSuccess(final BulkResponse bulk) throws RejectedExecutionException {
            LOGGER.debug("Completed {} after {}", batch, queued);

            final IndexerListener listener = config.getListener();

            // according to ES documentation, the response order is correlated 1-to-1 with
            // request order
            final List<Item> items = bulk.getItems();
            final int size = items.size();
            Preconditions.checkState(
                    size == batch.operations.size(),
                    "Number of responses (%s) does not match number of batch operations (%s)",
                    size,
                    batch.operations.size());

            List<BulkOperation> retryable = null;
            for (int i = 0; i < size; i++) {
                final BulkOperation op = batch.operations.get(i);
                final BulkOperationResponse response = items.get(i).get();

                final int status = response.getStatus();
                if (status >= 200 && status < 300) {
                    // operation was successful
                    successful.incrementAndGet();
                    listener.onSuccess(op);
                    continue;
                }

                // ignore certain 404 errors
                if (op.getAction() == DELETE && status == 404) {
                    successful.incrementAndGet();
                    listener.onSuccess(op);
                    continue;
                }

                // check if we can retry
                if (isClosed() || !ElasticClientUtils.retryIfResponse(status)) {
                    // operation failed and is not retryable
                    failed.incrementAndGet();
                    listener.onFailure(op, response);
                    continue;
                }

                // sanity check before re-attempting operation; note that we don't compare index
                // values because they may be different due to aliasing
                Preconditions.checkState(
                        StringUtils.equals(op.getId(), response.getId()),
                        "Response id %s did not match request type %s",
                        response.getId(),
                        op.getId());
                Preconditions.checkState(
                        StringUtils.equals(op.getType(), response.getType()),
                        "Response type %s did not match request type %s",
                        response.getType(),
                        op.getType());

                // collect operations that we can retry
                if (retryable == null) {
                    retryable = new ArrayList<>(size);
                }
                retryable.add(op);
            }

            // since the retry batch is merely a subset of this batch, we know that it fits
            // within the size and byte limitations of batches generally; future improvement might
            // be to collect these and flush together
            if (retryable != null) {
                int totalBytes = 0;
                for (final BulkOperation op : retryable) {
                    totalBytes += op.getOperation().length();
                }

                final Batch retryBatch = new Batch( //
                        ImmutableList.copyOf(retryable), //
                        totalBytes, //
                        config.getRetryDelayMillis());

                // there is no need to check if we are closed, as we will get a
                // RejectedExecutionException.
                submitBatch(retryBatch);
            }
        }

        @Override
        public void run() {
            final BulkResponse value;
            try {
                value = getUninterruptibly(future);
            } catch (final ExecutionException e) {
                onFailure(e.getCause());
                return;
            } catch (final RuntimeException e) {
                onFailure(e);
                return;
            } catch (final Error e) {
                onFailure(e);
                return;
            }
            Preconditions.checkArgument(value != null, "Batch response must be non-null");
            onSuccess(value);
        }
    }

    /**
     * Automatically flushes bulk operation queue to Elastic at periodic intervals. This prevents us
     * from holding bulk operations in memory for long periods of time when the queue is not full.
     */
    private final class PeriodicFlush implements Runnable {
        @Override
        public void run() {
            // according to ScheduledExecutorService docs: "If any execution of the task
            // encounters an exception, subsequent executions are suppressed."; consequently, we
            // catch and log exceptions but otherwise continue
            if (!isClosed()) {
                flushQuietly();
            }
        }
    }

    private static final AtomicInteger BULK_ID = new AtomicInteger(0);

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkIndexer.class);

    /** Configuration **/
    private final BulkIndexerConfig<T> config;

    /** Elastic API **/
    private final ElasticClient elasticClient;

    /** Refresh limiter **/
    private final RefreshLimiter refreshLimiter;

    /** Used as synchronization lock to make class thread safe **/
    private final Lock batchLock = new ReentrantLock();

    /** Executor for automatic flushes **/
    private final ScheduledExecutorService flushExecutor;

    /** Executor for Elastic bulk API call **/
    private final ListeningExecutorService batchExecutor;

    /** Executor for processing Elastic bulk API response and retrying if needed **/
    private final ListeningExecutorService bulkResponseExecutor;

    /** Bulk operations waiting to be flushed **/
    private final List<BulkOperation> pendingOperations = Lists.newArrayList();

    /** Total number of bytes in bulk operations **/
    private int totalPendingBytes;

    /** We can only be closed once **/
    private final AtomicBoolean closed = new AtomicBoolean();

    /** Number of documents submitted for indexing **/
    private final AtomicInteger submitted = new AtomicInteger();

    /** Number of documents retried **/
    private final AtomicInteger retries = new AtomicInteger();

    /** Total number of bytes submitted or retried **/
    private final AtomicLong totalBytes = new AtomicLong();

    /** Number of documents successfully indexed **/
    private final AtomicInteger successful = new AtomicInteger();

    /** Number of documents that failed indexing **/
    private final AtomicInteger failed = new AtomicInteger();

    /** Shutdown hook **/
    private final Thread shutdownHook;

    @SuppressWarnings("FutureReturnValueIgnored")
    public BulkIndexer(final BulkIndexerConfig<T> config, final RefreshLimiter refreshLimiter) {
        Preconditions.checkArgument(config != null, "config must not be null");
        Preconditions.checkArgument(refreshLimiter != null, "elasticClient must not be null");
        this.config = config;
        this.refreshLimiter = refreshLimiter;
        this.elasticClient = refreshLimiter.getElasticClient();

        // we queue flushes when waiting for Elastic
        // determine what to do when queue is full
        final RejectedExecutionHandler rejectedExecutionHandler = //
                config.isBlockingQueue() ? new BlockCallerPolicy() : new ThreadPoolExecutor.AbortPolicy();

        // calls to Elastic bulk API are asynchronous
        final ThreadPoolExecutor bulkExecutor = new ThreadPoolExecutor( //
                1, config.getMaximumThreads(), //
                0L, TimeUnit.MILLISECONDS, //
                new LinkedBlockingQueue<>(config.getQueueSize()), //
                ExecutorUtils.newThreadFactory(getClass(), "-batch", false), // daemon
                rejectedExecutionHandler);
        this.batchExecutor = MoreExecutors.listeningDecorator(bulkExecutor);

        // processing of Elastic bulk API responses are asynchronous
        final ThreadPoolExecutor bulkResponseExecutor = new ThreadPoolExecutor( //
                1, config.getMaximumThreads(), //
                0L, TimeUnit.MILLISECONDS, //
                new LinkedBlockingQueue<>(config.getQueueSize()), //
                ExecutorUtils.newThreadFactory(getClass(), "-response", true), // daemon
                rejectedExecutionHandler);
        this.bulkResponseExecutor = MoreExecutors.listeningDecorator(bulkResponseExecutor);

        // schedule automatic flushes
        final int automaticFlushMillis = config.getAutomaticFlushMillis();
        if (automaticFlushMillis != 0) {
            flushExecutor = MoreExecutors.getExitingScheduledExecutorService( //
                    new ScheduledThreadPoolExecutor(1,
                            ExecutorUtils.newThreadFactory(getClass(), "-flush", false)), //
                    1,
                    TimeUnit.MINUTES);
            flushExecutor.scheduleWithFixedDelay(
                    new PeriodicFlush(), //
                    automaticFlushMillis, //
                    automaticFlushMillis, //
                    TimeUnit.MILLISECONDS);
        } else {
            flushExecutor = null;
        }

        // bulk thread pool is daemon, and we use shutdown hook to close it safely
        shutdownHook = ExecutorUtils.createShutdownHook(this);
    }

    /**
     * Adds a bulk operation to the pending bartch.
     *
     * @param op
     *            bulk operation
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     */
    public void add(final BulkOperation op) throws RejectedExecutionException {
        add(op, false);
    }

    /**
     * Adds a bulk operation to the pending {@link Batch}.
     *
     * @param op
     *            bulk operation
     * @param retry
     *            true if operation is being retried
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     */
    private void add(final BulkOperation bulkOperation, final boolean retry)
            throws RejectedExecutionException {
        Preconditions.checkArgument(bulkOperation != null, "bulkOperation must be non-null");

        // we only hold the lock long enough to add the operation and create a batch if needed
        final Batch batch;
        batchLock.lock();
        try {
            // indexer may have closed since we acquired lock
            ensureOpen();

            // add to queue
            final String operation = bulkOperation.getOperation();
            Preconditions.checkState(
                    operation.charAt(operation.length() - 1) == '\n',
                    "Bulk operations must end with newline");
            totalPendingBytes += operation.length();
            pendingOperations.add(bulkOperation);

            // keep tally of what we put into queue
            if (retry) {
                retries.incrementAndGet();
            } else {
                submitted.incrementAndGet();
            }

            // flush only when queue or memory thresholds are reached
            batch = createBatch(false);
        } finally {
            batchLock.unlock();
        }

        // we submit to executor outside the lock, since this thread could block if the queue is
        // full
        if (batch != null) {
            submitBatch(batch);
        }
    }

    /**
     * Adds a bulk operation to the queue, using the given document and specified action.
     *
     * @param document
     *            document
     * @param action
     *            action to be performed on document
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    private void add(final T document, final Action action) throws RejectedExecutionException, IOException {
        if (document == null) {
            return;
        }

        // might be closed after this, but save time
        ensureOpen();

        // a document may be indexed to multiple places
        final BulkOperationFactory<T> factory = config.getBulkOperationFactory();
        final List<BulkOperation> ops = factory.getBulkOperations(action, document);
        if (ops == null || ops.size() == 0) {
            return;
        }

        // we do not acquire lock here because an add may cause a flush, and a flush could
        // block waiting for the indexer queue to have space
        for (final BulkOperation op : ops) {
            add(op, false);
        }
    }

    /**
     * Called during shutdown to terminate the scheduled executor thread.
     *
     * @throws BulkIndexerFailed
     *             if exception occurs while closing indexer
     */
    @Override
    public void close() throws BulkIndexerFailed {
        final boolean shutdown;

        // we only hold the lock long enough to signal to other threads that we're shutting down and
        // will not take any more business
        batchLock.lock();
        try {
            // once we are closed, we will not flush any more data; everything has to be
            // in queue for processing
            LOGGER.info("Closing {}", this);
            flushQuietly();

            // we only shutdown once
            shutdown = closed.compareAndSet(false, true);
        } finally {
            batchLock.unlock();
        }

        if (shutdown) {
            // no more flushes allowed
            final int timeout = config.getShutdownTimeout();
            final TimeUnit unit = config.getShutdownTimeoutUnit();
            if (flushExecutor != null) {
                ExecutorUtils.shutdown(flushExecutor, timeout, unit, true);
            }

            // shutdown batch executor first; we want to ensure that bulk executor queue is
            // emptied before we shutdown the response executor
            ExecutorUtils.shutdown(batchExecutor, timeout, unit, true);

            // make sure we process any responses
            ExecutorUtils.shutdown(bulkResponseExecutor, timeout, unit, true);

            // shutdown hook is last thing to go
            ExecutorUtils.removeShutdownHook(shutdownHook);

            // compute final statistics and do notification
            final BulkIndexerStats stats = getStats();
            final IndexerListener listener = config.getListener();
            listener.closed(stats);

            // throw exception if indexer failed
            final int expected = stats.getSubmitted() + stats.getRetries();
            if (stats.getSuccessful() != expected) {
                throw new BulkIndexerFailed(stats);
            }
        }
    }

    /**
     * Return a new {@link Batch} from the list of queued bulk operations.
     *
     * If there are no pending operations that need to be flushed, or the batch size thresholds have
     * not been met (and force is false), this method will return null.
     *
     * @param force
     *            true to force a Batch to be created
     * @return a new {@link Batch}, or null
     * @throws RejectedExecutionException
     *             if indexer is closed (and we have pending operations) or background queue is full
     */
    private Batch createBatch(final boolean force) throws RejectedExecutionException {
        batchLock.lock();
        try {
            final int size = pendingOperations.size();
            if (size == 0) {
                return null;
            }

            // indexer may have closed since we acquired lock
            ensureOpen();

            // we allow flush to occur after indexer is closed
            if (force || size >= config.getMaxBulkOperations()
                    || totalPendingBytes > config.getMaxBulkOperationBytes()) {
                final Batch batch = new Batch(ImmutableList.copyOf(pendingOperations), totalPendingBytes, 0);
                pendingOperations.clear();
                totalPendingBytes = 0;
                return batch;
            }
        } finally {
            batchLock.unlock();
        }
        return null;
    }

    /**
     * Deletes a list of documents from their respective Elastic indexes.
     *
     * @param documents
     *            list of documents to be removed
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public void delete(final Collection<T> documents) throws RejectedExecutionException, IOException {
        if (documents != null && documents.size() != 0) {
            // we do not acquire lock here because we might flush, which might cause us to block if
            // the queue is full
            for (final T document : documents) {
                delete(document);
            }
        }
    }

    /**
     * Delete specified document from Elastic index.
     *
     * @param document
     *            document to be deleted
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public void delete(final T document) throws RejectedExecutionException, IOException {
        add(document, DELETE);
    }

    /**
     * Throws exception if indexer has been closed.
     *
     * @throws RejectedExecutionException
     *             if indexer has been closed
     */
    private void ensureOpen() throws RejectedExecutionException {
        if (isClosed()) {
            throw new AlreadyClosedException("Bulk indexer is closed");
        }
    }

    /**
     * Flushes any pending bulk operations to Elastic asynchronously.
     *
     * @throws RejectedExecutionException
     *             if indexer is closed (and we have pending operations) or background queue is full
     */
    public void flush() throws RejectedExecutionException {
        final Batch batch = createBatch(true);
        if (batch != null) {
            // we submit to executor outside a lock, since this thread could block if the
            // batch executor's queue is full
            submitBatch(batch);
        }
    }

    /**
     * Flushes any pending bulk operations to Elastic asynchronously, and quietly eats any
     * exceptions that may occur.
     */
    private void flushQuietly() {
        try {
            flush();
        } catch (final Exception e) {
            LOGGER.warn("Unable to flush {}", this, e);
        }
    }

    public final BulkIndexerConfig<T> getConfig() {
        return config;
    }

    public BulkIndexerStats getStats() {
        return ImmutableBulkIndexerStats.builder() //
                .submitted(submitted.get()) //
                .retries(retries.get()) //
                .totalBytes(totalBytes.get()) //
                .successful(successful.get()) //
                .failed(failed.get()) //
                .build();
    }

    /**
     * Adds a list of documents to the Elastic index without immediate index refresh and optional
     * flush.
     *
     * @param documents
     *            list of documents to index
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public void index(final Collection<T> documents) throws RejectedExecutionException, IOException {
        if (documents != null && documents.size() != 0) {
            // we do not acquire lock here because we might flush, which might cause us to block if
            // the queue is full
            for (final T document : documents) {
                index(document);
            }
        }
    }

    /**
     * Adds a document to the Elastic index.
     *
     * @param document
     *            document to be indexed
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public void index(final T document) throws RejectedExecutionException, IOException {
        add(document, INDEX);
    }

    /**
     * Returns true if indexer has closed
     *
     * @return true if indexer has closed
     */
    public boolean isClosed() {
        return closed.get();
    }

    /**
     * Submits a batch of bulk updates to the {@link #batchExecutor}.
     *
     * @param batch
     *            batch to be submitted
     * @throws RejectedExecutionException
     *             if background queue is full
     */
    private void submitBatch(final Batch batch) throws RejectedExecutionException {
        LOGGER.info("Queuing {}", batch);
        final Stopwatch queued = Stopwatch.createStarted();
        final ListenableFuture<BulkResponse> future;
        try {
            future = batchExecutor.submit(batch);
        } catch (final RejectedExecutionException e) {
            // batch is lost and cannot be recovered; increase the queue size, or enable blocking
            // queue in the configuration
            batch.failed(e);
            throw new RejectedExecutionException("Bulk indexer failed to process " + batch, e);
        }

        // process responses asynchronously too
        future.addListener(new BatchListener(future, batch, queued), bulkResponseExecutor);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .omitNullValues() //
                .add("config", config) //
                .toString();
    }
}
