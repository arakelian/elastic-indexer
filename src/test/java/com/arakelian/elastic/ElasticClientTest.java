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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.model.ClusterHealth;
import com.arakelian.elastic.model.ClusterHealth.Status;
import com.arakelian.elastic.model.DeletedDocument;
import com.arakelian.elastic.model.Documents;
import com.arakelian.elastic.model.ImmutableMget;
import com.arakelian.elastic.model.ImmutableMgetDocument;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.IndexDeleted;
import com.arakelian.elastic.model.IndexedDocument;
import com.arakelian.elastic.model.Refresh;
import com.arakelian.fake.model.Person;
import com.arakelian.fake.model.RandomPerson;
import com.arakelian.jackson.utils.JacksonUtils;

public class ElasticClientTest extends AbstractElasticTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticClientTest.class);

    public ElasticClientTest(final String version) throws Exception {
        super(version);
    }

    private long assertDeleteWithExternalVersion(final Index index, final String id) throws IOException {
        // delete document with external version
        final long deleteMillis = DateUtils.nowWithZoneUtc().toInstant().toEpochMilli();
        final DeletedDocument deleted = assertSuccessful( //
                DeletedDocument.class, //
                elasticClient.deleteDocument( //
                        index.getName(), //
                        DEFAULT_TYPE, //
                        id, //
                        deleteMillis));
        assertEquals(index.getName(), deleted.getIndex());
        assertEquals(DEFAULT_TYPE, deleted.getType());
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
        final IndexedDocument response = assertSuccessful( //
                IndexedDocument.class, //
                elasticClient.indexDocument( //
                        index.getName(), //
                        DEFAULT_TYPE, //
                        person.getId(), //
                        JacksonUtils.toString(person, false), //
                        updateMillis));

        // verify response
        assertEquals(index.getName(), response.getIndex());
        assertEquals(DEFAULT_TYPE, response.getType());
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
        final IndexedDocument response = assertSuccessful( //
                IndexedDocument.class, //
                elasticClient.indexDocument(
                        index.getName(), //
                        DEFAULT_TYPE, //
                        person.getId(), //
                        JacksonUtils.toString(person, false)));

        assertEquals(index.getName(), response.getIndex());
        assertEquals(DEFAULT_TYPE, response.getType());
        assertEquals(person.getId(), response.getId());
        assertEquals("created", response.getResult());
        assertEquals(Long.valueOf(expectedVersion), response.getVersion());
        assertEquals(Boolean.TRUE, response.isCreated());
    }

    @Test
    public void testClusterHealth() throws IOException {
        final ClusterHealth health = assertSuccessful(
                ClusterHealth.class, //
                elasticClient.clusterHealth(Status.YELLOW, DEFAULT_TIMEOUT));
        LOGGER.info("{}", health);

        assertEquals("docker-cluster", health.getClusterName());
        assertNotEquals("red", health.getStatus());
        assertEquals(Boolean.FALSE, health.getTimedOut());
        assertEquals(Integer.valueOf(1), health.getNumberOfNodes());
        assertEquals(Integer.valueOf(1), health.getNumberOfDataNodes());
        assertNotNull(health.getActiveShardsPercentAsNumber());
    }

    @Test
    public void testDeleteAll() throws IOException {
        final IndexDeleted response = assertSuccessful(
                IndexDeleted.class, //
                elasticClient.deleteAllIndexes());
        LOGGER.info("deleteAllIndexes: {}", response);
    }

    @Test
    public void testDeleteNonExistantDocument() throws IOException {
        withPersonIndex(index -> {
            // verify can delete non-existant record
            assertEquals(
                    404,
                    elasticClient.deleteDocument(index.getName(), DEFAULT_TYPE, MoreStringUtils.shortUuid())
                            .code());
        });
    }

    @Test
    public void testDeletes() throws IOException {
        withPersonIndex(index -> {
            final List<Person> people = RandomPerson.listOf(10);
            for (final Person person : people) {
                assertIndexWithInternalVersion(index, person, 1);
                assertDeleteWithExternalVersion(index, person.getId());
            }
        });
    }

    @Test
    public void testIndexWithExternalVersion() throws IOException {
        withPersonIndex(index -> {
            final List<Person> people = RandomPerson.listOf(10);
            for (final Person person : people) {
                final long version = assertIndexWithExternalVersion(index, person);
                assertGetDocument(index, person, version);
                assertDeleteWithExternalVersion(index, person.getId());
            }
        });
    }

    @Test
    public void testIndexWithInternalVersion() throws IOException {
        withPersonIndex(index -> {
            final List<Person> people = RandomPerson.listOf(10);
            for (final Person person : people) {
                assertIndexWithInternalVersion(index, person, 1);
                assertGetDocument(index, person, Long.valueOf(1));
                final long deleteVersion = assertDeleteWithExternalVersion(index, person.getId());
                assertIndexWithInternalVersion(index, person, deleteVersion + 1);
            }
        });
    }

    @Test
    public void testMget() throws IOException {
        withPersonIndex(index -> {
            final List<Person> people = RandomPerson.listOf(10);
            for (final Person person : people) {
                assertIndexWithInternalVersion(index, person, 1);
            }

            // request ids that we just indexed
            final ImmutableMget.Builder builder = ImmutableMget.builder();
            for (final Person person : people) {
                builder.addDoc(
                        ImmutableMgetDocument.builder() //
                                .index(index.getName()) //
                                .type(DEFAULT_TYPE) //
                                .id(person.getId()) //
                                .build());
            }

            // request records we know don't exist
            for (int i = 0; i < 10; i++) {
                builder.addDoc(
                        ImmutableMgetDocument.builder() //
                                .index(index.getName()) //
                                .type(DEFAULT_TYPE) //
                                .id(MoreStringUtils.shortUuid()) //
                                .build());
            }

            // we should have received response for each record, whether found or not
            final ImmutableMget mget = builder.build();
            final Documents documents = assertSuccessful(
                    Documents.class, //
                    elasticClient.getDocuments(mget));
            assertNotNull(documents);
            assertEquals(mget.getDocs().size(), documents.getDocs().size());
        });
    }

    @Test
    public void testRefreshAll() throws IOException {
        final Refresh response = assertSuccessful(
                Refresh.class, //
                elasticClient.refreshAllIndexes());
        LOGGER.info("refreshAllIndexes: {}", response);
    }
}
