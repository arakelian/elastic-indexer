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

package com.arakelian.elastic.model;

import java.util.concurrent.TimeUnit;

import org.immutables.value.Value;

import com.arakelian.elastic.bulk.BulkOperationFactory;
import com.arakelian.elastic.bulk.event.IndexerListener;
import com.arakelian.elastic.bulk.event.NullIndexerListener;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.StopStrategy;
import com.github.rholder.retry.WaitStrategies;
import com.github.rholder.retry.WaitStrategy;
import com.google.common.base.Preconditions;

@Value.Immutable(copy = false)
public abstract class BulkIndexerConfig {
    public static final int ONE_MEGABYTE = 1 * 1024 * 1024;

    /**
     * Returns the number of milliseconds to wait before automatically flushing the bulk queue. If
     * this is zero or negative, the bulk queue is never automatically flushed.
     *
     * @return number of milliseconds to wait before automatically flushing the bulk queue.
     */
    @Value.Default
    public int getAutomaticFlushMillis() {
        return 1000;
    }

    @Value.Auxiliary
    public abstract BulkOperationFactory getBulkOperationFactory();

    /**
     * Returns the default strategy for determining when to stop retrying network requests to
     * Elastic.
     *
     * @return the default strategy for determining when to stop retrying network requests to
     *         Elastic.
     */
    public StopStrategy getDefaultStopStrategy() {
        return StopStrategies.stopAfterDelay(10, TimeUnit.MINUTES);
    }

    /**
     * Returns the default strategy for determining how long to wait before retrying network
     * requests to Elastic.
     *
     * NOTE: This wait strategy is only used for network-level events, not for partial retries. See
     * {@link #getPartialRetryDelayMillis()}.
     *
     * @return the default strategy for determining how long to wait before retrying network
     *         requests to Elastic.
     */
    public WaitStrategy getDefaultWaitStrategy() {
        return WaitStrategies.exponentialWait(30, TimeUnit.SECONDS);
    }

    @Value.Default
    @Value.Auxiliary
    public IndexerListener getListener() {
        return NullIndexerListener.SINGLETON;
    }

    /**
     * Returns the maximum size of a bulk operation (in bytes)
     *
     * @return maximum size of a bulk operation
     */
    @Value.Default
    public int getMaxBulkOperationBytes() {
        return ONE_MEGABYTE; // 1MB
    }

    /**
     * Returns the maximum size of a bulk operation (in documents)
     *
     * @return maximum size of a batch operation (in documents)
     */
    @Value.Default
    public int getMaxBulkOperations() {
        // by default, we constrain packages sizes not the number of operations
        return Integer.MAX_VALUE;
    }

    @Value.Default
    public int getMaximumThreads() {
        return 2;
    }

    /**
     * Returns the fixed number of times to perform partial retries.
     *
     * @return the fixed number of times to perform partial retries.
     */
    @Value.Default
    public int getMaxPartialRetries() {
        return 10;
    }

    /**
     * Returns the fixed delay before attempting partial retries.
     *
     * @return the fixed delay before attempting partial retries.
     */
    @Value.Default
    public int getPartialRetryDelayMillis() {
        return 5000;
    }

    @Value.Default
    public int getQueueSize() {
        // if each flush is 1MB in size, this represents approximately 100MB of RAM
        return 100;
    }

    /**
     * Returns a retryer, which executes a call to Elastic, and retries it until it succeeds, or a
     * stop strategy decides to stop retrying.
     *
     * @return a retryer, which executes a call to Elastic, and retries it until it succeeds, or a
     *         stop strategy decides to stop retrying.
     */
    @Value.Default
    @Value.Auxiliary
    public Retryer<BulkResponse> getRetryer() {
        return ElasticClientUtils.createElasticRetryer();
    }

    @Value.Default
    public int getShutdownTimeout() {
        return 1;
    }

    @Value.Default
    public TimeUnit getShutdownTimeoutUnit() {
        return TimeUnit.MINUTES;
    }

    /**
     * Returns true if caller should block when indexer queue is full.
     *
     * @return true if caller should block when indexer queue is full.
     */
    @Value.Default
    public boolean isBlockingQueue() {
        return true;
    }

    @Value.Check
    protected void checkSettings() {
        Preconditions.checkState(getMaximumThreads() > 0, "numberOfThreads must be greater than 0");
        Preconditions
                .checkState(getMaxBulkOperationBytes() > 0, "maxBulkOperationBytes must be greater than 0");
        Preconditions.checkState(getQueueSize() > 0, "queueSize must be greater than 0");
    }
}
