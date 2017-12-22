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

import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class ReduceWhitespaceTest {
    private final ReduceWhitespace FILTER = ImmutableReduceWhitespace.of();

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(FILTER, ReduceWhitespace.class);
    }

    @Test
    public void testReduceIntraWhitespace() {
        // one space
        assertEquals("h e l l o", FILTER.apply("h e l l o  "));
        assertEquals("h e l l o", FILTER.apply("  h e l l o"));
        assertEquals("h e l l o", FILTER.apply("  h e l l o  "));

        // two spaces
        assertEquals("h e l l o", FILTER.apply("h  e  l  l  o  "));
        assertEquals("h e l l o", FILTER.apply("  h  e  l  l  o"));
        assertEquals("h e l l o", FILTER.apply("  h  e  l  l  o  "));

        // three spaces
        assertEquals("h e l l o", FILTER.apply("h   e   l   l   o  "));
        assertEquals("h e l l o", FILTER.apply("  h   e   l   l   o"));
        assertEquals("h e l l o", FILTER.apply("  h   e   l   l   o  "));

        // random spaces
        assertEquals("h e l l o", FILTER.apply("  h    e     l     l   o"));
        assertEquals("h e l l o", FILTER.apply("        h    e     l     l   o        "));
        assertEquals("h e l l o", FILTER.apply("  h           e     l            l         o"));
    }

    @Test
    public void testReducesLeadingAndTrailing() {
        assertEquals(null, FILTER.apply(null));
        assertEquals("", FILTER.apply(""));
        assertEquals("", FILTER.apply("  "));
        assertEquals("hello", FILTER.apply("  hello"));
        assertEquals("hello", FILTER.apply("hello  "));
        assertEquals("hello", FILTER.apply("  hello  "));
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(FILTER, ReduceWhitespace.class);
    }
}
