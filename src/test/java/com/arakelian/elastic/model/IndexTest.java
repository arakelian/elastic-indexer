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

import java.io.IOException;

import org.junit.Test;

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
            "    \"_default_\" : {\n" + //
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
            "          \"doc_values\" : false,\n" + //
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
            "          \"doc_values\" : false,\n" + //
            "          \"include_in_all\" : true\n" + //
            "        },\n" + //
            "        \"city\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"doc_values\" : false,\n" + //
            "          \"include_in_all\" : true\n" + //
            "        },\n" + //
            "        \"state\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"doc_values\" : false,\n" + //
            "          \"include_in_all\" : true\n" + //
            "        },\n" + //
            "        \"zip\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"doc_values\" : false,\n" + //
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
            "    \"_default_\" : {\n" + //
            "      \"_source\" : {\n" + //
            "        \"enabled\" : true\n" + //
            "      },\n" + //
            "      \"properties\" : {\n" + //
            "        \"name\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"doc_values\" : false,\n" + //
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
            "          \"doc_values\" : false\n" + //
            "        },\n" + //
            "        \"city\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"doc_values\" : false\n" + //
            "        },\n" + //
            "        \"state\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"doc_values\" : false\n" + //
            "        },\n" + //
            "        \"zip\" : {\n" + //
            "          \"type\" : \"text\",\n" + //
            "          \"store\" : false,\n" + //
            "          \"index\" : true,\n" + //
            "          \"doc_values\" : false\n" + //
            "        }\n" + //
            "      }\n" + //
            "    }\n" + //
            "  }\n" + //
            "}" + // //
            "";
    public static final Index MINIMAL = ImmutableIndex.builder() //
            .name("index_name") //
            .putMapping(Mapping._DEFAULT_, MappingTest.CONTACT) //
            .build();

    public IndexTest(final String number) {
        super(number);
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(objectMapper, MINIMAL, Index.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(MINIMAL, Index.class);
    }

    @Test
    public void testWithoutNameSerializer() throws IOException {
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
