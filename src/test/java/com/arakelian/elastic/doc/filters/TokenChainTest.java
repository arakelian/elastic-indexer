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

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class TokenChainTest {
    @Test
    public void testEmpty() {
        Assert.assertEquals(
                ImmutableList.of("pass through"),
                TokenChain.link(ImmutableList.of()).accept("pass through", new TokenCollector()).get());
        Assert.assertEquals(
                ImmutableList.of("pass through"),
                TokenChain.link(null).accept("pass through", new TokenCollector()).get());
    }

    @Test
    public void testFourLinks() {
        final TokenFilter filter = TokenChain.link(
                ImmutableList.of(
                        UppercaseTest.FILTER,
                        SplitterTest.WHITESPACE,
                        LengthFilterTest.LEN_3,
                        PatternCaptureTest.DIGITS));

        Assert.assertEquals(
                ImmutableList.of("23", "12", "123", "45"),
                filter.accept("one two three four f23 g12 123 45X", new TokenCollector()).get());
    }

    @Test
    public void testOneLink() {
        final TokenFilter filter = TokenChain.link(ImmutableList.of(LowercaseTest.FILTER));
        Assert.assertEquals(
                ImmutableList.of("one two three four five six"),
                filter.accept("ONE TWO THREE FOUR FIVE SIX", new TokenCollector()).get());
    }

    @Test
    public void testTwoLinks() {
        final TokenFilter filter = TokenChain
                .link(ImmutableList.of(SplitterTest.WHITESPACE, UppercaseTest.FILTER));
        Assert.assertEquals(
                ImmutableList.of("ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX"),
                filter.accept("one two three four five six", new TokenCollector()).get());
    }
}
