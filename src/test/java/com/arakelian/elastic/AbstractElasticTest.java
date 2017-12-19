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

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
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
import com.arakelian.elastic.model.About;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

@RunWith(Parameterized.class)
public abstract class AbstractElasticTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractElasticTest.class);

    @ClassRule
    public static final DockerRule elastic = new DockerRule();

    @Parameters(name = "elastic-{0}")
    public static Object[] data() {
        return new Object[] { //
                // "5.2.1", //
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
    protected final ElasticTestUtils elasticTestUtils;
    protected final About about;
    protected final int majorVersion;

    public AbstractElasticTest(final String version) throws Exception {
        final String name = "elastic-test-" + version;
        final String image = "docker.elastic.co/elasticsearch/elasticsearch:" + version;

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

        // configure Retrofit
        final ObjectMapper objectMapper = JacksonUtils.getObjectMapper();
        final ElasticClient elasticClient = ElasticClientUtils
                .createElasticClient(client, elasticUrl, objectMapper, null);

        // wait for connection to Elastic
        about = ElasticClientUtils.waitForElasticReady(elasticClient, 2, TimeUnit.MINUTES);
        Assert.assertNotNull("Could not connect to Elasticsearch", about);
        Assert.assertTrue(
                "Requires Elastic 5.x+ but was " + about.getVersion().getNumber(),
                about.getVersion().getMajor() >= 5);

        // create API-specific elastic client
        this.elasticClient = ElasticClientUtils.createElasticClient(client, elasticUrl, objectMapper, about);
        this.elasticTestUtils = new ElasticTestUtils(elasticClient);
        this.majorVersion = about.getVersion().getMajor();
    }
}
