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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
import com.arakelian.elastic.model.Mget;
import com.arakelian.elastic.model.Nodes;
import com.arakelian.elastic.model.Refresh;
import com.arakelian.faker.model.Person;
import com.arakelian.faker.service.RandomPerson;
import com.arakelian.jackson.utils.JacksonUtils;

public class ElasticClientTest extends AbstractElasticDockerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticClientTest.class);

    private long assertDeleteWithExternalVersion(final Index index, final String id) {
        // delete document with external version
        final long deleteMillis = DateUtils.nowWithZoneUtc().toInstant().toEpochMilli();
        final DeletedDocument deleted = assertSuccessful( //
                getElasticClient().deleteDocument( //
                        index.getName(), //
                        _DOC, //
                        id, //
                        deleteMillis));
        assertEquals(index.getName(), deleted.getIndex());
        // type removed in Elastic 8+
        assertTrue(_DOC.equals(deleted.getType()) || deleted.getType() == null);
        assertEquals(id, deleted.getId());
        assertEquals(id, deleted.getId());
        assertEquals("deleted", deleted.getResult());
        assertEquals(Long.valueOf(deleteMillis), deleted.getVersion());
        assertNotNull(deleted.isFound());
        assertTrue(deleted.isFound().booleanValue());
        return deleteMillis;
    }

    private long assertIndexWithExternalVersion(final Index index, final Person person) {
        // index document
        final long updateMillis = DateUtils.nowWithZoneUtc().toInstant().toEpochMilli();
        final IndexedDocument response = assertSuccessful( //
                getElasticClient().indexDocument( //
                        index.getName(), //
                        _DOC, //
                        person.getId(), //
                        JacksonUtils.toString(person, false), //
                        updateMillis));

        // verify response
        assertEquals(index.getName(), response.getIndex());
        // type removed in Elastic 8+
        assertTrue(_DOC.equals(response.getType()) || response.getType() == null);
        assertEquals(person.getId(), response.getId());
        assertEquals("created", response.getResult());
        assertEquals(Long.valueOf(updateMillis), response.getVersion());
        assertEquals(Boolean.TRUE, response.isCreated());
        return updateMillis;
    }

    @Test
    public void testClusterHealth() {
        final ClusterHealth health = assertSuccessful(
                getElasticClient().clusterHealth(Status.YELLOW, DEFAULT_TIMEOUT));
        LOGGER.info("{}", health);

        assertEquals("docker-cluster", health.getClusterName());
        assertNotEquals("red", health.getStatus());
        assertEquals(Boolean.FALSE, health.getTimedOut());
        assertEquals(Integer.valueOf(1), health.getNumberOfNodes());
        assertEquals(Integer.valueOf(1), health.getNumberOfDataNodes());
        assertNotNull(health.getActiveShardsPercentAsNumber());
    }

    @Test
    public void testDeleteAll() {
        final IndexDeleted response = assertSuccessful(getElasticClient().deleteAllIndexes());
        LOGGER.info("deleteAllIndexes: {}", response);
    }

    @Test
    public void testDeleteNonExistantDocument() throws IOException {
        withPersonIndex(index -> {
            // verify can delete non-existant record
            try {
                getElasticClient().deleteDocument(index.getName(), _DOC, MoreStringUtils.shortUuid());
                Assertions.fail("Delete of non-existant document should have thrown 404");
            } catch (final ElasticNotFoundException e) {
                // successful
            }
        });
    }

    @Test
    public void testDeletes() throws IOException {
        withPersonIndex(index -> {
            final List<Person> people = RandomPerson.get().listOf(10);
            for (final Person person : people) {
                assertIndexWithInternalVersion(index, person, 1);
                assertDeleteWithExternalVersion(index, person.getId());
            }
        });
    }

    @Test
    public void testIndexWithExternalVersion() throws IOException {
        withPersonIndex(index -> {
            final List<Person> people = RandomPerson.get().listOf(10);
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
            final List<Person> people = RandomPerson.get().listOf(10);
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
            final List<Person> people = RandomPerson.get().listOf(10);
            for (final Person person : people) {
                assertIndexWithInternalVersion(index, person, 1);
            }

            // request ids that we just indexed
            final ImmutableMget.Builder builder = ImmutableMget.builder();
            for (final Person person : people) {
                builder.addDoc(
                        ImmutableMgetDocument.builder() //
                                .index(index.getName()) //
                                .type(_DOC) //
                                .id(person.getId()) //
                                .build());
            }

            // request records we know don't exist
            for (int i = 0; i < 10; i++) {
                builder.addDoc(
                        ImmutableMgetDocument.builder() //
                                .index(index.getName()) //
                                .type(_DOC) //
                                .id(MoreStringUtils.shortUuid()) //
                                .build());
            }

            // we should have received response for each record, whether found or not
            final Mget mget = builder.build();
            final Documents documents = assertSuccessful(getElasticClient().getDocuments(mget));
            assertNotNull(documents);
            assertEquals(mget.getDocs().size(), documents.getDocs().size());
        });
    }

    @Test
    public void testNodes() {
        final Nodes response = assertSuccessful(getElasticClient().nodes());
        LOGGER.info("nodes: {}", response);
    }

    @Test
    public void testRefreshAll() {
        final Refresh response = assertSuccessful(getElasticClient().refreshAllIndexes());
        LOGGER.info("refreshAllIndexes: {}", response);
    }
}
