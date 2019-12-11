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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.model.enums.SortMode;
import com.arakelian.elastic.model.enums.SortOrder;
import com.arakelian.elastic.search.WriteSearchVisitor;
import com.arakelian.jackson.model.Jackson;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.google.common.collect.ImmutableList;

import net.javacrumbs.jsonunit.JsonAssert;

public class SortTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SortTest.class);

    public static final Sort FIELD_ONLY = ImmutableSort.builder() //
            .fieldName("field") //
            .build();

    public static final Sort FIELD_ASCENDING = ImmutableSort.builder() //
            .fieldName("field") //
            .order(SortOrder.ASC) //
            .build();

    public static final Sort FIELD_DESCENDING = ImmutableSort.builder() //
            .fieldName("field") //
            .order(SortOrder.DESC) //
            .build();

    public static final Sort FULL = ImmutableSort.builder() //
            .fieldName("field") //
            .order(SortOrder.DESC) //
            .mode(SortMode.MAX) //
            .unmappedType(Type.DATE) //
            .build();

    @SuppressWarnings("MutableConstantField")
    public static final List<Sort> COMPLEX_SORT = ImmutableList.of(
            FIELD_ONLY, //
            FIELD_ASCENDING,
            FIELD_DESCENDING,
            FULL);

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(FULL, Sort.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(FULL, Sort.class);
    }

    @Test
    public void testSorts() {
        final String actual = Jackson.of()
                .toCharSequence(
                        generator -> new WriteSearchVisitor(generator, VersionComponents.of(5, 0))
                                .writeSorts(COMPLEX_SORT))
                .toString();
        LOGGER.info("Search visitor: {}", actual);
        JsonAssert.assertJsonEquals("[\n" + //
                "   \"field\",\n" + //
                "   \"field\",\n" + //
                "   {\n" + //
                "      \"field\":\"desc\"\n" + //
                "   },\n" + //
                "   {\n" + //
                "      \"field\":{\n" + //
                "         \"order\":\"desc\",\n" + //
                "         \"mode\":\"max\",\n" + //
                "         \"unmapped_type\":\"date\"\n" + //
                "      }\n" + //
                "   }\n" + //
                "]", actual);
    }
}
