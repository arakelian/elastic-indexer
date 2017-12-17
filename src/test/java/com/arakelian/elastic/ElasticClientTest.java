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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.api.ClusterHealth;
import com.arakelian.elastic.api.ClusterHealth.Status;
import com.arakelian.elastic.api.DeletedDocument;
import com.arakelian.elastic.api.Documents;
import com.arakelian.elastic.api.Field.Type;
import com.arakelian.elastic.api.ImmutableField;
import com.arakelian.elastic.api.ImmutableMapping;
import com.arakelian.elastic.api.ImmutableMget;
import com.arakelian.elastic.api.ImmutableMget.Builder;
import com.arakelian.elastic.api.ImmutableMgetDocument;
import com.arakelian.elastic.api.Index;
import com.arakelian.elastic.api.IndexDeleted;
import com.arakelian.elastic.api.IndexedDocument;
import com.arakelian.elastic.api.Refresh;
import com.arakelian.fake.model.Person;
import com.arakelian.fake.model.RandomPerson;
import com.arakelian.jackson.utils.JacksonUtils;

public class ElasticClientTest extends AbstractElasticTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticClientTest.class);

    private long assertDeleteWithExternalVersion(final Index index, final String id) throws IOException {
        // delete document with external version
        final long deleteMillis = DateUtils.nowWithZoneUtc().toInstant().toEpochMilli();
        final DeletedDocument deleted = elasticTestUtils.assertSuccessful( //
                DeletedDocument.class, //
                elasticClient.deleteDocument( //
                        index.getName(), //
                        ElasticTestUtils.DEFAULT_TYPE, //
                        id, //
                        deleteMillis) //
                        .execute());
        assertEquals(index.getName(), deleted.getIndex());
        assertEquals(ElasticTestUtils.DEFAULT_TYPE, deleted.getType());
        assertEquals(id, deleted.getId());
        assertEquals(id, deleted.getId());
        assertEquals("deleted", deleted.getResult());
        assertEquals(Long.valueOf(deleteMillis), deleted.getVersion());
        assertNotNull(deleted.isFound());
        assertTrue(deleted.isFound().booleanValue());
        return deleteMillis;
    }

    private long assertIndexWithExternalVersion(final Index index, final Person person) throws IOException {
        // index document
        final long updateMillis = DateUtils.nowWithZoneUtc().toInstant().toEpochMilli();
        final IndexedDocument response = elasticTestUtils.assertSuccessful( //
                IndexedDocument.class, //
                elasticClient.indexDocument( //
                        index.getName(), //
                        ElasticTestUtils.DEFAULT_TYPE, //
                        person.getId(), //
                        JacksonUtils.toString(person, false), //
                        updateMillis) //
                        .execute());

        // verify response
        assertEquals(index.getName(), response.getIndex());
        assertEquals(ElasticTestUtils.DEFAULT_TYPE, response.getType());
        assertEquals(person.getId(), response.getId());
        assertEquals("created", response.getResult());
        assertEquals(Long.valueOf(updateMillis), response.getVersion());
        assertEquals(Boolean.TRUE, response.isCreated());
        return updateMillis;
    }

    private void assertIndexWithInternalVersion(
            final Index index,
            final Person person,
            final long expectedVersion) throws IOException {
        // test default versioning
        final IndexedDocument response = elasticTestUtils.assertSuccessful( //
                IndexedDocument.class, //
                elasticClient.indexDocument(
                        index.getName(), //
                        ElasticTestUtils.DEFAULT_TYPE, //
                        person.getId(), //
                        JacksonUtils.toString(person, false)) //
                        .execute());

        assertEquals(index.getName(), response.getIndex());
        assertEquals(ElasticTestUtils.DEFAULT_TYPE, response.getType());
        assertEquals(person.getId(), response.getId());
        assertEquals("created", response.getResult());
        assertEquals(Long.valueOf(expectedVersion), response.getVersion());
        assertEquals(Boolean.TRUE, response.isCreated());
    }

    @Test
    public void testClusterHealth() throws IOException {
        final ClusterHealth health = elasticTestUtils.assertSuccessful(
                ClusterHealth.class, //
                elasticClient.clusterHealth(Status.YELLOW, DEFAULT_TIMEOUT).execute());
        LOGGER.info("{}", health);

        if (majorVersion > 5) {
            assertEquals("docker-cluster", health.getClusterName());
        } else {
            assertEquals("elasticsearch", health.getClusterName());
        }

        assertNotEquals("red", health.getStatus());
        assertEquals(Boolean.FALSE, health.getTimedOut());
        assertEquals(Integer.valueOf(1), health.getNumberOfNodes());
        assertEquals(Integer.valueOf(1), health.getNumberOfDataNodes());
        assertNotNull(health.getActiveShardsPercentAsNumber());
    }

    @Test
    public void testDeleteAll() throws IOException {
        final IndexDeleted response = elasticTestUtils.assertSuccessful(
                IndexDeleted.class, //
                elasticClient.deleteAllIndexes().execute());
        LOGGER.info("deleteAllIndexes: {}", response);
    }

    @Test
    public void testDeleteNonExistantDocument() throws IOException {
        elasticTestUtils.withPersonIndex(index -> {
            // verify can delete non-existant record
            assertEquals(
                    404,
                    elasticClient.deleteDocument(
                            index.getName(),
                            ElasticTestUtils.DEFAULT_TYPE,
                            MoreStringUtils.shortUuid()).execute().code());
        });
    }

    @Test
    public void testDeletes() throws IOException {
        elasticTestUtils.withPersonIndex(index -> {
            final List<Person> people = RandomPerson.listOf(10);
            for (final Person person : people) {
                assertIndexWithInternalVersion(index, person, 1);
                assertDeleteWithExternalVersion(index, person.getId());
            }
        });
    }

    @Test
    public void testIndexWithExternalVersion() throws IOException {
        elasticTestUtils.withPersonIndex(index -> {
            final List<Person> people = RandomPerson.listOf(10);
            for (final Person person : people) {
                final long version = assertIndexWithExternalVersion(index, person);
                elasticTestUtils.assertGetDocument(index, person, version);
                assertDeleteWithExternalVersion(index, person.getId());
            }
        });
    }

    @Test
    public void testIndexWithInternalVersion() throws IOException {
        elasticTestUtils.withPersonIndex(index -> {
            final List<Person> people = RandomPerson.listOf(10);
            for (final Person person : people) {
                assertIndexWithInternalVersion(index, person, 1);
                elasticTestUtils.assertGetDocument(index, person, Long.valueOf(1));
                final long deleteVersion = assertDeleteWithExternalVersion(index, person.getId());
                assertIndexWithInternalVersion(index, person, deleteVersion + 1);
            }
        });
    }

    @Test
    public void testMappingWithEachFieldType() throws Exception {
        // mappings for each field type will be created, using default values for "store", "index",
        // "doc_values", "include_in_all", and so forth.
        for (final Type type : Type.values()) {
            final ImmutableMapping mapping = ImmutableMapping.builder() //
                    .dynamic(STRICT) //
                    .addFields(
                            ImmutableField.builder() //
                                    .name(ElasticTestUtils.DEFAULT_TYPE) //
                                    .type(type) //
                                    .build())
                    .build();
            elasticTestUtils.withIndex(mapping, index -> {
                // pass if index created
            });
        }
    }

    @Test
    public void testMget() throws IOException {
        elasticTestUtils.withPersonIndex(index -> {
            final List<Person> people = RandomPerson.listOf(10);
            for (final Person person : people) {
                assertIndexWithInternalVersion(index, person, 1);
            }

            // request ids that we just indexed
            final Builder builder = ImmutableMget.builder();
            for (final Person person : people) {
                builder.addDocs(
                        ImmutableMgetDocument.builder() //
                                .index(index.getName()) //
                                .type(ElasticTestUtils.DEFAULT_TYPE) //
                                .id(person.getId()) //
                                .build());
            }

            // request records we know don't exist
            for (int i = 0; i < 10; i++) {
                builder.addDocs(
                        ImmutableMgetDocument.builder() //
                                .index(index.getName()) //
                                .type(ElasticTestUtils.DEFAULT_TYPE) //
                                .id(MoreStringUtils.shortUuid()) //
                                .build());
            }

            // we should have received response for each record, whether found or not
            final ImmutableMget mget = builder.build();
            final Documents documents = elasticTestUtils.assertSuccessful(
                    Documents.class, //
                    elasticClient.getDocuments(mget).execute());
            assertNotNull(documents);
            assertEquals(mget.getDocs().size(), documents.getDocs().size());
        });
    }

    @Test
    public void testRefreshAll() throws IOException {
        final Refresh response = elasticTestUtils.assertSuccessful(
                Refresh.class, //
                elasticClient.refreshAllIndexes().execute());
        LOGGER.info("refreshAllIndexes: {}", response);
    }
}
