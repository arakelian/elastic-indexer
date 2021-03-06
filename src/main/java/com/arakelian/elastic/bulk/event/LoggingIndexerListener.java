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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.bulk.BulkOperation;
import com.arakelian.elastic.model.BulkIndexerStats;
import com.arakelian.elastic.model.BulkResponse.BulkOperationResponse;

public final class LoggingIndexerListener implements IndexerListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingIndexerListener.class);

    public static final LoggingIndexerListener SINGLETON = new LoggingIndexerListener();

    private LoggingIndexerListener() {
    }

    @Override
    public void closed(final BulkIndexerStats stats) {
        LOGGER.debug(
                "Bulk indexer {}: {}",
                stats.getSuccessful() == stats.getTotal() ? "successful" : "FAILED",
                stats);
    }

    @Override
    public void onFailure(final BulkOperation op, final BulkOperationResponse response) {
        LOGGER.debug("Bulk operation failed: {}", op);
    }

    @Override
    public void onFailure(final BulkOperation op, final Throwable t) {
        LOGGER.debug("Bulk operation failed: {}", op);
    }

    @Override
    public void onSuccess(final BulkOperation op, final int statusCode) {
        LOGGER.debug("Bulk operation succeeded ({}): {}", statusCode, op);
    }
}
