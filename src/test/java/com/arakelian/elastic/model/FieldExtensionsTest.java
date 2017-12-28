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

import java.io.IOException;

import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.elastic.doc.filters.ImmutableLowercase;
import com.arakelian.elastic.doc.filters.ImmutableNormalizePunctuation;
import com.arakelian.elastic.doc.filters.ImmutableNullFilter;
import com.arakelian.elastic.doc.filters.ImmutableReduceWhitespace;
import com.arakelian.elastic.doc.filters.ImmutableStripWhitespace;
import com.arakelian.elastic.doc.filters.ImmutableTrimWhitespace;
import com.arakelian.elastic.doc.filters.ImmutableUppercase;
import com.arakelian.elastic.doc.filters.LengthFilterTest;
import com.arakelian.elastic.doc.filters.PatternCaptureTest;
import com.arakelian.elastic.doc.filters.PatternReplaceTest;
import com.arakelian.elastic.doc.filters.SplitterTest;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class FieldExtensionsTest {
    public static final Field MINIMAL = ImmutableField.builder() //
            .name("name") //
            .addTokenFilter(ImmutableLowercase.of()) //
            .addTokenFilter(ImmutableUppercase.of()) //
            .addTokenFilter(ImmutableNormalizePunctuation.of()) //
            .addTokenFilter(PatternReplaceTest.WHITESPACE) //
            .addTokenFilter(ImmutableReduceWhitespace.of()) //
            .addTokenFilter(ImmutableStripWhitespace.of()) //
            .addTokenFilter(ImmutableTrimWhitespace.of()) //
            .addTokenFilter(LengthFilterTest.LEN_2) //
            .addTokenFilter(LengthFilterTest.LEN_3) //
            .addTokenFilter(LengthFilterTest.MIN_2) //
            .addTokenFilter(PatternCaptureTest.DIGITS) //
            .addTokenFilter(PatternCaptureTest.FIRST_LETTERS) //
            .addTokenFilter(SplitterTest.WHITESPACE) //
            .addTokenFilter(ImmutableNullFilter.of()) //
            .build();

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(MINIMAL, Field.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(MINIMAL, Field.class);
    }
}
