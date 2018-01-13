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

package com.arakelian.elastic.model.query;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import org.immutables.value.Value;

import com.arakelian.elastic.query.ElasticQueryDslVisitor;
import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonTypeInfo(property = "type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({ //
        @JsonSubTypes.Type(name = QueryClause.BOOL_QUERY, value = BoolQuery.class),
        @JsonSubTypes.Type(name = QueryClause.EXISTS_QUERY, value = ExistsQuery.class), //
        @JsonSubTypes.Type(name = QueryClause.FUZZY_QUERY, value = FuzzyQuery.class), //
        @JsonSubTypes.Type(name = QueryClause.IDS_QUERY, value = IdsQuery.class), //
        @JsonSubTypes.Type(name = QueryClause.MATCH_QUERY, value = MatchQuery.class), //
        @JsonSubTypes.Type(name = QueryClause.PREFIX_QUERY, value = PrefixQuery.class), //
        @JsonSubTypes.Type(name = QueryClause.QUERY_STRING_QUERY, value = QueryStringQuery.class), //
        @JsonSubTypes.Type(name = QueryClause.RANGE_QUERY, value = RangeQuery.class), //
        @JsonSubTypes.Type(name = QueryClause.REGEXP_QUERY, value = RegexpQuery.class), //
        @JsonSubTypes.Type(name = QueryClause.TERMS_QUERY, value = TermsQuery.class), //
        @JsonSubTypes.Type(name = QueryClause.WILDCARD_QUERY, value = WildcardQuery.class) //
})
public interface QueryClause extends Serializable {
    public static final String BOOL_QUERY = "bool";
    public static final String EXISTS_QUERY = "exists";
    public static final String FUZZY_QUERY = "fuzzy";
    public static final String IDS_QUERY = "ids";
    public static final String MATCH_QUERY = "match";
    public static final String PREFIX_QUERY = "prefix";
    public static final String QUERY_STRING_QUERY = "query_string";
    public static final String RANGE_QUERY = "range";
    public static final String REGEXP_QUERY = "regexp";
    public static final String TERMS_QUERY = "terms";
    public static final String WILDCARD_QUERY = "wildcard";

    static int countNotEmpty(final List<QueryClause> clauses) {
        if (clauses == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0, size = clauses.size(); i < size; i++) {
            final QueryClause clause = clauses.get(i);
            if (!clause.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public static String toElasticQuery(final QueryClause query, final ObjectMapper mapper)
            throws IOException {
        final JsonFactory factory = mapper.getFactory();

        final StringWriter writer = new StringWriter();
        try (final JsonGenerator gen = factory.createGenerator(writer).useDefaultPrettyPrinter()) {
            gen.writeStartObject();
            query.accept(new ElasticQueryDslVisitor(gen));
            gen.writeEndObject();
        }
        final String actual = writer.getBuffer().toString();
        return actual;
    }

    public default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        visitor.leave(this);
    }

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public boolean isEmpty();
}
