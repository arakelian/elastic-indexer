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

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CustomTest {
    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableComplexFilter.class)
    @JsonDeserialize(builder = ImmutableComplexFilter.Builder.class)
    public static abstract class ComplexFilter implements CharFilter {
        @Override
        public String apply(final String value) {
            return StringUtils.replaceChars(value, getStripChars(), "");
        }

        public abstract String getStripChars();
    }

    public static class SimpleFilter implements CharFilter {
        @Override
        public String apply(final String value) {
            return StringUtils.lowerCase(value);
        }
    }

    public static final Custom SIMPLE = ImmutableCustom.builder() //
            .className(SimpleFilter.class.getName()) //
            .build();

    public static final Custom COMPLEX = ImmutableCustom.builder() //
            .className(ComplexFilter.class.getName()) //
            .arguments(ImmutableMap.of("stripChars", "AEIOUaeiou")) //
            .build();

    public static final Custom INVALID = ImmutableCustom.builder() //
            .className("com.arakelian.does.not.Exist") //
            .build();

    @Test
    public void testComplexCharFilter() {
        assertEquals("hll wrld", COMPLEX.apply("hello world"));
    }

    @Test
    public void testComplexTokenFilter() {
        Assertions.assertEquals(
                ImmutableList.of("wlcm"),
                COMPLEX.accept("welcome", new TokenCollector()).get());
    }

    @Test
    public void testFailure() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            INVALID.apply("LOWERCASE");
        });
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(SIMPLE, Custom.class);
        JacksonTestUtils.testReadWrite(COMPLEX, Custom.class);
        JacksonTestUtils.testReadWrite(INVALID, Custom.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(SIMPLE, Custom.class);
        SerializableTestUtils.testSerializable(COMPLEX, Custom.class);
        SerializableTestUtils.testSerializable(INVALID, Custom.class);
    }

    @Test
    public void testSimpleCharFilter() {
        assertEquals("lowercase", SIMPLE.apply("LOWERCASE"));
    }

    @Test
    public void testSimpleTokenFilter() {
        Assertions.assertEquals(
                ImmutableList.of("lowercase"),
                SIMPLE.accept("LOWERCASE", new TokenCollector()).get());
    }
}
