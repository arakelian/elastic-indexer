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

package com.arakelian.elastic;

import static com.arakelian.elastic.utils.ElasticClientUtils.DEFAULT_TIMEOUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.model.About;
import com.arakelian.elastic.model.ClusterHealth;
import com.arakelian.elastic.model.ClusterHealth.Status;
import com.arakelian.elastic.model.ElasticError;
import com.arakelian.elastic.model.ImmutableIndex;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.IndexCreated;
import com.arakelian.elastic.model.IndexDeleted;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.elastic.model.Refresh;
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.refresh.ImmutableRefreshLimiterConfig;
import com.arakelian.elastic.refresh.RefreshLimiter;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.JsonFilter;
import com.fasterxml.jackson.databind.JsonMappingException;

import okhttp3.OkHttpClient;
import retrofit2.Response;

public abstract class AbstractElasticTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractElasticTest.class);

    public static final String DEFAULT_TYPE = "test";

    protected abstract String getElasticUrl();

    protected abstract ElasticClient getElasticClient();

    protected abstract ElasticClientWithRetry getElasticClientWithRetry();

    private RefreshLimiter refreshLimiter;

    protected final void assertCreateIndex(final Index index) throws IOException {
        // verify it does not already exist
        assertIndexNotExists(index);

        // create index
        final IndexCreated response = assertSuccessful(
                IndexCreated.class, //
                getElasticClientWithRetry().createIndex(index.getName(), index));
        LOGGER.info("Create index response: {}", response);
        assertEquals(Boolean.TRUE, response.getAcknowledged());
        assertEquals(Boolean.TRUE, response.getShardsAcknowledged());
    }

    protected final void assertDeleteIndex(final String name) throws IOException {
        final IndexDeleted response = assertSuccessful( //
                IndexDeleted.class, //
                getElasticClientWithRetry().deleteIndex(name));

        LOGGER.info("Delete Index response: {}", response);
        assertEquals(Boolean.TRUE, response.getAcknowledged());
    }

    protected final void assertIndexExists(final Index index) throws ElasticException {
        final Response<Void> response = getElasticClientWithRetry().indexExists(index.getName());
        assertTrue(response.isSuccessful());
    }

    protected final void assertIndexNotExists(final Index index) throws ElasticException {
        final Response<Void> response = getElasticClientWithRetry().indexExists(index.getName());
        assertFalse(response.isSuccessful());
    }

    protected final void assertRefreshIndex(final Index index) throws IOException {
        final Refresh response = assertSuccessful(
                Refresh.class, //
                getElasticClientWithRetry().refreshIndex(index.getName()));
        LOGGER.info("Refresh response: {}", response);
    }

    protected final <T> T assertSuccessful(final Class<T> clazz, final Response<T> response)
            throws IOException {
        final boolean successful = response.isSuccessful();
        if (successful) {
            final T result = response.body();
            assertNotNull(result);
            return result;
        }

        final String body = response.errorBody().string();
        if (response.code() == 404) {
            // retrofit treats 404 like error but they are not as far as Elastic is concerned
            final T result = StringUtils.isEmpty(body) ? null
                    : JacksonUtils.getJsonProcessors().readValue(body, clazz);
            assertNotNull(result);
            return result;
        }

        final ElasticError error;
        try {
            error = StringUtils.isEmpty(body) ? null
                    : JacksonUtils.getJsonProcessors().readValue(body, ElasticError.class);
        } catch (final JsonMappingException e) {
            LOGGER.error("Expecting error response: {}", JsonFilter.prettyify(body));
            throw new IllegalStateException("Unable to deserialize non-successful response (status code: "
                    + response.code() + ") as an Error", e);
        }

        assertNotNull(error);
        fail("Elastic fail called: " + error);
        return null;
    }

    @Before
    public final void createRefreshLimiter() {
        refreshLimiter = new RefreshLimiter(ImmutableRefreshLimiterConfig.builder() //
                .coreThreads(1) //
                .maximumThreads(1) //
                .defaultPermitsPerSecond(1.0) //
                .build(), //
                getElasticClient());
    }

    @After
    public final void destroyRefreshLimiter() {
        refreshLimiter.close();
    }

    protected final RefreshLimiter getRefreshLimiter() {
        return refreshLimiter;
    }

    protected final Index newIndex(final String name, final Mapping mapping) {
        final Index index = ImmutableIndex.builder() //
                .name(name) //
                .putMapping(Mapping._DEFAULT_, mapping) //
                .build();
        return index;
    }

    protected final VersionComponents waitForElasticReady(final OkHttpClient client) {
        // configure Retrofit
        final ElasticClient elasticClient = ElasticClientUtils
                .createElasticClient(client, getElasticUrl(), JacksonUtils.getObjectMapper(), null);

        // wait for connection
        final About about = ElasticClientUtils.waitForElasticReady(elasticClient, 2, TimeUnit.MINUTES);
        Assert.assertNotNull("Could not connect to Elasticsearch", about);

        final VersionComponents version = about.getVersion().getComponents();
        Assert.assertTrue(
                "Requires Elastic 5.x+ but was " + about.getVersion().getNumber(),
                version.getMajor() >= 5);
        return version;
    }

    protected final void waitForIndexReady(final Index index) throws IOException {
        // wait for it to be available
        final ClusterHealth response = assertSuccessful(
                ClusterHealth.class, //
                getElasticClientWithRetry().clusterHealthForIndex( //
                        index.getName(), //
                        Status.YELLOW, //
                        DEFAULT_TIMEOUT));
        LOGGER.info("Cluster health response: {}", response);
        assertNotEquals("red", response.getStatus());
        assertEquals(Boolean.FALSE, response.getTimedOut());

        // verify that Elastic tells us it exists
        assertIndexExists(index);
    }

    protected final void withIndex(final Index index, final ElasticTestCallback test) throws IOException {
        assertCreateIndex(index);
        try {
            waitForIndexReady(index);
            test.doTest(index);
            assertRefreshIndex(index);
        } finally {
            assertDeleteIndex(index.getName());
        }
    }

    protected final void withIndex(final Mapping mapping, final ElasticTestCallback test) throws IOException {
        final Index index = newIndex(MoreStringUtils.uuid(), mapping);
        withIndex(index, test);
    }
}
