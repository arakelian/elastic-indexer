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
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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

    private String read(final String selector) {
        final JsonNode json;
        try {
            // won't fail
            json = JacksonUtils.readValue(JSON, JsonNode.class);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }

        final JsonNode value = JsonSelector.of(selector).read(json);
        return value.isMissingNode() ? "" : value.toString();
    }

    @Test
    public void testArgumentPaths() {
        final Map<String, List<String>> args = JsonSelector.of("@func a/A, b/B, c/C").getArguments();
        Assertions.assertEquals(ImmutableSet.of("a/A", "b/B", "c/C"), args.keySet());
        Assertions.assertEquals(ImmutableList.of("a", "A"), args.get("a/A"));
        Assertions.assertEquals(ImmutableList.of("b", "B"), args.get("b/B"));
        Assertions.assertEquals(ImmutableList.of("c", "C"), args.get("c/C"));
    }

    @Test
    public void testConcatArguments() {
        Assertions.assertEquals(
                ImmutableSet.of("a", "b", "c"),
                JsonSelector.of("+a,b,c").getArguments().keySet());
        Assertions.assertEquals(
                ImmutableSet.of("a", "b", "c"),
                JsonSelector.of("+ a, b, c").getArguments().keySet());
        Assertions.assertEquals(
                ImmutableSet.of("a/A", "b/B", "c/C"),
                JsonSelector.of("+ a/A, b/B, c/C").getArguments().keySet());
        Assertions.assertEquals(
                ImmutableSet.of("a", "b", "c"),
                JsonSelector.of("+   a , b , c  ").getArguments().keySet());
    }

    @Test
    public void testFunction() {
        // valid function name
        for (final String input : ImmutableSet.of(
                "@func",
                "@FUNC",
                "@Func",
                "@functionName",
                "@function2",
                "@function_name",
                "@_function",
                "@__function",
                "@__")) {
            Assertions.assertTrue(JsonSelector.FUNCTION.matcher(input).matches(), "Failed to match " + input);
        }

        // invalid function name
        for (final String input : ImmutableSet.of(
                "func",
                "@42",
                "@2easy",
                "@ ",
                "@ func",
                "functionName",
                "@func ",
                "@ function_name",
                "@_ function",
                "@__function:",
                " @__")) {
            Assertions
                    .assertFalse(JsonSelector.FUNCTION.matcher(input).matches(), "Should not match " + input);
        }
    }

    @Test
    public void testFunctionArguments() {
        Assertions.assertEquals(
                ImmutableSet.of("a", "b", "c"),
                JsonSelector.of("@func a,b,c").getArguments().keySet());
        Assertions.assertEquals(
                ImmutableSet.of("a", "b", "c"),
                JsonSelector.of("@func a, b, c").getArguments().keySet());
        Assertions.assertEquals(
                ImmutableSet.of("a/A", "b/B", "c/C"),
                JsonSelector.of("@func a/A, b/B, c/C").getArguments().keySet());
        Assertions.assertEquals(
                ImmutableSet.of("a", "b", "c"),
                JsonSelector.of("@func_name   a  , b  , c  ").getArguments().keySet());
    }

    @Test
    public void testFunctionName() {
        Assertions.assertEquals("functionName", JsonSelector.of("@functionName").getFunctionName());
        Assertions.assertEquals("function_name", JsonSelector.of("@function_name").getFunctionName());
    }

    @Test
    public void testInvalidPathDollarDot() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Assertions.assertEquals("", read("$."));
        });
    }

    @Test
    public void testInvalidPathDotDot() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Assertions.assertEquals("", read(".."));
        });
    }

    @Test
    public void testInvalidPathEmpty() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Assertions.assertEquals("", read(""));
        });
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(JsonSelector.of("$..somehwere"), JsonSelector.class);
    }

    @Test
    public void testNormalizeSelector() {
        Assertions.assertEquals(
                "store/bicycle/color",
                JsonSelector.of("///store///bicycle///color").getSelector());
        Assertions.assertEquals(
                "store/bicycle/color", //
                JsonSelector.of("    store..bicycle...color   ").getSelector());
        Assertions.assertEquals(
                "$['store']['book']..['price']", //
                JsonSelector.of("$.store.book..price").getSelector());
        Assertions.assertEquals(
                "+ a, b, c", //
                JsonSelector.of("+a,b,c").getSelector());
        Assertions.assertEquals(
                "+ a/A, b/B, c/C", //
                JsonSelector.of("+a///A,b...B,c././C").getSelector());
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(JsonSelector.of("$..somehwere"), JsonSelector.class);
    }

    @Test
    public void testSimplePath() {
        // price is not available at root
        Assertions.assertEquals("", read("price"));

        // simple path traversal
        Assertions.assertEquals("\"red\"", read("store/bicycle/color"));

        // dots are same thing as slashes
        Assertions.assertEquals("\"red\"", read("store.bicycle.color"));

        // collapse multiple dots and slashes
        Assertions.assertEquals("\"red\"", read("///store///bicycle///color"));

        // book[*] is implied
        Assertions.assertEquals("[8.95,12.99,8.99,22.99]", read("store/book/price"));

        // book[*] is implied
        Assertions.assertEquals(
                "[\"reference\",\"fiction\",\"fiction\",\"fiction\"]",
                read("store/book/category"));

        // cannot find .price without .book
        Assertions.assertEquals("", read("store/price"));
    }
}
