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

package com.arakelian.elastic.doc.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.google.common.collect.ImmutableList;

public class SortTest {
    public static final SortFilter SAMPLE = ImmutableSortFilter.builder().build();

    private List<String> test(final String... values) {
        final TokenCollector collector = new TokenCollector();

        // pipe in values
        for (final String v : values) {
            SAMPLE.accept(v, token -> {
                fail("Should not have received a token");
            });
        }

        // flush collector
        return SAMPLE.accept(null, collector).get();
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(SAMPLE, SortFilter.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(SAMPLE, SortFilter.class);
    }

    @Test
    public void testWhitespace() {
        assertEquals(ImmutableList.of("a", "b", "c"), test("c", "b", "a"));
    }
}
