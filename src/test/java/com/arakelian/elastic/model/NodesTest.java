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

import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class NodesTest extends AbstractElasticModelTest {
    public static final Nodes SAMPLE = ImmutableNodes.builder() //
            .clusterName("cluster") //
            .shards(ShardsTest.MINIMAL) //
            .putNode(
                    "test-hostname",
                    ImmutableNodeInfo.builder() //
                            .name("test") //
                            .transportAddress("127.0.0.1:9300") //
                            .host("127.0.0.1") //
                            .ip("127.0.0.1") //
                            .version("6.1.0") //
                            .buildHash("c0c1ba0") //
                            .http(
                                    ImmutableHttp.builder() //
                                            .addBoundAddress("0.0.0.0:9200") //
                                            .publishAddress("172.17.0.2:9200") //
                                            .maxContentLengthInBytes(104857600) //
                                            .build())
                            .build())
            .build();

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MethodSource("data")
    public void testJackson(String version) throws IOException {
        configure(version);
        JacksonTestUtils.testReadWrite(objectMapper, SAMPLE, Nodes.class);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MethodSource("data")
    public void testSerializable(String version) {
        configure(version);
        SerializableTestUtils.testSerializable(SAMPLE, Nodes.class);
    }
}
