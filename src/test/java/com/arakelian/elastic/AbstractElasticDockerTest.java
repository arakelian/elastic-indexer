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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.docker.junit.Container;
import com.arakelian.docker.junit.DockerRule;
import com.arakelian.docker.junit.model.DockerConfig;
import com.arakelian.docker.junit.model.HostConfigurers;
import com.arakelian.docker.junit.model.ImmutableDockerConfig;
import com.arakelian.elastic.bulk.BulkIndexer;
import com.arakelian.elastic.bulk.DefaultBulkOperationFactory;
import com.arakelian.elastic.bulk.event.IndexerListener;
import com.arakelian.elastic.bulk.event.LoggingIndexerListener;
import com.arakelian.elastic.model.BulkIndexerConfig;
import com.arakelian.elastic.model.Document;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.ImmutableBulkIndexerConfig;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableMapping;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.fake.model.Person;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.JsonFilter;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

@RunWith(Parameterized.class)
public abstract class AbstractElasticDockerTest extends AbstractElasticTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractElasticDockerTest.class);

    @ClassRule
    public static final DockerRule elastic = new DockerRule();

    public static final String DEFAULT_TYPE = "test";

    @Parameters(name = "elastic-{0}")
    public static Object[] data() {
        return new Object[] { //
                "5.2.1", //
                // "5.3.3", //
                // "5.4.3", //
                // "5.5.3", //
                // "5.6.5", //
                // "6.0.1", //
                "6.1.0" //
        };
    }

    protected final DockerConfig config;
    protected final String elasticUrl;
    protected final ElasticClient elasticClient;
    protected final ElasticClientWithRetry elasticClientWithRetry;

    public AbstractElasticDockerTest(final String imageVersion) throws Exception {
        final String name = "elastic-test-" + imageVersion;
        final String image = "docker.elastic.co/elasticsearch/elasticsearch:" + imageVersion;

        config = ImmutableDockerConfig.builder() //
                .name(name) //
                .image(image) //
                .ports("9200") //
                .addHostConfigurer(HostConfigurers.noUlimits()) //
                .addContainerConfigurer(builder -> {
                    builder.env(
                            "http.host=0.0.0.0", //
                            "transport.host=127.0.0.1", //
                            "xpack.security.enabled=false", //
                            "xpack.monitoring.enabled=false", //
                            "xpack.graph.enabled=false", //
                            "xpack.watcher.enabled=false", //
                            "ES_JAVA_OPTS=-Xms512m -Xmx512m");
                }) //
                .build();

        // stop containers which are currently running (we don't want to consume too much memory);
        // note that on Docker for Mac, if we run too many containers at the same time old
        // containers will stop with exit code 137.
        final boolean stopOthers = true;

        // start container that we need
        final Container container = DockerRule.start(config, stopOthers);

        // there is nothing in the log that we can wait for to indicate that Elastic
        // is really available; even after the logs indicate that it has started, it
        // may not respond to requests for a few seconds. We use /_cluster/health API as
        // a means of more reliably determining availability.
        final int port = container.getPort("9200/tcp");
        elasticUrl = "http://" + container.getHost() + ":" + port;
        LOGGER.info("Elastic host: {}", elasticUrl);

        // configure OkHttp3
        final OkHttpClient client = new OkHttpClient.Builder() //
                .addInterceptor( //
                        new HttpLoggingInterceptor(message -> {
                            if (!StringUtils.isEmpty(message)) {
                                final String pretty = JsonFilter.prettyifyQuietly(message);
                                LOGGER.info(pretty.indexOf('\n') == -1 ? "{}" : "\n{}", pretty);
                            }
                        }).setLevel(Level.BODY)) //
                .build();

        // create API-specific elastic client
        final VersionComponents version = waitForElasticReady(client);
        elasticClient = ElasticClientUtils
                .createElasticClient(client, elasticUrl, JacksonUtils.getObjectMapper(), version);
        elasticClientWithRetry = new ElasticClientWithRetry(elasticClient);
    }

    public void assertGetDocument(final Index index, final Person expectedPerson, final Long expectedVersion)
            throws IOException {
        final Document document = assertSuccessful( //
                Document.class, //
                elasticClientWithRetry.getDocument( //
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
                .bulkOperationFactory(new DefaultBulkOperationFactory<>(index, DEFAULT_TYPE)) //
                .listener(listener) //
                .shutdownTimeout(1) //
                .shutdownTimeoutUnit(TimeUnit.DAYS) //
                .build();
        return new BulkIndexer<>(config, getRefreshLimiter());
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
                                .build())
                .addField(
                        ImmutableField.builder() //
                                .name("lastName") //
                                .type(Type.TEXT) //
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

    public void withPersonIndex(final ElasticTestCallback test) throws IOException {
        withIndex(createPersonMapping(), test);
    }
}
