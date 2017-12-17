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

import static com.arakelian.elastic.api.Mapping.Dynamic.STRICT;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.api.ClusterHealth;
import com.arakelian.elastic.api.ClusterHealth.Status;
import com.arakelian.elastic.api.Document;
import com.arakelian.elastic.api.ElasticError;
import com.arakelian.elastic.api.Field.Type;
import com.arakelian.elastic.api.ImmutableField;
import com.arakelian.elastic.api.ImmutableIndex;
import com.arakelian.elastic.api.ImmutableMapping;
import com.arakelian.elastic.api.Index;
import com.arakelian.elastic.api.IndexCreated;
import com.arakelian.elastic.api.IndexDeleted;
import com.arakelian.elastic.api.Mapping;
import com.arakelian.elastic.api.Refresh;
import com.arakelian.elastic.bulk.BulkIndexer;
import com.arakelian.elastic.bulk.BulkIndexerConfig;
import com.arakelian.elastic.bulk.DefaultBulkOperationFactory;
import com.arakelian.elastic.bulk.ImmutableBulkIndexerConfig;
import com.arakelian.elastic.bulk.event.IndexerListener;
import com.arakelian.elastic.bulk.event.LoggingIndexerListener;
import com.arakelian.elastic.refresh.ImmutableRefreshLimiterConfig;
import com.arakelian.elastic.refresh.RefreshLimiter;
import com.arakelian.fake.model.Person;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.JsonFilter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Preconditions;

import retrofit2.Response;

public class ElasticTestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticTestUtils.class);

    public static final String DEFAULT_TYPE = "test";

    private final ElasticClient elasticClient;
    private final ElasticClientWithRetryer elasticClientRetryer;

    private RefreshLimiter refreshLimiter;

    public ElasticTestUtils(final ElasticClient elasticClient) {
        Preconditions.checkArgument(elasticClient != null, "elasticClient must be non-null");
        this.elasticClient = elasticClient;
        this.elasticClientRetryer = new ElasticClientWithRetryer(elasticClient);
    }

    public void assertCreateIndex(final Index index) throws IOException {
        // verify it does not already exist
        assertIndexNotExists(index);

        // create index
        final IndexCreated response = assertSuccessful(
                IndexCreated.class, //
                elasticClientRetryer.createIndex(index.getName(), index));
        LOGGER.info("Create index response: {}", response);
        assertEquals(Boolean.TRUE, response.getAcknowledged());
        assertEquals(Boolean.TRUE, response.getShardsAcknowledged());
    }

    public void assertDeleteIndex(final String name) throws IOException {
        final IndexDeleted response = assertSuccessful( //
                IndexDeleted.class, //
                elasticClientRetryer.deleteIndex(name));

        LOGGER.info("Delete Index response: {}", response);
        assertEquals(Boolean.TRUE, response.getAcknowledged());
    }

    public void assertGetDocument(final Index index, final Person expectedPerson, final Long expectedVersion)
            throws IOException {
        final Document document = assertSuccessful( //
                Document.class, //
                elasticClientRetryer.getDocument( //
                        index.getName(), //
                        DEFAULT_TYPE, //
                        expectedPerson.getId(), //
                        null));

        assertEquals(index.getName(), document.getIndex());
        assertEquals(DEFAULT_TYPE, document.getType());
        assertEquals(expectedPerson.getId(), document.getId());
        if (expectedVersion != null) {
            assertEquals(expectedVersion, document.getVersion());
        }
        assertTrue(document.isFound());
    }

    public void assertIndexExists(final Index index) throws IOException {
        Response<Void> response = elasticClientRetryer.indexExists(index.getName());
        assertTrue(response.isSuccessful());
    }

    public void assertIndexNotExists(final Index index) throws IOException {
        Response<Void> response = elasticClientRetryer.indexExists(index.getName());
        assertFalse(response.isSuccessful());
    }

    public void assertRefreshIndex(final Index index) throws IOException {
        final Refresh response = assertSuccessful(
                Refresh.class, //
                elasticClientRetryer.refreshIndex(index.getName()));
        LOGGER.info("Refresh response: {}", response);
    }

    public <T> T assertSuccessful(final Class<T> clazz, final Response<T> response) throws IOException {
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

    public BulkIndexer<Person> createIndexer(final Index index) {
        return createIndexer(index, LoggingIndexerListener.SINGLETON);
    }

    public BulkIndexer<Person> createIndexer(final Index index, final IndexerListener listener) {
        final BulkIndexerConfig<Person> config = ImmutableBulkIndexerConfig.<Person> builder() //
                .blockingQueue(true) //
                .queueSize(10) //
                .maxBulkOperations(10) //
                .maxBulkOperationBytes(1 * 1024 * 1024) //
                .maximumThreads(1) //
                .bulkOperationFactory(new DefaultBulkOperationFactory<>(index, ElasticTestUtils.DEFAULT_TYPE)) //
                .listener(listener) //
                .shutdownTimeout(1) //
                .shutdownTimeoutUnit(TimeUnit.DAYS) //
                .build();
        return new BulkIndexer<>(config, refreshLimiter);
    }

    public Mapping createPersonMapping() {
        final Mapping mapping = ImmutableMapping.builder() //
                .dynamic(STRICT) //
                .addFields(
                        ImmutableField.builder() //
                                .name("id") //
                                .type(Type.KEYWORD) //
                                .build())
                .addFields(
                        ImmutableField.builder() //
                                .name("firstName") //
                                .type(Type.TEXT) //
                                .build())
                .addFields(
                        ImmutableField.builder() //
                                .name("lastName") //
                                .type(Type.TEXT) //
                                .build())
                .addFields(
                        ImmutableField.builder() //
                                .name("gender") //
                                .type(Type.KEYWORD) //
                                .build())
                .addFields(
                        ImmutableField.builder() //
                                .name("comments") //
                                .type(Type.TEXT) //
                                .build())
                .addFields(
                        ImmutableField.builder() //
                                .name("created") //
                                .type(Type.DATE) //
                                .build())
                .addFields(
                        ImmutableField.builder() //
                                .name("updated") //
                                .type(Type.DATE) //
                                .build())
                .build();
        return mapping;
    }

    public synchronized RefreshLimiter createRefreshLimiter() {
        if (refreshLimiter == null) {
            refreshLimiter = new RefreshLimiter(ImmutableRefreshLimiterConfig.builder() //
                    .coreThreads(1) //
                    .maximumThreads(1) //
                    .defaultPermitsPerSecond(1.0) //
                    .build(), //
                    elasticClient);
        }
        return refreshLimiter;
    }

    public Index newIndex(final String name, final Mapping mapping) {
        final Index index = ImmutableIndex.builder() //
                .name(name) //
                .putMappings(Mapping._DEFAULT_, mapping) //
                .build();
        return index;
    }

    public void waitForIndexReady(final Index index) throws IOException {
        // wait for it to be available
        final ClusterHealth response = assertSuccessful(
                ClusterHealth.class, //
                elasticClientRetryer.clusterHealthForIndex( //
                        index.getName(), //
                        Status.YELLOW, //
                        DEFAULT_TIMEOUT));
        LOGGER.info("Cluster health response: {}", response);
        assertNotEquals("red", response.getStatus());
        assertEquals(Boolean.FALSE, response.getTimedOut());

        // verify that Elastic tells us it exists
        assertIndexExists(index);
    }

    public void withIndex(final Index index, final ElasticTestCallback test) throws IOException {
        assertCreateIndex(index);
        try {
            waitForIndexReady(index);
            test.doTest(index);
            assertRefreshIndex(index);
        } finally {
            assertDeleteIndex(index.getName());
        }
    }

    public void withIndex(final Mapping mapping, final ElasticTestCallback test) throws IOException {
        final Index index = newIndex(MoreStringUtils.uuid(), mapping);
        withIndex(index, test);
    }

    public void withPersonIndex(final ElasticTestCallback test) throws IOException {
        withIndex(createPersonMapping(), test);
    }
}
