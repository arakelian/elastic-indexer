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

import static org.junit.jupiter.params.ParameterizedTest.ARGUMENTS_PLACEHOLDER;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.elastic.model.AbstractElasticModelTest;
import com.arakelian.elastic.model.search.SearchHits.Relation;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class SearchHitsTest extends AbstractElasticModelTest {
    public static final SearchHits SAMPLE = ImmutableSearchHits.builder() //
            .totalObject(
                    ImmutableTotal.builder() //
                            .value(3) //
                            .relation(Relation.GTE) //
                            .build()) //
            .maxScore(3.0f) //
            .addHit(
                    ImmutableSearchHit.builder() //
                            .index("files") //
                            .id("one") //
                            .score(1d) //
                            .source(SourceTest.SAMPLE) //
                            .build()) //
            .addHit(
                    ImmutableSearchHit.builder() //
                            .index("files") //
                            .id("two") //
                            .score(2d) //
                            .source(SourceTest.SAMPLE) //
                            .build()) //
            .addHit(
                    ImmutableSearchHit.builder() //
                            .index("files") //
                            .id("three") //
                            .score(3d) //
                            .source(SourceTest.SAMPLE) //
                            .build()) //
            .build();

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MethodSource("data")
    public void testJackson(String version) throws IOException {
        configure(version);
        JacksonTestUtils.testReadWrite(objectMapper, SAMPLE, SearchHits.class);
    }

    @ParameterizedTest(name = ARGUMENTS_PLACEHOLDER)
    @MethodSource("data")
    public void testSerializable(String version) {
        configure(version);
        SerializableTestUtils.testSerializable(SAMPLE, SearchHits.class);
    }
}
