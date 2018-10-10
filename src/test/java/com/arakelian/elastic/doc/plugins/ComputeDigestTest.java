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

import org.apache.commons.io.input.CharSequenceReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.doc.ElasticDocBuilder;
import com.arakelian.elastic.model.ImmutableElasticDocConfig;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableMapping;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.elastic.model.MappingTest;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;

public class ComputeDigestTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeDigestTest.class);

    private static final String CRC_FIELD = "_crc";
    private static final String EXCLUDE_FIELD = "exclude";

    private Mapping mapping;

    private ComputeDigest plugin;

    @Before
    public void createBuilder() {
        plugin = new ComputeDigest(ImmutableComputeDigestConfig.builder() //
                .algorithm("MD5") //
                .fieldName(CRC_FIELD) //
                .addExcludeField(EXCLUDE_FIELD) //
                .build());

        mapping = ImmutableMapping.builder() //
                .from(MappingTest.CONTACT) //
                .addField(ImmutableField.builder().name(CRC_FIELD).build()) //
                .addField(ImmutableField.builder().name(EXCLUDE_FIELD).build()) //
                .build();

        LOGGER.info("Mapping: \n{}", JacksonUtils.toStringSafe(mapping, true));
    }

    @Test
    public void testName() throws IOException {
        // only include "name" field in Elastic document
        verifyDigest(
                md5("Greg Arakelian"),
                new ElasticDocBuilder(ImmutableElasticDocConfig.builder() //
                        .addPlugin(plugin) //
                        .mapping(mapping) //
                        .addIdentityField("name") //
                        .build()));
    }

    @Test
    public void testNameStreetZip() throws IOException {
        // only include "name" field in Elastic document
        verifyDigest(
                md5("Greg Arakelian", "123 Main Street", "20001"),
                new ElasticDocBuilder(ImmutableElasticDocConfig.builder() //
                        .addPlugin(plugin) //
                        .mapping(mapping) //
                        .addIdentityFields("name", "street", "zip") //
                        .build()));
    }

    private String md5(final String... values) {
        MessageDigest func;
        try {
            func = MessageDigest.getInstance("MD5");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        for (final String value : values) {
            final byte[] bytes = value.getBytes(Charsets.UTF_8);
            func.update(bytes);
        }
        return BaseEncoding.base64().omitPadding().encode(func.digest());
    }

    private void verifyDigest(final String expected, final ElasticDocBuilder builder) throws IOException {
        // try with fields arranged one way in source
        final ObjectMapper mapper = JacksonUtils.getObjectMapper();
        Assert.assertEquals(
                expected,
                mapper //
                        .readTree(new CharSequenceReader(builder.build("{\n" + //
                                "   \"name\":\"Greg Arakelian\",\n" + //
                                "   \"street\":\"123 Main Street\",\n" + //
                                "   \"zip\":\"20001\",\n" + //
                                "   \"" + EXCLUDE_FIELD + "\":\"bob smith\"\n" + //
                                "}")))
                        .path(CRC_FIELD) //
                        .asText());

        // digest should be the same if fields rearranged another way
        Assert.assertEquals(
                expected,
                mapper //
                        .readTree(new CharSequenceReader(builder.build("{\n" + //
                                "   \"" + EXCLUDE_FIELD + "\":\"bob smith\"\n," + //
                                "   \"zip\":\"20001\",\n" + //
                                "   \"street\":\"123 Main Street\",\n" + //
                                "   \"name\":\"Greg Arakelian\"" + //
                                "}")))
                        .path(CRC_FIELD) //
                        .asText());
    }
}
