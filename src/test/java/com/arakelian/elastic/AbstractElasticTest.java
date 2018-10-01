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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.model.About;
import com.arakelian.elastic.model.ClusterHealth;
import com.arakelian.elastic.model.ClusterHealth.Status;
import com.arakelian.elastic.model.ImmutableIndex;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.IndexCreated;
import com.arakelian.elastic.model.IndexDeleted;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.elastic.model.Refresh;
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.refresh.ImmutableRefreshLimiterConfig;
import com.arakelian.elastic.refresh.RefreshLimiter;
import com.arakelian.elastic.refresh.DefaultRefreshLimiter;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.jackson.utils.JacksonUtils;

import okhttp3.OkHttpClient;

public abstract class AbstractElasticTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractElasticTest.class);

    public static final String DEFAULT_TYPE = "test";

    private DefaultRefreshLimiter refreshLimiter;

    @Before
    public final void createRefreshLimiter() {
        refreshLimiter = new DefaultRefreshLimiter(ImmutableRefreshLimiterConfig.builder() //
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

    protected final void assertCreateIndex(final Index index) {
        // verify it does not already exist
        assertIndexNotExists(index);

        // create index
        final IndexCreated response = assertSuccessful(
                getElasticClientWithRetry().createIndex(index.getName(), index));
        LOGGER.info("Create index response: {}", response);
        assertEquals(Boolean.TRUE, response.getAcknowledged());
        assertEquals(Boolean.TRUE, response.getShardsAcknowledged());
    }

    protected final void assertDeleteIndex(final String name) {
        final IndexDeleted response = assertSuccessful( //
                getElasticClientWithRetry().deleteIndex(name));

        LOGGER.info("Delete Index response: {}", response);
        assertEquals(Boolean.TRUE, response.getAcknowledged());
    }

    protected final void assertIndexExists(final Index index) throws ElasticException {
        final boolean exists = getElasticClientWithRetry().indexExists(index.getName());
        Assert.assertTrue("Index " + index.getName() + " does not exist", exists);
    }

    protected final void assertIndexNotExists(final Index index) throws ElasticException {
        final boolean exists = getElasticClientWithRetry().indexExists(index.getName());
        Assert.assertFalse("Index " + index.getName() + " exists", exists);
    }

    protected final void assertRefreshIndex(final Index index) {
        final Refresh response = assertSuccessful(getElasticClientWithRetry().refreshIndex(index.getName()));
        LOGGER.info("Refresh response: {}", response);
    }

    protected final <T> T assertSuccessful(final T response) {
        assertNotNull(response);
        return response;
    }

    protected abstract ElasticClient getElasticClient();

    protected abstract ElasticClientWithRetry getElasticClientWithRetry();

    protected abstract String getElasticUrl();

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
                .createElasticClient(getElasticUrl(), client, JacksonUtils.getObjectMapper(), null);

        // wait for connection
        final About about = ElasticClientUtils.waitForElasticReady(elasticClient, 2, TimeUnit.MINUTES);
        Assert.assertNotNull("Could not connect to Elasticsearch", about);

        final VersionComponents version = about.getVersion().getComponents();
        Assert.assertTrue(
                "Requires Elastic 5.x+ but was " + about.getVersion().getNumber(),
                version.getMajor() >= 5);
        return version;
    }

    protected final void waitForIndexReady(final Index index) {
        // wait for it to be available
        final ClusterHealth response = assertSuccessful(
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

    protected final void withIndex(final Index index, final WithIndexCallback test) throws IOException {
        assertCreateIndex(index);
        try {
            waitForIndexReady(index);
            test.accept(index);
            assertRefreshIndex(index);
        } finally {
            assertDeleteIndex(index.getName());
        }
    }

    protected final void withIndex(final Mapping mapping, final WithIndexCallback test) throws IOException {
        final Index index = newIndex(MoreStringUtils.uuid(), mapping);
        withIndex(index, test);
    }
}
