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
import java.util.Optional;
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
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Indexes or deletes a group of documents from one or more Elastic indexes using the Elastic Bulk
 * Index API.
 */
public class BulkIndexer implements Closeable {
    /**
     * Represents a single, asynchronous flushing of bulk operations to the Elastic Bulk API.
     */
    private final class Batch implements Callable<BulkResponse> {
        private final int id;
        private final ImmutableList<BulkOperation> operations;
        private final int totalBytes;
        private final int delayMillis;
        private final int attempt;

        public Batch(
                final ImmutableList<BulkOperation> operations,
                final int totalBytes,
                final int delayMillis,
                final int attempt) {
            Preconditions.checkNotNull(operations);
            this.id = BULK_ID.incrementAndGet();
            this.operations = operations;
            this.totalBytes = totalBytes;
            this.delayMillis = delayMillis;
            this.attempt = Math.min(attempt, 1);
            BulkIndexer.this.totalBytes.addAndGet(totalBytes);
        }

        @Override
        public BulkResponse call() throws IOException, InterruptedException {
            if (delayMillis != 0) {
                LOGGER.info(
                        "Waiting {} before sending retry of {}",
                        MoreStringUtils.toString(delayMillis, TimeUnit.MILLISECONDS),
                        this);
                Thread.sleep(delayMillis);
            }

            LOGGER.info("Sending {}", this);
            final CharSequence ops = buildPayload();

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

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this) //
                    .omitNullValues() //
                    .add("id", id) //
                    .add("operations", operations.size()) //
                    .add("totalBytes", totalBytes) //
                    .add("attempt", attempt) //
                    .toString();
        }

        /**
         * Returns the payload for the Elastic bulk API endpoint.
         *
         * We compute this by concatenating all of the individual operations being performed on each
         * document.
         *
         * Note that we never convert the StringBuilder to a String to unnecessarily allocate extra
         * RAM.
         *
         * @return payload for the Elastic bulk API endpoint.
         */
        private CharSequence buildPayload() {
            // to reduce memory fragmentation when indexing billions of records, let's round up
            // memory allocation
            final int size = roundAllocation(totalBytes);

            // allocate a string
            final StringBuilder buf = new StringBuilder(size);
            for (final BulkOperation op : operations) {
                buf.append(op.getOperation());
            }
            return buf;
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
    }

    /**
     * Callback that handles response from a single {@link Batch}. This listener must iterate the
     * batch and ensure that all operations are marked as successful or failed. It may also elect to
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
                    listener.onSuccess(op, status);
                    continue;
                }

                // ignore certain 404 errors
                if (op.getAction() == DELETE && status == 404) {
                    successful.incrementAndGet();
                    listener.onSuccess(op, status);
                    continue;
                }

                // check if we can retry
                if (isClosed() || !ElasticClientUtils.retryIfResponse(status)) {
                    // operation failed and is not retryable
                    failed.incrementAndGet();
                    if (status == 409) {
                        versionConflicts.incrementAndGet();
                    }
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
                retries.incrementAndGet();
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
                        config.getRetryDelayMillis(), batch.attempt + 1);

                // there is no need to check if we are closed, as we will get a
                // RejectedExecutionException.
                final ListenableFuture<BulkResponse> result = submitBatch(retryBatch);
                assert result != null;
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

    private static final int ONE_KB = 1024;

    private static final AtomicInteger BULK_ID = new AtomicInteger(0);

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkIndexer.class);

    public static int roundAllocation(final int bytes) {
        // JVM will have an easier time with free blocks if they're
        // the same size
        return (bytes + ONE_KB - 1) / ONE_KB * ONE_KB;
    }

    /** Configuration **/
    private final BulkIndexerConfig config;

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

    /** Number of documents that failed indexing due to version conflicts **/
    private final AtomicInteger versionConflicts = new AtomicInteger();

    /** Shutdown hook **/
    private final Thread shutdownHook;

    @SuppressWarnings("FutureReturnValueIgnored")
    public BulkIndexer(
            final ElasticClient elasticClient,
            final BulkIndexerConfig config,
            final RefreshLimiter refreshLimiter) {
        this.config = Preconditions.checkNotNull(config, "config must be non-null");
        this.elasticClient = Preconditions.checkNotNull(elasticClient, "elasticClient must be non-null");
        this.refreshLimiter = Preconditions.checkNotNull(refreshLimiter, "refreshLimiter must be non-null");

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
     * Adds a bulk operation to the pending batch, and returns a {@link ListenableFuture} that
     * represents that bulk update (or null if a batch operation is not available yet).
     *
     * @param op
     *            bulk operation
     * @param forceFlush
     *            true if flush operation should be forced, even if batch is not full
     * @return a Future that represents the batch, or null if batch is not flushed
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     */
    public Optional<ListenableFuture<BulkResponse>> add(final BulkOperation op, final boolean forceFlush)
            throws RejectedExecutionException {
        final ListenableFuture<BulkResponse> future = enqueue(op, forceFlush);
        if (future == null) {
            Preconditions.checkState(!forceFlush, "Expected bulk operation to result in future");
            return Optional.empty();
        }

        return Optional.of(future);
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
     * Deletes a list of documents from their respective Elastic indexes.
     *
     * @param documents
     *            list of documents to be removed
     * @return an optional future for retrieving bulk responses associated with this request
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> delete(final Collection<?> documents)
            throws RejectedExecutionException, IOException {
        if (documents == null || documents.size() == 0) {
            return Optional.empty();
        }

        // we do not acquire lock here because we might flush, which might cause us to block if
        // the queue is full!
        List<ListenableFuture<BulkResponse>> futures = null;
        for (final Object document : documents) {
            futures = add(document, DELETE, false, futures);
        }
        return combineFutures(futures);
    }

    /**
     * Delete specified document from Elastic index.
     *
     * @param document
     *            document to be deleted
     * @return an optional future for retrieving bulk responses associated with this request
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> delete(final Object document)
            throws RejectedExecutionException, IOException {
        return delete(document, false);
    }

    /**
     * Delete specified document from Elastic index.
     *
     * @param document
     *            document to be deleted
     * @param forceFlush
     *            true to force an immediate flush of data to Elastic
     * @return an optional future for retrieving bulk responses associated with this request
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> delete(
            final Object document,
            final boolean forceFlush) throws RejectedExecutionException, IOException {
        return combineFutures(add(document, DELETE, forceFlush, null));
    }

    /**
     * Flushes any pending bulk operations to Elastic asynchronously, and returns a future that
     * corresponds to the batch (or null if no batch operation is required).
     *
     * @return returns a future that corresponds to the batch, or null if no batch operation
     *         required
     * @throws RejectedExecutionException
     *             if indexer is closed (and we have pending operations) or background queue is full
     */
    public ListenableFuture<BulkResponse> flush() throws RejectedExecutionException {
        final Batch batch = createBatch(true);
        if (batch != null) {
            // we submit to executor outside a lock, since this thread could block if the
            // batch executor's queue is full
            return submitBatch(batch);
        }
        return null;
    }

    public final BulkIndexerConfig getConfig() {
        return config;
    }

    public RefreshLimiter getRefreshLimiter() {
        return refreshLimiter;
    }

    public BulkIndexerStats getStats() {
        return ImmutableBulkIndexerStats.builder() //
                .submitted(submitted.get()) //
                .retries(retries.get()) //
                .totalBytes(totalBytes.get()) //
                .successful(successful.get()) //
                .failed(failed.get()) //
                .versionConflicts(versionConflicts.get()) //
                .build();
    }

    /**
     * Adds a list of documents to the Elastic index without immediate index refresh and optional
     * flush.
     *
     * @param documents
     *            list of documents to index
     * @return an optional Future for retrieving bulk responses associated with this request.
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> index(final Collection<?> documents)
            throws RejectedExecutionException, IOException {
        if (documents == null || documents.size() == 0) {
            return Optional.empty();
        }

        // we do not acquire lock here because we might flush, which might cause us to block if
        // the queue is full!
        List<ListenableFuture<BulkResponse>> futures = null;
        for (final Object document : documents) {
            futures = add(document, INDEX, false, futures);
        }
        return combineFutures(futures);
    }

    /**
     * Adds a document to the Elastic index.
     *
     * @param document
     *            document to be indexed
     * @return an optional Future for retrieving bulk responses associated with this request.
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> index(final Object document)
            throws RejectedExecutionException, IOException {
        return index(document, false);
    }

    /**
     * Adds a document to the Elastic index.
     *
     * @param document
     *            document to be indexed
     * @param forceFlush
     *            true to force an immediate flush of data to Elastic
     * @return an optional Future for retrieving bulk responses associated with this request.
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> index(
            final Object document,
            final boolean forceFlush) throws RejectedExecutionException, IOException {
        return combineFutures(add(document, INDEX, forceFlush, null));
    }

    /**
     * Returns true if indexer has closed
     *
     * @return true if indexer has closed
     */
    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .omitNullValues() //
                .add("config", config) //
                .toString();
    }

    /**
     * Adds a bulk operation to the queue, using the given document and specified action.
     *
     * @param document
     *            document
     * @param action
     *            action to be performed on document
     * @param forceFlush
     *            true to force an immediate flush of data to Elastic
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    private List<ListenableFuture<BulkResponse>> add(
            final Object document,
            final Action action,
            final boolean forceFlush,
            List<ListenableFuture<BulkResponse>> futures) throws RejectedExecutionException, IOException {
        if (document == null) {
            return futures;
        }

        // might be closed after this, but save time
        ensureOpen();

        // a document may be indexed to multiple places
        final BulkOperationFactory factory = config.getBulkOperationFactory();
        if (!factory.supports(document)) {
            throw new IOException("Unsupported document: " + document);
        }

        final List<BulkOperation> ops = factory.createBulkOperations(document, action);
        if (ops == null || ops.size() == 0) {
            return futures;
        }

        // we do not acquire lock here because an add may cause a flush, and a flush could
        // block waiting for the indexer queue to have space
        for (int i = 0, size = ops.size(); i < size; i++) {
            final BulkOperation op = ops.get(i);
            final ListenableFuture<BulkResponse> future = enqueue(op, forceFlush && i == size - 1);
            assert !forceFlush || future != null;

            if (future != null) {
                if (futures == null) {
                    futures = new ArrayList<>();
                }
                futures.add(future);
            }
        }

        return futures;
    }

    /**
     * Returns a future that combines the result of multiple futures.
     *
     * @param futures
     *            list of futures
     * @return a future that combines the result of multiple futures.
     */
    private Optional<ListenableFuture<List<BulkResponse>>> combineFutures(
            final List<ListenableFuture<BulkResponse>> futures) {
        if (futures == null || futures.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(Futures.allAsList(futures));
        }
    }

    /**
     * Return a new {@link Batch} from the list of queued bulk operations.
     *
     * If there are no pending operations that need to be flushed, or the batch size thresholds have
     * not been met (and force is false), this method will return null.
     *
     * @param forceFlush
     *            true to force a Batch to be created
     * @return a new {@link Batch}, or null
     * @throws RejectedExecutionException
     *             if indexer is closed (and we have pending operations) or background queue is full
     */
    private Batch createBatch(final boolean forceFlush) throws RejectedExecutionException {
        batchLock.lock();
        try {
            final int size = pendingOperations.size();
            if (size == 0) {
                return null;
            }

            // indexer may have closed since we acquired lock
            ensureOpen();

            // we allow flush to occur after indexer is closed
            if (forceFlush || size >= config.getMaxBulkOperations()
                    || totalPendingBytes > config.getMaxBulkOperationBytes()) {
                final Batch batch = new Batch(ImmutableList.copyOf(pendingOperations), //
                        totalPendingBytes, 0, 1);
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
     * Enqueue a bulk operation to the pending {@link Batch}.
     *
     * @param bulkOperation
     *            bulk operation
     * @param forceFlush
     *            true if flush operation should be forced, even if batch is not full
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     */
    private ListenableFuture<BulkResponse> enqueue(
            final BulkOperation bulkOperation,
            final boolean forceFlush) throws RejectedExecutionException {
        Preconditions.checkArgument(bulkOperation != null, "bulkOperation must be non-null");

        // we only hold the lock long enough to add the operation and create a batch if needed
        final Batch batch;
        batchLock.lock();
        try {
            // indexer may have closed since we acquired lock
            ensureOpen();

            // add to queue
            final CharSequence operation = bulkOperation.getOperation();
            Preconditions.checkState(
                    operation.charAt(operation.length() - 1) == '\n',
                    "Bulk operations must end with newline");
            totalPendingBytes += operation.length();
            pendingOperations.add(bulkOperation);

            // keep tally of what we put into queue
            submitted.incrementAndGet();

            // flush only when queue or memory thresholds are reached
            batch = createBatch(forceFlush);
        } finally {
            batchLock.unlock();
        }

        // we submit to executor outside the lock, since this thread could block if the queue is
        // full
        return batch != null ? submitBatch(batch) : null;
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
     * Flushes any pending bulk operations to Elastic asynchronously, and quietly eats any
     * exceptions that may occur.
     */
    @SuppressWarnings("FutureReturnValueIgnored")
    private void flushQuietly() {
        try {
            flush();
        } catch (final Exception e) {
            LOGGER.warn("Unable to flush {}", this, e);
        }
    }

    /**
     * Submits a batch of bulk updates to the {@link #batchExecutor}.
     *
     * @param batch
     *            batch to be submitted
     * @throws RejectedExecutionException
     *             if background queue is full
     */
    private ListenableFuture<BulkResponse> submitBatch(final Batch batch) throws RejectedExecutionException {
        final int maxRetries = config.getMaxRetries();
        if (batch.attempt > maxRetries) {
            throw new RejectedExecutionException(
                    "Bulk indexer rejected after " + maxRetries + " attempts: " + batch);
        }

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
        return future;
    }
}
