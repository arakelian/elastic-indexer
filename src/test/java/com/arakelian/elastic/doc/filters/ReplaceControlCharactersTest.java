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

public class ReplaceControlCharactersTest {
    public static final ReplaceControlCharacters FILTER = ImmutableReplaceControlCharacters.of();

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(FILTER, ReplaceControlCharacters.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(FILTER, ReplaceControlCharacters.class);
    }

    @Test
    public void testUpper() {
        Assertions.assertEquals(null, FILTER.apply(null));
        Assertions.assertEquals("", FILTER.apply(""));
        Assertions.assertEquals(" ", FILTER.apply(" "));
        Assertions.assertEquals("one two", FILTER.apply("one\u0000two"));
    }
}
