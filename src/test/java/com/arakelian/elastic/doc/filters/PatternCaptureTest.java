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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.google.common.collect.ImmutableList;

public class PatternCaptureTest {
    /**
     * Captures a run of digits. There are no capture groups which implies the entire match is
     * output as token.
     **/
    public static final PatternCapture DIGITS = ImmutablePatternCapture.builder() //
            .addPattern("[0-9]+") //
            .preserveOriginal(false) //
            .build();

    /**
     * Captures the first letter of every word. All of the capture groups in the expression are
     * output as tokens.
     **/
    public static final PatternCapture FIRST_LETTERS = ImmutablePatternCapture.builder() //
            .addPattern("\\b([A-Za-z])(?:[A-Za-z]+)\\b") //
            .preserveOriginal(false) //
            .build();

    @Test
    public void testDigits() {
        final ImmutableList<String> expected = ImmutableList.of("703", "555", "1212");
        Assertions.assertEquals(expected, DIGITS.accept("(703) 555-1212", new TokenCollector()).get());
        Assertions.assertEquals(expected, DIGITS.accept("703/555-1212", new TokenCollector()).get());
        Assertions.assertEquals(expected, DIGITS.accept("703.555.1212", new TokenCollector()).get());
    }

    @Test
    public void testFirstLetters() {
        final ImmutableList<String> expected = ImmutableList.of("a", "b", "c");
        Assertions.assertEquals(
                expected,
                FIRST_LETTERS.accept("aardvard babboon camel", new TokenCollector()).get());
        Assertions.assertEquals(
                expected,
                FIRST_LETTERS.accept("american broadcasting company", new TokenCollector()).get());
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(DIGITS, PatternCapture.class);
        JacksonTestUtils.testReadWrite(FIRST_LETTERS, PatternCapture.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(DIGITS, PatternCapture.class);
        SerializableTestUtils.testSerializable(FIRST_LETTERS, PatternCapture.class);
    }
}
