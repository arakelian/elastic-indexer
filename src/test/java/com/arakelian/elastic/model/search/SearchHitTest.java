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

package com.arakelian.elastic.model.search;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonUtils;
import com.google.common.collect.ImmutableList;

public class SearchHitTest {
    public static final SearchHit SAMPLE = ImmutableSearchHit.builder() //
            .index("index") //
            .id("id") //
            .score(2.5d) //
            .type("type") //
            .source(
                    ImmutableSource.builder() //
                            .putProperty("id", "id") //
                            .putProperty("name", "Greg Arakelian") //
                            .build()) //
            .build();

    @Test
    public void testExtraProps() throws IOException {
        final String json = "{\n" + //
                "  \"_index\" : \"c9c26c1cbd7e47eb8a85f6c07ddff12d\",\n" + //
                "  \"_type\" : \"test\",\n" + //
                "  \"_id\" : \"random-id\",\n" + //
                "  \"_score\" : 1.0,\n" + //
                "  \"_source\" : {\n" + //
                "    \"id\" : \"random-id\",\n" + //
                "    \"firstName\" : \"GREG\",\n" + //
                "    \"lastName\" : \"ARAKELIAN\",\n" + //
                "    \"gender\" : \"MALE\",\n" + //
                "    \"age\" : 23\n" + //
                "  },\n" + //
                "  \"matched_queries\" : [\n" + //
                "    \"ids_query\"\n" + //
                "  ],\n" + //
                "  \"ARRAY_FIELD\" : [\"a\",\"b\",\"c\"],\n" + //
                "  \"STRING_FIELD\" : \"Extra!\",\n" + //
                "  \"DOUBLE_FIELD\" : 3.14\n" + //
                "}\n";
        final SearchHit hit = JacksonUtils.readValue(json, SearchHit.class);
        Assertions.assertNotNull(hit);
        Assertions.assertTrue(hit.getMatchedQueries().contains("ids_query"));
        Assertions.assertEquals("random-id", hit.getId());
        Assertions.assertEquals(3, hit.getProperties().size());

        // test simple Strings
        Assertions.assertEquals("Extra!", hit.getProperties().get("STRING_FIELD"));
        Assertions.assertEquals("Extra!", hit.get("STRING_FIELD", String.class));

        // test type conversion
        Assertions.assertEquals(Integer.valueOf(3), hit.get("DOUBLE_FIELD", Integer.class));
        Assertions.assertEquals(Double.valueOf(3.14d), hit.get("DOUBLE_FIELD", Double.class));
        Assertions.assertEquals(ImmutableList.of("a", "b", "c"), hit.get("ARRAY_FIELD", List.class));

        // test "first"
        Assertions.assertEquals("Extra!", hit.first("STRING_FIELD", String.class));
        Assertions.assertEquals("a", hit.first("ARRAY_FIELD", String.class));
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(SAMPLE, SearchHit.class);
    }
}
