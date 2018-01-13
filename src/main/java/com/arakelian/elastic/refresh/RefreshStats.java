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

import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.MoreObjects;

public class RefreshStats {
    /** Number of times attempt was made to acquire rate limiter **/
    private final AtomicLong acquires = new AtomicLong(0);

    /** Number of times Elastic refresh method was called **/
    private final AtomicLong attempts = new AtomicLong(0);

    /** Number of times Elastic refresh method succeeded **/
    private final AtomicLong successful = new AtomicLong(0);

    /** Number of times Futures created to perform refresh in background **/
    private final AtomicLong futures = new AtomicLong(0);

    public final AtomicLong getAcquires() {
        return acquires;
    }

    public final AtomicLong getAttempts() {
        return attempts;
    }

    public final AtomicLong getFutures() {
        return futures;
    }

    public final AtomicLong getSuccessful() {
        return successful;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .omitNullValues() //
                .add("acquires", acquires.get()) //
                .add("refreshes", attempts.get()) //
                .add("successful", successful.get()) //
                .add("futures", futures.get()) //
                .toString();
    }
}
