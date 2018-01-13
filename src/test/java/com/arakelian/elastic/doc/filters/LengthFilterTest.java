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
import com.google.common.collect.ImmutableList;

public class LengthFilterTest {
    /** Token must be exactly 2 characters long **/
    public static final LengthFilter LEN_2 = ImmutableLengthFilter.builder() //
            .minimum(2) //
            .maximum(2) //
            .build();

    /** Token must be exactly 3 characters long **/
    public static final LengthFilter LEN_3 = ImmutableLengthFilter.builder() //
            .minimum(3) //
            .maximum(3) //
            .build();

    /** Token must be at least 2 characters long **/
    public static final LengthFilter MIN_2 = ImmutableLengthFilter.builder() //
            .minimum(2) //
            .build();

    @Test
    public void testEmpty() {
        Assert.assertEquals(ImmutableList.of(), LEN_2.accept(null, new TokenCollector()).get());
        Assert.assertEquals(ImmutableList.of(), LEN_2.accept("", new TokenCollector()).get());
    }

    @Test
    public void testIso2() {
        Assert.assertEquals(ImmutableList.of(), LEN_2.accept("u", new TokenCollector()).get());
        Assert.assertEquals(ImmutableList.of(), LEN_2.accept("usa", new TokenCollector()).get());
        Assert.assertEquals(ImmutableList.of(), LEN_2.accept("united states", new TokenCollector()).get());
        Assert.assertEquals(ImmutableList.of("us"), LEN_2.accept("us", new TokenCollector()).get());
    }

    @Test
    public void testIso3() {
        Assert.assertEquals(ImmutableList.of(), LEN_3.accept("us", new TokenCollector()).get());
        Assert.assertEquals(ImmutableList.of(), LEN_3.accept("usaa", new TokenCollector()).get());
        Assert.assertEquals(ImmutableList.of("usa"), LEN_3.accept("usa", new TokenCollector()).get());
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(LEN_2, LengthFilter.class);
        JacksonTestUtils.testReadWrite(LEN_3, LengthFilter.class);
        JacksonTestUtils.testReadWrite(MIN_2, LengthFilter.class);
    }

    @Test
    public void testMinimum2() {
        Assert.assertEquals(ImmutableList.of(), MIN_2.accept(null, new TokenCollector()).get());
        Assert.assertEquals(ImmutableList.of(), MIN_2.accept("", new TokenCollector()).get());
        Assert.assertEquals(ImmutableList.of(), MIN_2.accept("h", new TokenCollector()).get());
        Assert.assertEquals(ImmutableList.of("hel"), MIN_2.accept("hel", new TokenCollector()).get());
        Assert.assertEquals(ImmutableList.of("hello"), MIN_2.accept("hello", new TokenCollector()).get());
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(LEN_2, LengthFilter.class);
        SerializableTestUtils.testSerializable(LEN_3, LengthFilter.class);
        SerializableTestUtils.testSerializable(MIN_2, LengthFilter.class);
    }
}
