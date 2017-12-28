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

package com.arakelian.elastic.doc.plugins;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.doc.ElasticDocBuilder;
import com.arakelian.elastic.doc.plugin.ComputeDigest;
import com.arakelian.elastic.doc.plugin.ImmutableComputeDigestConfig;
import com.arakelian.elastic.model.ImmutableElasticDocConfig;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableMapping;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.elastic.model.MappingTest;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;

public class ComputeDigestTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeDigestTest.class);

    private static final String CRC_FIELD = "_crc";
    private static final String EXCLUDE_FIELD = "exclude";

    private ElasticDocBuilder builder;

    @Before
    public void createBuilder() throws IOException {
        ComputeDigest plugin = new ComputeDigest(ImmutableComputeDigestConfig.builder() //
                .algorithm("MD5") //
                .fieldName(CRC_FIELD) //
                .addExcludeField(EXCLUDE_FIELD) //
                .build());

        Mapping mapping = ImmutableMapping.builder() //
                .from(MappingTest.CONTACT) //
                .addField(ImmutableField.builder().name(CRC_FIELD).build()) //
                .addField(ImmutableField.builder().name(EXCLUDE_FIELD).build()) //
                .build();
        LOGGER.info("Mapping: \n{}", JacksonUtils.toString(mapping, true));

        builder = new ElasticDocBuilder(ImmutableElasticDocConfig.builder() //
                .addIdentityField("name") //
                .mapping(mapping) //
                .addPlugin(plugin) //
                .build());
    }

    @Test
    public void testSimple() throws IOException {
        JsonNode input = JacksonUtils.getObjectMapper().readTree("{\n" + //
                "   \"name\":\"Greg Arakelian\",\n" + //
                "   \"street\":\"123 Main Street\",\n" + //
                "   \"zip\":\"20001\",\n" + //
                "   \"" + EXCLUDE_FIELD + "\":\"bob smith\"\n" + // not part of MD5!
                "}");
        String output = builder.build(input);

        // verify that only certain fields were included in MD5 computation
        Assert.assertEquals(
                md5("Greg Arakelian", "123 Main Street", "20001"),
                JacksonUtils.getObjectMapper().readTree(output).path(CRC_FIELD).asText());
    }

    protected String md5(String... values) {
        MessageDigest func;
        try {
            func = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        for (String value : values) {
            func.digest(value.getBytes(Charsets.UTF_8));
        }
        return BaseEncoding.base64().omitPadding().encode(func.digest());
    }
}
