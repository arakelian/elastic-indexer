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

package com.arakelian.elastic.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.ElasticClient;
import com.arakelian.elastic.api.About;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import retrofit2.Response;

public class ElasticClientUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticClientUtils.class);

    public static final String DEFAULT_TIMEOUT = "30s";

    /**
     * Returns true if call to Elastic should be retried.
     *
     * <ul>
     * <li>HTTP 1xx are informational, and retry will have no benefit.</li>
     * <li>HTTP 2xx are success, and retry makes no sense.</li>
     * <li>HTTP 3xx are redirect message, and should be handled by Retrofit/OkHttp3 client.</li>
     * <li>HTTP 4xx are bad client requests, except for status 429 (TOO_MANY_REQUESTS).</li>
     * <li>HTTP 5xx are server errors, and retry is worthwhile</li>
     * </ul>
     *
     * @param status
     *            status code from HTTP response
     * @return true if call to Elastic should be retried
     */
    public static boolean retryIfResponse(final int status) {
        // note: status 429 is TOO_MANY_REQUESTS and it is a server exception that should be retried
        return status == 429 || status >= 500;
    }

    /**
     * Returns true if call to Elastic should be retried.
     *
     * @param response
     *            response from last call to Elastic
     * @return true if call to Elastic should be retried.
     */
    public static boolean retryIfResponse(final Response<?> response) {
        return response != null ? retryIfResponse(response.code()) : false;
    }

    public static About waitForElasticReady(final ElasticClient elasticClient, final long timeout,
            final TimeUnit unit) {
        final Retryer<About> retryer = RetryerBuilder.<About> newBuilder() //
                .retryIfException() //
                .withStopStrategy(StopStrategies.stopAfterDelay(timeout, unit)) //
                .withWaitStrategy(WaitStrategies.fixedWait(5, TimeUnit.SECONDS)) //
                .build();

        // wait for elastic
        try {
            final About info = retryer.call(() -> {
                return elasticClient.about().execute().body();
            });
            return info;
        } catch (final ExecutionException e) {
            LOGGER.warn("Unable to retrieve Elastic information after {} {}", timeout, unit, e);
            return null;
        } catch (final RetryException e) {
            LOGGER.warn("Unable to retrieve Elastic information after {} {}", timeout, unit, e);
            return null;
        }
    }

    private ElasticClientUtils() {
        // utility class
    }
}
