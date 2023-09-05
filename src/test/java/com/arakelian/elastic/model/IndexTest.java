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

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IndexTest extends AbstractElasticModelTest {

    private static final String VERSION5 = "{\n" + //
            "  \"settings\" : {\n" + //
            "    \"number_of_replicas\" : 1,\n" + //
            "    \"number_of_shards\" : 5\n" + //
            "  },\n" + //
            "  \"mappings\" : {\n" + //
            "    \"_doc\" : {\n" + //
            "      \"_all\" : {\n" + //
            "        \"enabled\" : true\n" + //
            "      },\n" + //
            "      \"_source\" : {\n" + //
            "        \"enabled\" : true\n" + //
            "      },\n" + //
            "      \"properties\" : {\n" + //
            "        \"name\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"include_in_all\" : true,\n" + //
            "          \"fields\" : {\n" + //
            "            \"raw\" : {\n" + //
            "              \"type\" : \"keyword\",\n" + //
            "              \"store\" : false,\n" + //
            "              \"index\" : true,\n" + //
            "              \"doc_values\" : true,\n" + //
            "              \"ignore_above\" : 256\n" + //
            "            }\n" + //
            "          }\n" + //
            "        },\n" + //
            "        \"street\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"include_in_all\" : true\n" + //
            "        },\n" + //
            "        \"city\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"include_in_all\" : true\n" + //
            "        },\n" + //
            "        \"state\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"include_in_all\" : true\n" + //
            "        },\n" + //
            "        \"zip\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"include_in_all\" : true\n" + //
            "        }\n" + //
            "      }\n" + //
            "    }\n" + //
            "  }\n" + //
            "}" + //
            "";

    private static final String VERSION6 = "{\n" + //
            "  \"settings\" : {\n" + //
            "    \"number_of_replicas\" : 1,\n" + //
            "    \"number_of_shards\" : 5\n" + //
            "  },\n" + //
            "  \"mappings\" : {\n" + //
            "    \"_doc\" : {\n" + //
            "      \"_source\" : {\n" + //
            "        \"enabled\" : true\n" + //
            "      },\n" + //
            "      \"properties\" : {\n" + //
            "        \"name\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"fields\" : {\n" + //
            "            \"raw\" : {\n" + //
            "              \"type\" : \"keyword\",\n" + //
            "              \"store\" : false,\n" + //
            "              \"index\" : true,\n" + //
            "              \"doc_values\" : true,\n" + //
            "              \"ignore_above\" : 256\n" + //
            "            }\n" + //
            "          }\n" + //
            "        },\n" + //
            "        \"street\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true\n" + //
            "        },\n" + //
            "        \"city\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true\n" + //
            "        },\n" + //
            "        \"state\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true\n" + //
            "        },\n" + //
            "        \"zip\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true\n" + //
            "        }\n" + //
            "      }\n" + //
            "    }\n" + //
            "  }\n" + //
            "}" + // //
            "";
    public static final Index MINIMAL = ImmutableIndex.builder() //
            .name("index_name") //
            .putMapping(Mapping._DOC, MappingTest.CONTACT) //
            .build();

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MethodSource("data")
    public void testJackson(final String version) throws IOException {
        configure(version);
        JacksonTestUtils.testReadWrite(objectMapper, MINIMAL, Index.class);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MethodSource("data")
    public void testSerializable(final String version) {
        configure(version);
        SerializableTestUtils.testSerializable(MINIMAL, Index.class);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MethodSource("data")
    public void testWithoutNameSerializer(final String number) throws IOException {
        configure(number);

        final ObjectMapper newMapper = JacksonUtils.getObjectMapper().copy();
        ElasticClientUtils.configureIndexSerialization(newMapper);

        final String withoutName = JsonFilter.prettyify(newMapper.writeValueAsString(MINIMAL)).toString();
        switch (version.getMajor()) {
        case 5:
            assertJsonEquals(VERSION5, withoutName);
            break;
        case 6:
            assertJsonEquals(VERSION6, withoutName);
            break;
        }
    }
}
