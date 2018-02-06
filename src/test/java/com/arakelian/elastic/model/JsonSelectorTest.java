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

package com.arakelian.elastic.model;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

public class JsonSelectorTest {
    private static final String JSON = "{\n" + //
            "    \"store\": {\n" + //
            "        \"book\": [\n" + //
            "            {\n" + //
            "                \"category\": \"reference\",\n" + //
            "                \"author\": \"Nigel Rees\",\n" + //
            "                \"title\": \"Sayings of the Century\",\n" + //
            "                \"price\": 8.95\n" + //
            "            },\n" + //
            "            {\n" + //
            "                \"category\": \"fiction\",\n" + //
            "                \"author\": \"Evelyn Waugh\",\n" + //
            "                \"title\": \"Sword of Honour\",\n" + //
            "                \"price\": 12.99\n" + //
            "            },\n" + //
            "            {\n" + //
            "                \"category\": \"fiction\",\n" + //
            "                \"author\": \"Herman Melville\",\n" + //
            "                \"title\": \"Moby Dick\",\n" + //
            "                \"isbn\": \"0-553-21311-3\",\n" + //
            "                \"price\": 8.99\n" + //
            "            },\n" + //
            "            {\n" + //
            "                \"category\": \"fiction\",\n" + //
            "                \"author\": \"J. R. R. Tolkien\",\n" + //
            "                \"title\": \"The Lord of the Rings\",\n" + //
            "                \"isbn\": \"0-395-19395-8\",\n" + //
            "                \"price\": 22.99\n" + //
            "            }\n" + //
            "        ],\n" + //
            "        \"bicycle\": {\n" + //
            "            \"color\": \"red\",\n" + //
            "            \"price\": 19.95\n" + //
            "        }\n" + //
            "    },\n" + //
            "    \"expensive\": 10\n" + //
            "}";

    private Configuration configuration;

    @Before
    public void setupConfiguration() {
        final ObjectMapper mapper = JacksonUtils.getObjectMapper();

        configuration = Configuration.builder() //
                .jsonProvider(new JacksonJsonNodeJsonProvider(mapper)) //
                .mappingProvider(new JacksonMappingProvider(mapper)) //
                .build();
    }

    @Test(expected = InvalidPathException.class)
    public void testInvalidPathDollarDot() {
        Assert.assertEquals("", read("$.").toString());
    }

    @Test(expected = InvalidPathException.class)
    public void testInvalidPathDotDot() {
        Assert.assertEquals("", read("..").toString());
    }

    @Test(expected = InvalidPathException.class)
    public void testInvalidPathEmpty() {
        Assert.assertEquals("", read("").toString());
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(JsonSelector.of("$..somehwere"), JsonSelector.class);
    }

    @Test
    public void testJsonPath() {
        Assert.assertEquals("[8.95,12.99,8.99,22.99]", read("$.store.book..price").toString());
        Assert.assertEquals("[8.95,12.99,8.99,22.99,19.95]", read("$..price").toString());
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(JsonSelector.of("$..somehwere"), JsonSelector.class);
    }

    @Test
    public void testSimplePath() {
        // price is not available at root
        Assert.assertEquals("[]", read("price").toString());

        // simple path traversal
        Assert.assertEquals("[\"red\"]", read("store/bicycle/color").toString());

        // dots are same thing as slashes
        Assert.assertEquals("[\"red\"]", read("store.bicycle.color").toString());

        // collapse multiple dots and slashes
        Assert.assertEquals("[\"red\"]", read("///store///bicycle///color").toString());

        // book[*] is implied
        Assert.assertEquals("[8.95,12.99,8.99,22.99]", read("store/book/price").toString());

        // cannot find .price without .book
        Assert.assertEquals("[]", read("store/price").toString());
    }

    private JsonNode read(final String selector) {
        final JsonPath jsonPath = JsonSelector.of(selector).getJsonPath();
        try {
            return jsonPath.read(JSON, configuration);
        } catch (final PathNotFoundException e) {
            return MissingNode.getInstance();
        }
    }
}
