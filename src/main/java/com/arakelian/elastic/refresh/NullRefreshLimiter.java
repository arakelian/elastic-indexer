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

import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class NullRefreshLimiter implements RefreshLimiter {
    /** Singleton instance **/
    public static final NullRefreshLimiter INSTANCE = new NullRefreshLimiter();

    private NullRefreshLimiter() {
        // singleton
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }

    @Override
    public void enqueueRefresh(final String name) throws RejectedExecutionException {
        // do nothing
    }

    @Override
    public boolean tryRefresh(final String name) {
        // do nothing
        return true;
    }

    @Override
    public boolean tryRefresh(final String name, final long timeout, final TimeUnit unit)
            throws InterruptedException {
        // do nothing
        return true;
    }

    @Override
    public boolean waitForRefresh(final String name, final long timeout, final TimeUnit unit) {
        // do nothing
        return true;
    }
}
