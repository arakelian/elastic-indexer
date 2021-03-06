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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class VersionComponentsTest {
    public static final VersionComponents MINIMAL = ImmutableVersionComponents.builder() //
            .number("2017.10") //
            .major(2017) //
            .minor(10) //
            .build();

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(MINIMAL, VersionComponents.class);
    }

    @Test
    public void testParse() {
        testParse("1", 1, 0, 0);
        testParse("1.25", 1, 25, 0);
        testParse("1.25.10", 1, 25, 10);
        testParse("1.25.RELEASE", 1, 25, 0);
        testParse(null, 0, 0, 0);
        testParse("", 0, 0, 0);
    }

    public void testParse(final String number, final int major, final int minor, final int build) {
        final VersionComponents version = VersionComponents.of(number);
        Assertions.assertEquals(major, version.getMajor());
        Assertions.assertEquals(minor, version.getMinor());
        Assertions.assertEquals(build, version.getBuild());
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(MINIMAL, VersionComponents.class);
    }
}
