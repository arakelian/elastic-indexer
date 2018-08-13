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

public class StripLeadingZeroesTest {
    public static final StripLeadingZeroes WHOLE = ImmutableStripLeadingZeroes.builder() //
            .emitOriginal(false) //
            .build();

    public static final StripLeadingZeroes WORDS = ImmutableStripLeadingZeroes.builder() //
            .pattern("\\s+") //
            .emitOriginal(false) //
            .build();

    private List<String> test(StripLeadingZeroes tokenFilter, final String input) {
        return tokenFilter.accept(input, new TokenCollector()).get();
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(WHOLE, StripLeadingZeroes.class);
        JacksonTestUtils.testReadWrite(WORDS, StripLeadingZeroes.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(WHOLE, StripLeadingZeroes.class);
        SerializableTestUtils.testSerializable(WORDS, StripLeadingZeroes.class);
    }

    @Test
    public void testWhole() {
        Assert.assertEquals(ImmutableList.of(), test(WHOLE, null));
        Assert.assertEquals(ImmutableList.of(), test(WHOLE, ""));
        Assert.assertEquals(ImmutableList.of(), test(WHOLE, " "));
        Assert.assertEquals(ImmutableList.of(), test(WHOLE, "000000"));
        Assert.assertEquals(ImmutableList.of(), test(WHOLE, "   0000000"));
        Assert.assertEquals(ImmutableList.of(), test(WHOLE, "   0000000 0000 000000    000000   "));
        Assert.assertEquals(ImmutableList.of(), test(WHOLE, "0000000   "));
        Assert.assertEquals(ImmutableList.of("12345"), test(WHOLE, "00000012345"));
        Assert.assertEquals(ImmutableList.of("12345 6789"), test(WHOLE, "00000012345 6789"));
    }

    @Test
    public void testWords() {
        Assert.assertEquals(ImmutableList.of(), test(WORDS, null));
        Assert.assertEquals(ImmutableList.of(), test(WORDS, ""));
        Assert.assertEquals(ImmutableList.of(), test(WORDS, " "));
        Assert.assertEquals(ImmutableList.of(), test(WORDS, "00000 0000"));
        Assert.assertEquals(
                ImmutableList.of("UNITED AIRLINES FLIGHT 295"),
                test(WORDS, "UNITED AIRLINES FLIGHT 00295"));
        Assert.assertEquals(ImmutableList.of("1 2 3 4 5"), test(WORDS, "   01 02 03 0004 00005"));
    }
}
