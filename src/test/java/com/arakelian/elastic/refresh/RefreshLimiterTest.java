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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.model.ImmutableRefresh;
import com.arakelian.elastic.model.ImmutableShards;
import com.arakelian.elastic.model.Refresh;
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.okhttp.MockOkHttpElasticApi;
import com.arakelian.elastic.okhttp.OkHttpElasticApi;
import com.arakelian.elastic.okhttp.OkHttpElasticClient;
import com.arakelian.jackson.utils.JacksonUtils;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

public class RefreshLimiterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshLimiterTest.class);
    private NetworkBehavior networkBehavior;

    private BehaviorDelegate<OkHttpElasticApi> delegate;

    @BeforeEach
    public void initializeRetrofit() {
        final Retrofit retrofit = new Retrofit.Builder() //
                .baseUrl("http://localhost") //
                .addConverterFactory(ScalarsConverterFactory.create()) //
                .addConverterFactory(JacksonConverterFactory.create(JacksonUtils.getObjectMapper())) //
                .build();

        // configure Mock delegate
        networkBehavior = NetworkBehavior.create();
        networkBehavior.setDelay(0, TimeUnit.MICROSECONDS);
        final MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit) //
                .networkBehavior(networkBehavior) //
                .build();
        delegate = mockRetrofit.create(OkHttpElasticApi.class);
    }

    private MockOkHttpElasticApi mockApi() {
        final MockOkHttpElasticApi elasticClient = new MockOkHttpElasticApi(delegate) {
            @Override
            public Call<Refresh> refreshIndex(final String names) {
                refreshCount.incrementAndGet();
                final Refresh response = ImmutableRefresh.builder() //
                        .shards(
                                ImmutableShards.builder() //
                                        .successful(1) //
                                        .failed(0) //
                                        .total(1) //
                                        .build()) //
                        .build();
                return delegate.returning(Calls.response(response)).refreshIndex(names);
            }
        };
        return elasticClient;
    }

    @Test
    public void testEnqueueOnePerSecond() {
        verifyEnqueue(3000, 1.0d, false);
    }

    @Test
    public void testEnqueueOnePerSecondNetworkDead() {
        verifyEnqueue(3000, 1.0d, true);
    }

    @Test
    public void testEnqueueTenPerSecond() {
        verifyEnqueue(3000, 10.0d, false);
    }

    @Test
    public void testTryAcquireOnePerSecond() {
        verifyTryAcquire(3000, 1.0d);
    }

    @Test
    public void testTryAcquireTenPerSecond() {
        verifyTryAcquire(3000, 10.0d);
    }

    private void verifyEnqueue(
            final int durationMillis,
            final double permitsPerSecond,
            final boolean networkFailures) {
        LOGGER.info(
                "Starting rate limiter test for {}ms at {}/second (failures: {})",
                durationMillis,
                permitsPerSecond,
                networkFailures);

        final RefreshLimiterConfig config = ImmutableRefreshLimiterConfig.builder() //
                .coreThreads(1) //
                .maximumThreads(1) //
                .defaultPermitsPerSecond(permitsPerSecond) //
                .build();

        final MockOkHttpElasticApi mockApi = mockApi();
        final OkHttpElasticClient elasticClient = new OkHttpElasticClient("http://localhost",
                (elasticUrl, mapper) -> {
                    return mockApi;
                }, JacksonUtils.getObjectMapper(), VersionComponents.of("6.0.1"));
        networkBehavior.setFailurePercent(networkFailures ? 100 : 0);

        final String index = "test";
        try (DefaultRefreshLimiter refreshLimiter = new DefaultRefreshLimiter(config, elasticClient)) {
            final int duration = durationMillis;
            final long stopTime = System.currentTimeMillis() + duration;
            while (System.currentTimeMillis() < stopTime) {
                refreshLimiter.enqueueRefresh(index);
            }
            refreshLimiter.waitForRefresh(index, 1, TimeUnit.MINUTES);

            final RefreshStats stats = refreshLimiter.getStats(index);
            final AtomicLong successful = stats.getSuccessful();
            final AtomicLong refreshes = stats.getAttempts();
            final AtomicInteger refreshCount = mockApi.refreshCount;
            LOGGER.info("Testing completed: {}", stats);

            final double actualPermitsPerSecond = (double) successful.get() / (double) duration * 1000.0;
            LOGGER.info(
                    "Expected {}/second and was {}/second",
                    permitsPerSecond,
                    Math.round(actualPermitsPerSecond * 100.0) / 100.0);
            final int expected = (int) (permitsPerSecond * duration / 1000.0);
            if (networkFailures) {
                Assertions.assertTrue(
                        expected < refreshes.get(),
                        "Expected more than " + refreshes.get() + " refreshes due to network failures");
                Assertions.assertEquals(0, successful.get());
                Assertions.assertTrue(
                        expected < refreshCount.get(),
                        "Expected more than " + refreshCount.get() + " refreshes due to network failures");
            } else {
                Assertions.assertTrue(
                        Math.abs(expected - refreshes.get()) <= 1,
                        "Expected " + expected + " but was " + refreshes.get());
                Assertions.assertTrue(
                        Math.abs(expected - successful.get()) <= 1,
                        "Expected " + expected + " but was " + successful.get());
                Assertions.assertTrue(
                        Math.abs(expected - refreshCount.get()) <= 1,
                        "Expected " + expected + " but was " + refreshCount.get());
            }
        }
    }

    private void verifyTryAcquire(final int durationMillis, final double permitsPerSecond) {
        LOGGER.info("Starting rate limiter test for {}ms at {}/second", durationMillis, permitsPerSecond);

        final RefreshLimiterConfig config = ImmutableRefreshLimiterConfig.builder() //
                .coreThreads(1) //
                .maximumThreads(1) //
                .defaultPermitsPerSecond(permitsPerSecond) //
                .build();

        final MockOkHttpElasticApi mockApi = mockApi();
        final OkHttpElasticClient elasticClient = new OkHttpElasticClient("http://localhost",
                (elasticUrl, mapper) -> {
                    return mockApi;
                }, JacksonUtils.getObjectMapper(), VersionComponents.of("6.0.1"));
        networkBehavior.setFailurePercent(0);

        final String index = "test";
        int attempts = 0;
        int successful = 0;
        try (DefaultRefreshLimiter refreshLimiter = new DefaultRefreshLimiter(config, elasticClient)) {
            final int duration = durationMillis;
            final long stopTime = System.currentTimeMillis() + duration;
            while (System.currentTimeMillis() < stopTime) {
                attempts++;
                if (refreshLimiter.tryRefresh(index)) {
                    successful++;
                }
            }

            final RefreshStats stats = refreshLimiter.getStats(index);
            LOGGER.info("Testing completed: {}", stats);
            final int expected = (int) (config.getDefaultPermitsPerSecond() * duration / 1000.0);
            Assertions.assertEquals(successful, stats.getAttempts().get());
            Assertions.assertEquals(successful, stats.getSuccessful().get());
            Assertions.assertEquals(attempts, stats.getAcquires().get());
            Assertions.assertEquals(successful, mockApi.refreshCount.get());
            Assertions.assertTrue(
                    Math.abs(expected - mockApi.refreshCount.get()) <= 1,
                    "Expected " + expected + " but was " + mockApi.refreshCount.get());
        }
    }
}
