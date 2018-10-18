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

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.doc.ElasticDocBuilder;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.ImmutableElasticDocConfig;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableMapping;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.ImmutableJsonFilterOptions;
import com.arakelian.json.JsonFilter;

import net.javacrumbs.jsonunit.JsonAssert;

public class HasLengthPluginTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HasLengthPluginTest.class);

    private static final String INDICATOR_FIELD = "_indicator";

    private Mapping mapping;

    private HasLengthPlugin plugin;

    private ElasticDocBuilder docBuilder;

    @Before
    public void createBuilder() {
        plugin = new HasLengthPlugin( //
                ImmutableHasLengthConfig.builder() //
                        .indicator(INDICATOR_FIELD) //
                        .predicate(field -> Boolean.TRUE.equals(field.isDocValues())) //
                        .length(50) //
                        .build());

        mapping = ImmutableMapping.builder() //
                .addField(
                        ImmutableField.builder() //
                                .name(INDICATOR_FIELD) //
                                .type(Type.KEYWORD) //
                                .sortTokens(true) //
                                .build()) //
                .addField(
                        ImmutableField.builder() //
                                .name("name") //
                                .putField(
                                        "raw",
                                        ImmutableField.builder() //
                                                .name("raw") //
                                                .type(Type.KEYWORD) //
                                                .build()) //
                                .build()) //
                .addField(ImmutableField.builder().name("street").type(Type.TEXT).build()) //
                .addField(ImmutableField.builder().name("city").type(Type.KEYWORD).build()) //
                .addField(ImmutableField.builder().name("state").type(Type.KEYWORD).build()) //
                .addField(ImmutableField.builder().name("zip").type(Type.KEYWORD).build()) //
                .build();

        LOGGER.info("Mapping: \n{}", JacksonUtils.toStringSafe(mapping, true));

        docBuilder = new ElasticDocBuilder( //
                ImmutableElasticDocConfig.builder() //
                        .addPlugin(plugin) //
                        .mapping(mapping) //
                        .addIdentityField("name") //
                        .addIdentityField("street") //
                        .addIdentityField("city") //
                        .addIdentityField("state") //
                        .addIdentityField("zip") //
                        .build());
    }

    /**
     * <p>
     * Test lengthy using sample document with these characteristics:
     * </p>
     * <ul>
     * <li>Name is exactly 50 characters, so not lengthy despite docvalues</li>
     * <li>Street is longer than 50 characters, but no docvalues, therefore not lengthy</li>
     * <li>City is longer than 50 characters, has docvalues, so lengthy</li>
     * <li>Zip is 49 characters, therefore not lengthy despite docvalues</li>
     * </ul>
     *
     * @throws IOException
     *             if there is a problem parsing document
     */
    @Test
    public void testLengthyCity() throws IOException {
        assertLengthyEquals( //
                "{\"_indicator\" : [\n" + //
                        "    \"HAS_LENGTH_50\",\n" + //
                        "    \"HAS_LENGTH_50__CITY\"\n" + //
                        "  ]}", //
                "{\n" + //
                        "   \"name\":\"123456789+123456789+123456789+123456789+123456789+\",\n" + //
                        "   \"street\":\"123456789+123456789+123456789+123456789+123456789+123456789\",\n" + //
                        "   \"city\":\"123456789+123456789+123456789+123456789+123456789+123456789\",\n" + //
                        "   \"zip\":\"123456789+123456789+123456789+123456789+123456789\"\n" + //
                        "}");
    }

    /**
     * <p>
     * Test lengthy using sample document with these characteristics:
     * </p>
     * <ul>
     * <li>Name is longer than 50 characters, has docvalues, therefore lengthy</li>
     * <li>Street is longer than 50 characters, but no docvalues, therefore not lengthy</li>
     * <li>City is longer than 50 characters, has docvalues, so lengthy</li>
     * <li>Zip is 49 characters, therefore not lengthy despite docvalues</li>
     * </ul>
     *
     * @throws IOException
     *             if there is a problem parsing document
     */
    @Test
    public void testMultipleLengthy() throws IOException {
        assertLengthyEquals( //
                "{\"_indicator\" : [\n" + //
                        "    \"HAS_LENGTH_50\",\n" + //
                        "    \"HAS_LENGTH_50__CITY\",\n" + //
                        "    \"HAS_LENGTH_50__NAME\"\n" + //
                        "  ]}", //
                "{\n" + //
                        "   \"name\":\"123456789+123456789+123456789+123456789+123456789+123456789\",\n" + //
                        "   \"street\":\"123456789+123456789+123456789+123456789+123456789+123456789\",\n" + //
                        "   \"city\":\"123456789+123456789+123456789+123456789+123456789+123456789\",\n" + //
                        "   \"zip\":\"123456789+123456789+123456789+123456789+123456789\"\n" + //
                        "}");
    }

    private void assertLengthyEquals(final String expected, final String document) throws IOException {
        final CharSequence elasticDoc = docBuilder.build(document);
        LOGGER.info("Input document: {}", JsonFilter.prettyify(document));
        LOGGER.info("Elastic document: {}", JsonFilter.prettyify(elasticDoc));

        JsonAssert.assertJsonEquals(
                expected,
                JsonFilter.filter(
                        elasticDoc,
                        ImmutableJsonFilterOptions.builder() //
                                .addIncludes(INDICATOR_FIELD) //
                                .build())
                        .toString());
    }
}
