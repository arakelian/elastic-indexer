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

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Executor policy that blocks caller to execute task.
 */
public final class BlockCallerPolicy implements RejectedExecutionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockCallerPolicy.class);

    @Override
    public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            LOGGER.warn("Executor is busy; running task on calling thread: {}", executor);
            r.run();
        }
    }
}
