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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.immutables.value.Value;

import com.arakelian.elastic.model.Refresh;
import com.arakelian.elastic.utils.ElasticClientUtils.RetryIoException;
import com.arakelian.retry.Retryer;
import com.arakelian.retry.RetryerBuilder;
import com.arakelian.retry.StopStrategies;
import com.arakelian.retry.WaitStrategies;
import com.google.common.util.concurrent.RateLimiter;

@Value.Immutable(copy = false)
public abstract class RefreshLimiterConfig {
    @Value.Default
    public int getCoreThreads() {
        return 2;
    }

    @Value.Default
    public double getDefaultPermitsPerSecond() {
        return 1 / 60.0;
    }

    @Value.Default
    public int getMaximumThreads() {
        return 5;
    }

    @Value.Auxiliary
    public abstract Map<String, RateLimiter> getRateLimiter();

    @Value.Default
    @Value.Auxiliary
    public Retryer<Refresh> getRetryer() {
        return RetryerBuilder.<Refresh> newBuilder() //
                .retryIfException(new RetryIoException()) //
                .withStopStrategy(StopStrategies.stopAfterDelay(1, TimeUnit.MINUTES)) //
                .withWaitStrategy(WaitStrategies.fixedWait(5, TimeUnit.SECONDS)) //
                .withRetryListener(new RefreshRetryListener()) //
                .build();
    }
}
