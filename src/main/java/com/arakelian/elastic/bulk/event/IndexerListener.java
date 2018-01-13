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

package com.arakelian.elastic.bulk.event;

import com.arakelian.elastic.bulk.BulkIndexer;
import com.arakelian.elastic.bulk.BulkOperation;
import com.arakelian.elastic.model.BulkIndexerConfig;
import com.arakelian.elastic.model.BulkIndexerStats;
import com.arakelian.elastic.model.BulkResponse;
import com.arakelian.elastic.model.BulkResponse.BulkOperationResponse;

/**
 * A callback for accepting the results of a {@link BulkIndexer} computation asynchronously.
 *
 * Note: It's important that the callback is not slow or heavyweight, as the {@link BulkIndexer} is
 * prevented from running during its execution, even if those listeners are to run in other
 * executors.
 */
public interface IndexerListener {
    /**
     * Finished
     *
     * @param stats
     *            indexer stats
     */
    void closed(BulkIndexerStats stats);

    /**
     * Invoked when a {@code BulkOperation} fails individually. These operations always have a
     * status code outside the 2xx range, and they are generally not retryable because they have
     * status codes below the 5xx range, which are reserved for server-side issues.
     *
     * @param op
     *            bulk operation
     * @param response
     *            bulk operation response
     */
    void onFailure(BulkOperation op, BulkOperationResponse response);

    /**
     * Invoked when a {@code BulkOperation} fails because the entire request to the Elastic Bulk API
     * failed, always returning an HTTP status code outside the 2xx range.
     *
     * Note: The bulk API request would been retried according to configured
     * {@link BulkIndexerConfig#getRetryer()} policy.
     *
     * @param op
     *            bulk operation that failed
     * @param result
     *            the response from the Elastic Bulk API, always with status code outside the 2xx
     *            range.
     */
    void onFailure(BulkOperation op, BulkResponse result);

    /**
     * Invoked when a {@code BulkOperation} fails because the entire request failed, usually due to
     * an IOException. It's possible that the Elastic Bulk API never received the request.
     *
     * Note: The bulk API request would been retried according to configured
     * {@link BulkIndexerConfig#getRetryer()} policy.
     *
     * @param op
     *            bulk operation that failed
     * @param t
     *            the exception that caused failure, usually an IOException
     */
    void onFailure(BulkOperation op, Throwable t);

    /**
     * Invoked when a {@code BulkOperation} is successful. These operations usually have a status
     * code in the 2xx range, but there are exceptions, such as 404 status code for delete
     * operations which are benign and merely reported that the requested record was not found.
     *
     * @param op
     *            bulk operation
     */
    void onSuccess(BulkOperation op);
}
