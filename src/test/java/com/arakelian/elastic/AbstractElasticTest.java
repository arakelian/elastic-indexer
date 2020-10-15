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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
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
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.jackson.utils.JacksonUtils;

import okhttp3.OkHttpClient;

public abstract class AbstractElasticTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractElasticTest.class);

    public static final String DEFAULT_TYPE = "test";

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
        Assertions.assertTrue(exists, "Index " + index.getName() + " does not exist");
    }

    protected final void assertIndexNotExists(final Index index) throws ElasticException {
        final boolean exists = getElasticClientWithRetry().indexExists(index.getName());
        Assertions.assertFalse(exists, "Index " + index.getName() + " exists");
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

    protected final Index newIndex(final String name, final Mapping mapping) {
        final Index index = ImmutableIndex.builder() //
                .name(name) //
                .putMapping(Mapping._DOC, mapping) //
                .build();
        return index;
    }

    @BeforeEach
    public final void startTest(final TestInfo testInfo) {
        LOGGER.info("Starting test: {}", testInfo.getDisplayName());
    }

    protected final VersionComponents waitForElasticReady(final OkHttpClient client) {
        // configure Retrofit
        final ElasticClient elasticClient = ElasticClientUtils
                .createElasticClient(getElasticUrl(), client, JacksonUtils.getObjectMapper(), null);

        // wait for connection
        final About about = ElasticClientUtils.waitForElasticReady(elasticClient, 2, TimeUnit.MINUTES);
        Assertions.assertNotNull(about, "Could not connect to Elasticsearch");

        final VersionComponents version = about.getVersion().getComponents();
        Assertions.assertTrue(
                version.getMajor() >= 5,
                "Requires Elastic 5.x+ but was " + about.getVersion().getNumber());
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
