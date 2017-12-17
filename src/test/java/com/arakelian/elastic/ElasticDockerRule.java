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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.docker.junit.Container;
import com.arakelian.docker.junit.DockerRule;
import com.arakelian.docker.junit.ImmutableDockerConfig;
import com.arakelian.elastic.api.About;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.spotify.docker.client.messages.ContainerConfig.Builder;
import com.spotify.docker.client.messages.HostConfig.Ulimit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

public class ElasticDockerRule extends DockerRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticDockerRule.class);

    private ElasticClient elasticClient;

    private About about;

    private String elasticUrl;

    private int port;

    public ElasticDockerRule(final String name, String elasticImage, int port) {
        super(ImmutableDockerConfig.builder() //
                .name(name) //
                .image(elasticImage) //
                .ports(Integer.toString(port)) //
                .build());
        this.port = port;
    }

    @Override
    protected void configureContainer(final Builder builder) {
    }

    @Override
    protected void configureHost(final com.spotify.docker.client.messages.HostConfig.Builder builder) {
        builder.ulimits(
                Lists.newArrayList(
                        Ulimit.builder() //
                                .name("nofile") //
                                .soft(65536L) //
                                .hard(65536L) //
                                .build()));
    }

    public final About getAbout() {
        return about;
    }

    public final ElasticClient getElasticClient() {
        return elasticClient;
    }

    public final String getElasticUrl() {
        return elasticUrl;
    }

    @Override
    public void onStarted(final Container container) throws Exception {
        // there is nothing in the log that we can wait for to indicate that Elastic
        // is really available; even after the logs indicate that it has started, it
        // may not respond to requests for a few seconds. We use /_cluster/health API as
        // a means of more reliably determining availability.
        final int port = container.getPort(Integer.toString(this.port) + "/tcp");
        container.waitForPort(port);

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
        ObjectMapper objectMapper = JacksonUtils.getObjectMapper();
        ElasticClient elasticClient = ElasticClientUtils
                .createElasticClient(client, elasticUrl, objectMapper, null);

        // wait for connection to Elastic
        about = ElasticClientUtils.waitForElasticReady(elasticClient, 1, TimeUnit.MINUTES);
        Assert.assertNotNull("Could not connect to Elasticsearch", about);
        Assert.assertTrue(
                "Requires Elastic 5.x+ but was " + about.getVersion().getNumber(),
                about.getVersion().getMajor() >= 5);

        // create API-specific elastic client
        this.elasticClient = ElasticClientUtils.createElasticClient(client, elasticUrl, objectMapper, about);
    }
}
