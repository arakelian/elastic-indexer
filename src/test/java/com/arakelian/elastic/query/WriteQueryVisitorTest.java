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

package com.arakelian.elastic.query;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.model.search.ImmutableBoolQuery;
import com.arakelian.elastic.model.search.ImmutableQueryStringQuery;
import com.arakelian.elastic.model.search.ImmutableSearch;
import com.arakelian.elastic.model.search.ImmutableTermsQuery;
import com.arakelian.elastic.model.search.Query;
import com.arakelian.elastic.model.search.QueryStringQuery;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.elastic.model.search.TermsQuery;
import com.arakelian.elastic.search.WriteSearchVisitor;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.javacrumbs.jsonunit.JsonAssert;

public class WriteQueryVisitorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WriteQueryVisitorTest.class);

    private static final QueryStringQuery QUERY_STRING_QUERY = ImmutableQueryStringQuery.builder() //
            .addField("content", "name") //
            .queryString("this AND that") //
            .build();

    private static final TermsQuery TERMS_QUERY = ImmutableTermsQuery.builder() //
            .fieldName("field") //
            .addValue("the", "quick", "brown", "fox") //
            .build();

    private final ObjectMapper mapper = JacksonUtils.getObjectMapper();

    @Test
    public void testBoolQuery() {
        validateQueryDsl(
                ImmutableBoolQuery.builder() //
                        .addMustClause(TERMS_QUERY) //
                        .addMustClause(TERMS_QUERY) //
                        .addShouldClause(QUERY_STRING_QUERY) //
                        .build(),
                "" + //
                        "{\n" + //
                        "  \"query\" : {\n" + //
                        "    \"bool\" : {\n" + //
                        "      \"must\" : [ {\n" + //
                        "        \"terms\" : {\n" + //
                        "          \"field\" : [ \"brown\", \"fox\", \"quick\", \"the\" ]\n" + //
                        "        }\n" + //
                        "      }, {\n" + //
                        "        \"terms\" : {\n" + //
                        "          \"field\" : [ \"brown\", \"fox\", \"quick\", \"the\" ]\n" + //
                        "        }\n" + //
                        "      } ],\n" + //
                        "      \"should\" : {\n" + //
                        "        \"query_string\" : {\n" + //
                        "          \"fields\" : [ \"content\", \"name\" ],\n" + //
                        "          \"query\" : \"this AND that\"\n" + //
                        "        }\n" + //
                        "      }\n" + //
                        "    }\n" + //
                        "  }\n" + //
                        "}");
    }

    @Test
    public void testQueryStringQuery() {
        validateQueryDsl(WriteQueryVisitorTest.QUERY_STRING_QUERY, "" + //
                "{\n" + //
                "  \"query\" : {\n" + //
                "    \"query_string\" : {\n" + //
                "      \"fields\" : [ \"content\", \"name\" ],\n" + //
                "      \"query\" : \"this AND that\"\n" + //
                "    }\n" + //
                "  }\n" + //
                "}");
    }

    @Test
    public void testTermsQuery() {
        validateQueryDsl(
                WriteQueryVisitorTest.TERMS_QUERY, //
                "" + //
                        "{\n" + //
                        "  \"query\" : {\n" + //
                        "    \"terms\" : {\n" + //
                        "      \"field\" : [ \"brown\", \"fox\", \"quick\", \"the\" ]\n" + //
                        "    }\n" + //
                        "  }\n" + //
                        "}");
    }

    private void validateQueryDsl(final Query query, final String expected) {
        final Search search = ImmutableSearch.builder().query(query).build();

        final String dsl = JacksonUtils.toString(writer -> {
            new WriteSearchVisitor(writer, VersionComponents.of(5, 0)).writeSearch(search);
        }, mapper, true);

        LOGGER.info("Query DSL: {}", dsl);
        JsonAssert.assertJsonEquals(expected, dsl);
    }
}
