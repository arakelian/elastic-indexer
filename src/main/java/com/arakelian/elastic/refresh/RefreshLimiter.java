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
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public interface RefreshLimiter extends Closeable {
    /**
     * Refreshes the given index asynchronously.
     *
     * @param name
     *            index name
     * @throws RejectedExecutionException
     *             if exceptions occurs while refreshing index
     */
    void enqueueRefresh(String name) throws RejectedExecutionException;

    boolean tryRefresh(String name);

    boolean tryRefresh(String name, long timeout, TimeUnit unit) throws InterruptedException;

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
    boolean waitForRefresh(String name, long timeout, TimeUnit unit);

}
