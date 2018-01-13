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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.google.common.collect.ImmutableList;

public class PatternReplaceTest {
    /** Replaces runs of whitespace with a single underscore character **/
    public static final PatternReplace WHITESPACE = ImmutablePatternReplace.builder() //
            .pattern("\\s+") //
            .replacement("_") //
            .build();

    @Test
    public void testCharFilter() {
        assertEquals(null, WHITESPACE.apply(null));
        assertEquals("", WHITESPACE.apply(""));
        assertEquals("hello_there", WHITESPACE.apply("hello there"));
        assertEquals("_h_e_l_l_o_", WHITESPACE.apply("   h    e l l o  "));
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(WHITESPACE, PatternReplace.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(WHITESPACE, PatternReplace.class);
    }

    @Test
    public void testTokenFilter() {
        final ImmutableList<String> expected = ImmutableList.of("_one_two_three_");
        Assert.assertEquals(
                expected,
                WHITESPACE.accept("    one two     three   ", new TokenCollector()).get());
    }
}
