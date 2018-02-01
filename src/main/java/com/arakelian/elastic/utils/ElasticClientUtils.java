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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.ElasticClient;
import com.arakelian.elastic.ElasticHttpException;
import com.arakelian.elastic.OkHttpElasticApi;
import com.arakelian.elastic.OkHttpElasticClient;
import com.arakelian.elastic.Views.Elastic.Version5;
import com.arakelian.elastic.Views.Elastic.Version5.Version52;
import com.arakelian.elastic.Views.Elastic.Version5.Version53;
import com.arakelian.elastic.Views.Elastic.Version5.Version54;
import com.arakelian.elastic.Views.Elastic.Version5.Version55;
import com.arakelian.elastic.Views.Elastic.Version5.Version56;
import com.arakelian.elastic.Views.Elastic.Version6;
import com.arakelian.elastic.Views.Elastic.Version6.Version61;
import com.arakelian.elastic.model.About;
import com.arakelian.elastic.model.ImmutableIndex;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.Index.WithoutNameSerializer;
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicate;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ElasticClientUtils {
    /**
     * Determines if an {@link IOException} is suitable for retry. Adapted from Apache Commons IO's
     * DefaultHttpMethodRetryHandler.
     */
    public static class RetryIoException implements Predicate<Throwable> {
        @Override
        public boolean apply(final Throwable exception) {
            if (exception instanceof SocketTimeoutException) {
                // Retry if the server dropped connection on us
                return true;
            }
            if (exception instanceof ElasticHttpException) {
                final ElasticHttpException e = (ElasticHttpException) exception;
                return retryIfResponse(e.getStatusCode());
            }
            // otherwise do not retry
            return false;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticClientUtils.class);

    public static final String DEFAULT_TIMEOUT = "30s";

    public static void configure(final ObjectMapper mapper, final VersionComponents version) {
        final Class<?> view = getJsonView(version);
        JacksonUtils.withView(mapper, view);
    }

    public static void configureIndexSerialization(final ObjectMapper mapper) {
        JsonSerializer<Object> delegate;
        try {
            delegate = mapper.copy().getSerializerProviderInstance()
                    .findTypedValueSerializer(ImmutableIndex.class, false, null);
        } catch (final JsonMappingException e) {
            throw new UncheckedIOException(e);
        }

        final SimpleModule module = new SimpleModule();
        module.addSerializer(Index.class, new WithoutNameSerializer(delegate));
        mapper.registerModule(module);
    }

    public static ElasticClient createElasticClient(
            final OkHttpClient client,
            final String elasticUrl,
            final ObjectMapper objectMapper,
            final VersionComponents version) {

        // we want Retrofit to have a uniquely configured ObjectMapper
        final ObjectMapper mapper = objectMapper.copy();

        // we will not know the version of Elastic when we first boot
        if (version != null) {
            configure(mapper, version);
        }

        // when serializing for Retrofit
        configureIndexSerialization(mapper);

        final Retrofit retrofit = new Retrofit.Builder() //
                .client(client) //
                .baseUrl(elasticUrl) //
                .addConverterFactory(ScalarsConverterFactory.create()) //
                .addConverterFactory(JacksonConverterFactory.create(mapper)) //
                .build();
        final OkHttpElasticApi api = retrofit.create(OkHttpElasticApi.class);
        final ElasticClient elasticClient = new OkHttpElasticClient(api, mapper, version);
        return elasticClient;
    }

    public static <T> Retryer<T> createElasticRetryer() {
        return RetryerBuilder.<T> newBuilder() //
                .retryIfException(new RetryIoException()) //
                .withStopStrategy(StopStrategies.stopAfterDelay(1, TimeUnit.MINUTES)) //
                .withWaitStrategy(WaitStrategies.fixedWait(5, TimeUnit.SECONDS)) //
                .build();
    }

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
        return response != null ? ElasticClientUtils.retryIfResponse(response.code()) : false;
    }

    public static About waitForElasticReady(
            final ElasticClient elasticClient,
            final long timeout,
            final TimeUnit unit) {
        final Retryer<About> retryer = RetryerBuilder.<About> newBuilder() //
                .retryIfException() //
                .withStopStrategy(StopStrategies.stopAfterDelay(timeout, unit)) //
                .withWaitStrategy(WaitStrategies.fixedWait(5, TimeUnit.SECONDS)) //
                .build();

        // wait for elastic
        try {
            final About info = retryer.call(() -> {
                return elasticClient.about();
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

    private static Class<?> getJsonView(final VersionComponents version) {
        switch (version.getMajor()) {
        case 5:
            switch (version.getMinor()) {
            case 2:
                return Version52.class;
            case 3:
                return Version53.class;
            case 4:
                return Version54.class;
            case 5:
                return Version55.class;
            case 6:
                return Version56.class;
            default:
                return Version5.class;
            }
        case 6:
            switch (version.getMinor()) {
            case 1:
                return Version61.class;
            default:
                return Version6.class;
            }
        default:
            throw new IllegalStateException("Unsupported version of Elasticsearch: " + version);
        }
    }

    private ElasticClientUtils() {
        // utility class
    }
}
