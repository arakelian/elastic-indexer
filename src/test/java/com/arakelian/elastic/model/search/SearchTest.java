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

package com.arakelian.elastic.model.search;

import java.io.IOException;

import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.elastic.model.enums.SearchType;
import com.arakelian.elastic.model.enums.SortOrder;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class SearchTest {
    public static final Search SAMPLE = ImmutableSearch.builder() //
            .from(0) //
            .size(10) //
            .preference("primary") //
            .version(true) //
            .scroll("1m") //
            .sourceFilter(SourceFilter.EXCLUDE_ALL) //
            .searchType(SearchType.DFS_QUERY_THEN_FETCH) //
            .terminateAfter(100) //
            .query(TermsQueryTest.MINIMAL) //
            .addSorts(Sort.of("one"), Sort.of("two", SortOrder.DESC)) //
            .build();

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(SAMPLE, Search.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(SAMPLE, Search.class);
    }
}
