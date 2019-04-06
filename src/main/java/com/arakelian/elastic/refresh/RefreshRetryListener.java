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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.retry.Attempt;
import com.arakelian.retry.RetryListener;

public final class RefreshRetryListener implements RetryListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshRetryListener.class);

    @Override
    public <V> void onRetry(final Attempt<V> attempt) {
        final long attemptNumber = attempt.getAttemptNumber();
        if (attemptNumber > 1) {
            LOGGER.debug(
                    "Attempt {} occurring {} after first attempt",
                    attemptNumber, //
                    MoreStringUtils.toString(attempt.getDelaySinceFirstAttempt(), TimeUnit.MILLISECONDS));
        }
    }
}
