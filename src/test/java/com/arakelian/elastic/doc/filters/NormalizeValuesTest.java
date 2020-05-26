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

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.google.common.collect.ImmutableList;

public class NormalizeValuesTest {
    public static enum TestEnum {
        ONE, TWO;

        public static TestEnum fromString(final String value) {
            if ("1".equals(value)) {
                return ONE;
            }
            if ("2".equals(value)) {
                return TWO;
            }
            return null;
        }
    }

    public static final NormalizeValues GENDER = ImmutableNormalizeValues.builder() //
            .putCode("M", "MALE") //
            .putCode("F", "FEMALE") //
            .caseSensitive(false) //
            .build();

    public static final NormalizeValues ENUM = ImmutableNormalizeValues.builder() //
            .factoryClass(TestEnum.class.getName()) //
            .factoryMethod("fromString") //
            .caseSensitive(false) //
            .build();

    private List<String> test(final NormalizeValues tokenFilter, final String input) {
        return tokenFilter.accept(input, new TokenCollector()).get();
    }

    @Test
    public void testCodes() {
        Assert.assertEquals(ImmutableList.of(), test(GENDER, null));
        Assert.assertEquals(ImmutableList.of(), test(GENDER, ""));
        Assert.assertEquals(ImmutableList.of("MALE"), test(GENDER, "M"));
        Assert.assertEquals(ImmutableList.of("MALE"), test(GENDER, "MALE"));
        Assert.assertEquals(ImmutableList.of("FEMALE"), test(GENDER, "F"));
        Assert.assertEquals(ImmutableList.of("FEMALE"), test(GENDER, "feMALE"));
        Assert.assertEquals(ImmutableList.of("OTHER"), test(GENDER, "OTHER"));
        Assert.assertEquals(ImmutableList.of(" "), test(GENDER, " "));
    }

    @Test
    public void testFactory() {
        Assert.assertEquals(ImmutableList.of(), test(ENUM, null));
        Assert.assertEquals(ImmutableList.of(), test(ENUM, ""));
        Assert.assertEquals(ImmutableList.of(" "), test(ENUM, " "));
        Assert.assertEquals(ImmutableList.of("ONE"), test(ENUM, "1"));
        Assert.assertEquals(ImmutableList.of("TWO"), test(ENUM, "2"));
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(GENDER, NormalizeValues.class);
        JacksonTestUtils.testReadWrite(ENUM, NormalizeValues.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(GENDER, NormalizeValues.class);
        SerializableTestUtils.testSerializable(ENUM, NormalizeValues.class);
    }
}
