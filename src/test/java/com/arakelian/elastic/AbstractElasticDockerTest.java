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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.docker.junit.Container;
import com.arakelian.docker.junit.Container.SimpleBinding;
import com.arakelian.docker.junit.DockerRule;
import com.arakelian.docker.junit.model.DockerConfig;
import com.arakelian.docker.junit.model.ImmutableDockerConfig;
import com.arakelian.elastic.bulk.BulkIndexer;
import com.arakelian.elastic.bulk.BulkIngester;
import com.arakelian.elastic.bulk.ImmutableSimpleBulkOperationFactory;
import com.arakelian.elastic.bulk.SimpleBulkOperationFactory;
import com.arakelian.elastic.bulk.event.IndexerListener;
import com.arakelian.elastic.bulk.event.LoggingIndexerListener;
import com.arakelian.elastic.model.BulkIndexerConfig;
import com.arakelian.elastic.model.Document;
import com.arakelian.elastic.model.Field.Type;
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
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.faker.model.Person;
import com.arakelian.faker.service.RandomPerson;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.JsonFilter;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import repackaged.com.arakelian.docker.junit.com.github.dockerjava.api.model.ExposedPort;
import repackaged.com.arakelian.docker.junit.com.github.dockerjava.api.model.PortBinding;
import repackaged.com.arakelian.docker.junit.com.github.dockerjava.api.model.Ports.Binding;
import repackaged.com.arakelian.docker.junit.com.github.dockerjava.api.model.Ulimit;

@RunWith(Parameterized.class)
public abstract class AbstractElasticDockerTest extends AbstractElasticTest {
    @FunctionalInterface
    public interface WithPeopleCallback {
        public void accept(Index index, List<Person> people) throws IOException;
    }

    /**
     * Field in Elastic index that should not contain any value so that we can test the 'empty'
     * query
     **/
    protected static final String ALWAYS_EMPTY_FIELD = "alwaysEmptyField";

    /** Logger **/
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractElasticDockerTest.class);

    @ClassRule
    public static final DockerRule elastic = new DockerRule();

    public static final String _DOC = Mapping._DOC;

    public static final ExposedPort ELASTIC_PORT = ExposedPort.tcp(9200);

    /**
     * Returns list of Elastic versions that we want to test
     *
     * @return list of Elastic versions that we want to test
     * @see <a href="https://www.docker.elastic.co">Docker Images</a>
     */
    @Parameters(name = "elastic-{0}")
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
                "7.3.1" //
        };
    }

    protected final DockerConfig config;
    protected final String elasticUrl;
    protected final ElasticClient elasticClient;
    protected final ElasticClientWithRetry elasticClientWithRetry;

    protected final Container container;

    public AbstractElasticDockerTest(final String imageVersion) throws Exception {
        final String image = "docker.elastic.co/elasticsearch/elasticsearch:" + imageVersion;

        config = ImmutableDockerConfig.builder() //
                .image(image) //
                .addCreateContainerConfigurer(create -> {
                    create.withExposedPorts(ELASTIC_PORT);
                    create.withEnv(
                            "http.host=0.0.0.0", //
                            "transport.host=127.0.0.1", //
                            "xpack.security.enabled=false", //
                            "xpack.monitoring.enabled=false", //
                            "xpack.graph.enabled=false", //
                            "xpack.watcher.enabled=false", //
                            "ES_JAVA_OPTS=-Xms512m -Xmx512m");
                }) //
                .addHostConfigConfigurer(hostConfig -> {
                    hostConfig.withAutoRemove(true);
                    hostConfig.withPortBindings(new PortBinding(Binding.empty(), ELASTIC_PORT));
                    hostConfig.withUlimits(new Ulimit[] { new Ulimit("nofile", 65536L, 65536L) });
                }) //
                .build();

        // stop containers which are currently running (we don't want to consume too much memory);
        // note that on Docker for Mac, if we run too many containers at the same time old
        // containers will stop with exit code 137.
        final boolean stopOthers = true;

        container = DockerRule.start(config, stopOthers);
        container.addRef();

        boolean success = false;
        try {
            // there is nothing in the log that we can wait for to indicate that Elastic
            // is really available; even after the logs indicate that it has started, it
            // may not respond to requests for a few seconds. We use /_cluster/health API as
            // a means of more reliably determining availability.
            final SimpleBinding binding = container.getSimpleBinding(ELASTIC_PORT);
            elasticUrl = "http://" + binding.getHost() + ":" + binding.getPort();
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
            elasticClient = ElasticClientUtils.createElasticClient(
                    elasticUrl, //
                    client, //
                    JacksonUtils.getObjectMapper(), //
                    version);
            elasticClientWithRetry = new ElasticClientWithRetry(elasticClient);
            success = true;
        } finally {
            if (!success) {
                container.releaseRef();
            }
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
                    "Expected at least " + Long.toString(leastExpected) + " but found " + total,
                    total >= leastExpected);
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
                                .type(Type.KEYWORD) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("firstName") //
                                .type(Type.TEXT) //
                                .putField(
                                        "raw",
                                        ImmutableField.builder() //
                                                .name("raw") //
                                                .type(Type.KEYWORD) //
                                                .build()) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("lastName") //
                                .type(Type.TEXT) //
                                .putField(
                                        "raw",
                                        ImmutableField.builder() //
                                                .name("raw") //
                                                .type(Type.KEYWORD) //
                                                .build()) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("title") //
                                .type(Type.TEXT) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("birthdate") //
                                .type(Type.DATE) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("age") //
                                .type(Type.INTEGER) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("gender") //
                                .type(Type.KEYWORD) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("comments") //
                                .type(Type.TEXT) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("location") //
                                .type(Type.GEO_POINT) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("shape") //
                                .type(Type.GEO_SHAPE) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name(ALWAYS_EMPTY_FIELD) //
                                .type(Type.KEYWORD) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("created") //
                                .type(Type.DATE) //
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("updated") //
                                .type(Type.DATE) //
                                .build())
                .build();
        return mapping;
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

    @After
    public void releaseRef() {
        container.releaseRef();
    }

    protected SearchResponse search(final Index index, final Search search) {
        // make sure index has been refreshed
        elasticClient.refreshIndex(index.getName());

        // perform search
        final SearchResponse result = assertSuccessful(elasticClient.search(index.getName(), search));
        return result;
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
