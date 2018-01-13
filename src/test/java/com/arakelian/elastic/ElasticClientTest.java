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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
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
import com.arakelian.elastic.model.Nodes;
import com.arakelian.elastic.model.Refresh;
import com.arakelian.elastic.model.search.ImmutableBoolQuery;
import com.arakelian.elastic.model.search.ImmutableIdsQuery;
import com.arakelian.elastic.model.search.ImmutableMatchQuery;
import com.arakelian.elastic.model.search.ImmutablePrefixQuery;
import com.arakelian.elastic.model.search.ImmutableQueryStringQuery;
import com.arakelian.elastic.model.search.ImmutableSearch;
import com.arakelian.elastic.model.search.ImmutableTermsQuery;
import com.arakelian.elastic.model.search.ImmutableWildcardQuery;
import com.arakelian.elastic.model.search.Operator;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.elastic.model.search.SearchHits;
import com.arakelian.elastic.model.search.SearchResponse;
import com.arakelian.fake.model.Person;
import com.arakelian.fake.model.RandomPerson;
import com.arakelian.jackson.utils.JacksonUtils;

import net.javacrumbs.jsonunit.JsonAssert;

public class ElasticClientTest extends AbstractElasticDockerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticClientTest.class);

    public ElasticClientTest(final String version) throws Exception {
        super(version);
    }

    private long assertDeleteWithExternalVersion(final Index index, final String id) {
        // delete document with external version
        final long deleteMillis = DateUtils.nowWithZoneUtc().toInstant().toEpochMilli();
        final DeletedDocument deleted = assertSuccessful( //
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

    private void assertSearchFindsPerson(final Index index, final Search search, final Person person) {
        // make sure index has been refreshed
        elasticClient.refreshIndex(index.getName());

        // perform search
        final SearchResponse result = assertSuccessful(elasticClient.search(index.getName(), search));

        // verify we have a match
        final SearchHits hits = result.getHits();
        assertEquals(1, hits.getTotal());

        final Map<String, Object> hit = hits.getHit(0);
        JsonAssert.assertJsonPartEquals(person.getId(), hit, "_source.id");
        JsonAssert.assertJsonPartEquals(person.getFirstName(), hit, "_source.firstName");
        JsonAssert.assertJsonPartEquals(person.getLastName(), hit, "_source.lastName");
        JsonAssert.assertJsonPartEquals(person.getComments(), hit, "_source.comments");
    }

    @Test
    public void testBoolQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final Person person = people.get(0);

            final ImmutableQueryStringQuery query = ImmutableQueryStringQuery.builder() //
                    .defaultField("lastName") //
                    .queryString(person.getLastName()) //
                    .build();
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableBoolQuery.builder() //
                                            .addMustClause(query) //
                                            .addShouldClause(query, query, query) //
                                            .minimumShouldMatch("1") // .
                                            .build()) //
                            .build(),
                    person);
        });
    }

    @Test
    public void testClusterHealth() {
        final ClusterHealth health = assertSuccessful(
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
    public void testDeleteAll() {
        final IndexDeleted response = assertSuccessful(elasticClient.deleteAllIndexes());
        LOGGER.info("deleteAllIndexes: {}", response);
    }

    @Test
    public void testDeleteNonExistantDocument() throws IOException {
        withPersonIndex(index -> {
            // verify can delete non-existant record
            try {
                elasticClient.deleteDocument(index.getName(), DEFAULT_TYPE, MoreStringUtils.shortUuid());
                Assert.fail("Delete of non-existant document should have thrown 404");
            } catch (final ElasticNotFoundException e) {
                // successful
            }
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
    public void testIdsQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final Person person = people.get(0);
            final Search search = ImmutableSearch.builder() //
                    .query(
                            ImmutableIdsQuery.builder() //
                                    .addValue(person.getId()) //
                                    .build()) //
                    .build();

            assertSearchFindsPerson(index, search, person);
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
    public void testMatchQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final Person person = people.get(0);

            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableMatchQuery.builder() //
                                            .fieldName("lastName") //
                                            .value("find a person whose last name is " + person.getLastName()) //
                                            .operator(Operator.OR) //
                                            .build()) //
                            .build(),
                    person);
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
            final Documents documents = assertSuccessful(elasticClient.getDocuments(mget));
            assertNotNull(documents);
            assertEquals(mget.getDocs().size(), documents.getDocs().size());
        });
    }

    @Test
    public void testNodes() {
        final Nodes response = assertSuccessful(elasticClient.nodes());
        LOGGER.info("nodes: {}", response);
    }

    @Test
    public void testPrefixQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final Person person = people.get(0);
            final String lastname = person.getLastName().toLowerCase();
            final Search search = ImmutableSearch.builder() //
                    .query(
                            ImmutablePrefixQuery.builder() //
                                    .fieldName("lastName") //
                                    .value(StringUtils.left(lastname, lastname.length() - 1)) //
                                    .build()) //
                    .build();

            assertSearchFindsPerson(index, search, person);
        });
    }

    @Test
    public void testQueryStringQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final Person person = people.get(0);

            // use default_field
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableQueryStringQuery.builder() //
                                            .defaultField("lastName") //
                                            .queryString(person.getLastName()) //
                                            .build()) //
                            .build(),
                    person);

            // use complex query
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableQueryStringQuery.builder() //
                                            .queryString("lastName:" + person.getLastName()) //
                                            .boost(2.0f) //
                                            .name("name") //
                                            .build()) //
                            .build(),
                    person);
        });
    }

    @Test
    public void testRefreshAll() {
        final Refresh response = assertSuccessful(elasticClient.refreshAllIndexes());
        LOGGER.info("refreshAllIndexes: {}", response);
    }

    @Test
    public void testTermsQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final Person person = people.get(0);

            // try simple search
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableTermsQuery.builder() //
                                            .fieldName("lastName") //
                                            .addValue(person.getLastName().toLowerCase()) //
                                            .build()) //
                            .build(),
                    person);

            // try again with boost and named query
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableTermsQuery.builder() //
                                            .fieldName("lastName") //
                                            .addValue(person.getLastName().toLowerCase()) //
                                            .boost(2.0f) //
                                            .name("last") //
                                            .build()) //
                            .build(),
                    person);
        });
    }

    @Test
    public void testWildcardQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // standard analyzer forces to lowercase
            final Person person = people.get(0);
            final String lastName = person.getLastName().toLowerCase();
            final Search search = ImmutableSearch.builder() //
                    .query(
                            ImmutableWildcardQuery.builder() //
                                    .fieldName("lastName") //
                                    .value(StringUtils.left(lastName, lastName.length() - 1) + "?") //
                                    .build()) //
                    .build();

            assertSearchFindsPerson(index, search, person);
        });
    }
}
