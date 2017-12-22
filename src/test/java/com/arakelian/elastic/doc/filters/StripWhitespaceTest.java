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

import org.junit.Assert;
import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class StripWhitespaceTest {
    public static final StripWhitespace FILTER = ImmutableStripWhitespace.of();

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(FILTER, StripWhitespace.class);
    }

    @Test
    public void testOneSpace() {
        // one space
        Assert.assertEquals("hello", FILTER.apply("h e l l o  "));
        Assert.assertEquals("hello", FILTER.apply("  h e l l o"));
        Assert.assertEquals("hello", FILTER.apply("  h e l l o  "));
    }

    @Test
    public void testRandomSpaces() {
        // random spaces
        Assert.assertEquals("hello", FILTER.apply("  h    e     l     l   o"));
        Assert.assertEquals("hello", FILTER.apply("        h    e     l     l   o        "));
        Assert.assertEquals("hello", FILTER.apply("  h           e     l            l         o"));
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(FILTER, StripWhitespace.class);
    }

    @Test
    public void testStripWhitespace() {
        Assert.assertEquals(null, FILTER.apply(null));
        Assert.assertEquals("", FILTER.apply(""));
        Assert.assertEquals("", FILTER.apply("  "));
        Assert.assertEquals("hello", FILTER.apply("  hello"));
        Assert.assertEquals("hello", FILTER.apply("hello  "));
        Assert.assertEquals("hello", FILTER.apply("  hello  "));
    }

    @Test
    public void testThreeSpaces() {
        // three spaces
        Assert.assertEquals("hello", FILTER.apply("h   e   l   l   o  "));
        Assert.assertEquals("hello", FILTER.apply("  h   e   l   l   o"));
        Assert.assertEquals("hello", FILTER.apply("  h   e   l   l   o  "));
    }

    @Test
    public void testTwoSpaces() {
        // two spaces
        Assert.assertEquals("hello", FILTER.apply("h  e  l  l  o  "));
        Assert.assertEquals("hello", FILTER.apply("  h  e  l  l  o"));
        Assert.assertEquals("hello", FILTER.apply("  h  e  l  l  o  "));
    }
}
