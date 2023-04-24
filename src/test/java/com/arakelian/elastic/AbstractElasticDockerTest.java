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

import static com.arakelian.elastic.model.Mapping.Dynamic.STRICT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.elastic.bulk.BulkIndexer;
import com.arakelian.elastic.bulk.BulkIngester;
import com.arakelian.elastic.bulk.ImmutableSimpleBulkOperationFactory;
import com.arakelian.elastic.bulk.SimpleBulkOperationFactory;
import com.arakelian.elastic.bulk.event.IndexerListener;
import com.arakelian.elastic.bulk.event.LoggingIndexerListener;
import com.arakelian.elastic.model.BulkIndexerConfig;
import com.arakelian.elastic.model.Document;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.ImmutableBulkIndexerConfig;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableMapping;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.IndexedDocument;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.elastic.model.search.SearchHits;
import com.arakelian.elastic.model.search.SearchResponse;
import com.arakelian.elastic.model.search.Source;
import com.arakelian.elastic.okhttp.GzipRequestInterceptor;
import com.arakelian.elastic.refresh.DefaultRefreshLimiter;
import com.arakelian.elastic.refresh.ImmutableRefreshLimiterConfig;
import com.arakelian.elastic.refresh.RefreshLimiter;
import com.arakelian.elastic.utils.OkHttpElasticClientUtils;
import com.arakelian.faker.model.Person;
import com.arakelian.faker.service.RandomPerson;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.JsonFilter;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

@Testcontainers
public abstract class AbstractElasticDockerTest extends AbstractElasticTest {
    @FunctionalInterface
    public interface WithPeopleCallback {
        public void accept(Index index, List<Person> people) throws IOException;
    }

    private static final int ELASTICSEARCH_DEFAULT_PORT = 9200;

    /**
     * Field in Elastic index that should not contain any value so that we can test the 'empty'
     * query
     **/
    protected static final String ALWAYS_EMPTY_FIELD = "alwaysEmptyField";

    /** Logger **/
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractElasticDockerTest.class);

    public static GenericContainer elastic;

    public static final String _DOC = Mapping._DOC;

    /**
     * Returns list of Elastic versions that we want to test
     *
     * @return list of Elastic versions that we want to test
     * @see <a href="https://www.docker.elastic.co">Docker Images</a>
     */
    public static Object[] data() {
        return new Object[] { //
                // "5.2.1", //
                // "5.3.3", //
                // "5.4.3", //
                // "5.5.3", //
                // "5.6.16", //
                // "6.0.1", //
                // "6.1.4", //
                // "6.2.4", //
                // "6.3.2", //
                // "6.4.2", //
                // "6.5.4", //
                // "6.6.2", //
                // "6.7.2", //
                // "6.8.2", //
                // "7.0.1", //
                // "7.1.1", //
                // "7.2.1", //
                // "7.3.1", //
                // "7.9.2", //
                // "7.16.3", //
                "7.17.9" };
    }

    protected String elasticUrl;
    protected ElasticClient elasticClient;
    protected ElasticClientWithRetry elasticClientWithRetry;

    private DefaultRefreshLimiter refreshLimiter;

    @SuppressWarnings({ "resource", "StaticAssignmentInConstructor" })
    public AbstractElasticDockerTest() {
        if (elastic == null) {
            elastic = new GenericContainer<>("docker.elastic.co/elasticsearch/elasticsearch:7.17.9") //
                    .withExposedPorts(ELASTICSEARCH_DEFAULT_PORT) //
                    .withEnv("http.host", "0.0.0.0") //
                    .withEnv("discovery.type", "single-node") //
                    .withEnv("transport.host", "127.0.0.1") //
                    .withEnv("xpack.security.enabled", "false") //
                    .withEnv("xpack.monitoring.enabled", "false") //
                    .withEnv("xpack.graph.enabled", "false") //
                    .withEnv("xpack.watcher.enabled", "false") //
                    .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

            elastic.setWaitStrategy(
                    new HttpWaitStrategy().forPort(ELASTICSEARCH_DEFAULT_PORT)
                            .forStatusCodeMatching(
                                    response -> response == HTTP_OK || response == HTTP_UNAUTHORIZED)
                            .withStartupTimeout(Duration.ofMinutes(2)));

            elastic.start();
        }
    }

    public void assertGetDocument(
            final Index index,
            final Person expectedPerson,
            final Long expectedVersion) {
        final Document document = assertSuccessful( //
                elasticClientWithRetry.getDocument( //
                        index.getName(), //
                        _DOC, //
                        expectedPerson.getId(), //
                        null));

        assertEquals(index.getName(), document.getIndex());
        assertEquals(_DOC, document.getType());
        assertEquals(expectedPerson.getId(), document.getId());
        if (expectedVersion != null) {
            assertEquals(expectedVersion, document.getVersion());
        }
        assertTrue(document.isFound());
    }

    protected void assertIndexWithInternalVersion(
            final Index index,
            final Person person,
            final long expectedVersion) {
        // test default versioning
        final IndexedDocument response = assertSuccessful( //
                elasticClient.indexDocument(
                        index.getName(), //
                        _DOC, //
                        person.getId(), //
                        JacksonUtils.toStringSafe(person, false)));

        assertEquals(index.getName(), response.getIndex());
        assertEquals(_DOC, response.getType());
        assertEquals(person.getId(), response.getId());
        assertEquals("created", response.getResult());
        assertEquals(Long.valueOf(expectedVersion), response.getVersion());
        assertEquals(Boolean.TRUE, response.isCreated());
    }

    protected SearchResponse assertSearchFinds(
            final Index index,
            final Search search,
            final long expectedTotal) {
        final SearchResponse response = search(index, search);

        // verify we have a match
        final SearchHits hits = response.getHits();
        final long total = hits.getTotal();
        if (expectedTotal > 0) {
            assertEquals(expectedTotal, total);
        } else {
            final long leastExpected = -expectedTotal;
            assertTrue(
                    total >= leastExpected,
                    "Expected at least " + Long.toString(leastExpected) + " but found " + total);
        }
        return response;
    }

    protected void assertSearchFindsOneOf(final Index index, final Search search, final Person person) {
        final SearchResponse response = assertSearchFinds(index, search, -1);
        final SearchHits hits = response.getHits();

        final String id = person.getId();
        for (int i = 0, size = hits.getSize(); i < size; i++) {
            final Source source = hits.get(i).getSource();
            if (!id.equals(source.getString("id"))) {
                continue;
            }

            assertEquals(id, source.getString("id"));
            assertEquals(person.getFirstName(), source.getString("firstName"));
            assertEquals(person.getLastName(), source.getString("lastName"));
            assertEquals(person.getAge(), source.getInt("age"));
            assertEquals(person.getComments(), source.getString("comments"));
            assertEquals(DateUtils.toStringIsoFormat(person.getBirthdate()), source.getString("birthdate"));
            return;
        }

        fail("Unable to find any match for " + person);
    }

    protected void assertSearchFindsPerson(final Index index, final Search search, final Person person) {
        final SearchResponse response = assertSearchFinds(index, search, 1);
        final SearchHits hits = response.getHits();
        assertEquals(1, hits.getSize());

        final Source source = hits.get(0).getSource();
        assertEquals(person.getId(), source.getString("id"));
        assertEquals(person.getFirstName(), source.getString("firstName"));
        assertEquals(person.getLastName(), source.getString("lastName"));
        assertEquals(person.getAge(), source.getInt("age"));
        assertEquals(person.getComments(), source.getString("comments"));
        assertEquals(DateUtils.toStringIsoFormat(person.getBirthdate()), source.getString("birthdate"));
    }

    public BulkIndexer createIndexer(final IndexerListener listener) {
        final BulkIndexerConfig config = ImmutableBulkIndexerConfig.builder() //
                .blockingQueue(true) //
                .queueSize(10) //
                .maxBulkOperations(10) //
                .maxBulkOperationBytes(1 * 1024 * 1024) //
                .maximumThreads(1) //
                .listener(listener) //
                .shutdownTimeout(1) //
                .shutdownTimeoutUnit(TimeUnit.DAYS) //
                .build();
        return new BulkIndexer(getElasticClient(), config, getRefreshLimiter());
    }

    public SimpleBulkOperationFactory<Person> createPersonBulkOperationFactory(final Index index) {
        final SimpleBulkOperationFactory<Person> bulkOperationFactory = //
                ImmutableSimpleBulkOperationFactory.<Person> builder() //
                        .elasticVersion(person -> elasticClient.getVersion()) //
                        .type(person -> _DOC) //
                        .documentClass(Person.class) //
                        .index(index) //
                        .id(person -> person.getId()) //
                        .version(person -> person.getUpdated()) //
                        .build();
        return bulkOperationFactory;
    }

    public BulkIndexer createPersonIndexer() {
        return createIndexer(LoggingIndexerListener.SINGLETON);
    }

    public BulkIngester createPersonIngester(final Index index, final BulkIndexer bulkIndexer) {
        return new BulkIngester(createPersonBulkOperationFactory(index), bulkIndexer);
    }

    public Mapping createPersonMapping() {
        final Mapping mapping = ImmutableMapping.builder() //
                .dynamic(STRICT) //
                .addField(
                        ImmutableField.builder() //
                                .name("id") //
                                .type(Field.Type.KEYWORD) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("firstName") //
                                .type(Field.Type.TEXT) //
                                .putField(
                                        "raw",
                                        ImmutableField.builder() //
                                                .name("raw") //
                                                .type(Field.Type.KEYWORD) //
                                                .build()) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("lastName") //
                                .type(Field.Type.TEXT) //
                                .putField(
                                        "raw",
                                        ImmutableField.builder() //
                                                .name("raw") //
                                                .type(Field.Type.KEYWORD) //
                                                .build()) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("title") //
                                .type(Field.Type.TEXT) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("birthdate") //
                                .type(Field.Type.DATE) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("age") //
                                .type(Field.Type.INTEGER) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("gender") //
                                .type(Field.Type.KEYWORD) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("comments") //
                                .type(Field.Type.TEXT) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("location") //
                                .type(Field.Type.GEO_POINT) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("shape") //
                                .type(Field.Type.GEO_SHAPE) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name(ALWAYS_EMPTY_FIELD) //
                                .type(Field.Type.KEYWORD) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("created") //
                                .type(Field.Type.DATE) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("updated") //
                                .type(Field.Type.DATE) //
                                .build())
                .build();
        return mapping;
    }

    @AfterEach
    public final void destroyRefreshLimiter() {
        if (refreshLimiter != null) {
            refreshLimiter.close();
        }
    }

    @Override
    protected ElasticClient getElasticClient() {
        return elasticClient;
    }

    @Override
    protected ElasticClientWithRetry getElasticClientWithRetry() {
        return elasticClientWithRetry;
    }

    @Override
    protected String getElasticUrl() {
        return elasticUrl;
    }

    protected final RefreshLimiter getRefreshLimiter() {
        return refreshLimiter;
    }

    protected SearchResponse search(final Index index, final Search search) {
        // make sure index has been refreshed
        elasticClient.refreshIndex(index.getName());

        // perform search
        final SearchResponse result = assertSuccessful(elasticClient.search(index.getName(), search));
        return result;
    }

    @BeforeEach
    public void setupElastic() {
        // there is nothing in the log that we can wait for to indicate that Elastic
        // is really available; even after the logs indicate that it has started, it
        // may not respond to requests for a few seconds. We use /_cluster/health API as
        // a means of more reliably determining availability.
        elasticUrl = "http://" + elastic.getHost() + ":" + elastic.getMappedPort(9200);
        LOGGER.info("Elastic host: {}", elasticUrl);

        final HttpLoggingInterceptor logger = new HttpLoggingInterceptor(message -> {
            if (!StringUtils.isEmpty(message)) {
                final CharSequence pretty = JsonFilter.prettyifyQuietly(message);
                LOGGER.info("{}", pretty);
            }
        });
        logger.level(Level.BODY);

        // configure OkHttp3
        final OkHttpClient client = new OkHttpClient.Builder() //
                .addInterceptor(logger) //
                .addInterceptor(new GzipRequestInterceptor()) //
                .build();

        // create API-specific elastic client
        final VersionComponents version = waitForElasticReady(client);
        elasticClient = OkHttpElasticClientUtils.createElasticClient(
                elasticUrl, //
                client, //
                JacksonUtils.getObjectMapper(), //
                version);
        elasticClientWithRetry = new ElasticClientWithRetry(elasticClient);

        // create refresh limiter
        refreshLimiter = new DefaultRefreshLimiter(ImmutableRefreshLimiterConfig.builder() //
                .coreThreads(1) //
                .maximumThreads(1) //
                .defaultPermitsPerSecond(1.0) //
                .build(), //
                getElasticClient());
    }

    protected void withPeople(final int numberOfPeople, final WithPeopleCallback callback)
            throws IOException {
        withPersonIndex(index -> {
            final List<Person> people = RandomPerson.get().listOf(numberOfPeople);
            for (final Person person : people) {
                assertIndexWithInternalVersion(index, person, 1);
            }
            callback.accept(index, people);
        });
    }

    public void withPersonIndex(final WithIndexCallback test) throws IOException {
        withIndex(createPersonMapping(), test);
    }
}
