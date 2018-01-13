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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.arakelian.elastic.model.query.BoolQuery;
import com.arakelian.elastic.model.query.QueryClause;
import com.arakelian.elastic.model.query.QueryStringQuery;
import com.arakelian.elastic.model.query.StandardQuery;
import com.arakelian.elastic.model.query.TermsQuery;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Preconditions;

public class ElasticQueryDslVisitor extends QueryVisitor {
    private final JsonGenerator writer;

    public ElasticQueryDslVisitor(final JsonGenerator writer) {
        this.writer = Preconditions.checkNotNull(writer);
    }

    @Override
    public boolean enter(final QueryClause clause) {
        if (!super.enter(clause)) {
            return false;
        }
        if (getDepth() == 1) {
            try {
                writer.writeFieldName("query");
                writer.writeStartObject();
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return true;
    }

    @Override
    public boolean enterBoolQuery(final BoolQuery bool) {
        try {
            writer.writeFieldName("bool");
            writer.writeStartObject();

            writeClauses("must", bool.getMustClauses());
            writeClauses("filter", bool.getFilterClauses());
            writeClauses("must_not", bool.getMustNotClauses());
            writeClauses("should", bool.getShouldClauses());

            writeFieldValue("minimum_should_match", bool.getMinimumShouldMatch());
            writeStandardFields(bool);

            writer.writeEndObject(); // bool
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterQueryStringQuery(final QueryStringQuery qs) {
        try {
            writer.writeFieldName("query_string");
            writer.writeStartObject();

            writeFieldValue("fields", qs.getFields());
            writeFieldValue("default_field", qs.getDefaultField());
            writeFieldValue("query", qs.getQueryString());
            writeFieldValue("default_operator", qs.getDefaultOperator());
            writeFieldValue("analyzer", qs.getAnalyzer());
            writeFieldValue("quote_analyzer", qs.getQuoteAnalyzer());
            writeFieldValue("allow_leading_wildcard", qs.isAllowLeadingWildcard());
            writeFieldValue("enable_position_increments", qs.isEnablePositionIncrements());
            writeFieldValue("fuzzy_max_expansions", qs.getFuzzyMaxExpansions());
            writeFieldValue("fuzziness", qs.getFuzziness());
            writeFieldValue("fuzzy_prefix_length", qs.getFuzzyPrefixLength());
            writeFieldValue("fuzzy_transpositions", qs.isFuzzyTranspositions());
            writeFieldValue("phrase_slop", qs.getPhraseSlop());
            writeFieldValue("analyze_wildcard", qs.isAnalyzeWildcard());
            writeFieldValue("max_determinized_states", qs.getMaxDeterminizedStates());
            writeFieldValue("minimum_should_match", qs.getMinimumShouldMatch());
            writeFieldValue("lenient", qs.isLenient());
            writeFieldValue("time_zone", qs.getTimeZone());
            writeFieldValue("quote_field_suffix", qs.getQuoteFieldSuffix());
            writeFieldValue("auto_generate_synonyms_phrase_query", qs.isAutoGenerateSynonymsPhraseQuery());
            writeStandardFields(qs);

            writer.writeEndObject(); // query_string
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterTermsQuery(final TermsQuery terms) {
        try {
            writer.writeFieldName("terms");
            writer.writeStartObject();

            final String field = terms.getFieldName();
            final List<String> values = terms.getValues();

            writer.writeFieldName(field);
            writer.writeStartArray();
            for (final String value : values) {
                writer.writeString(value);
            }
            writer.writeEndArray();

            writeStandardFields(terms);
            writer.writeEndObject(); // terms
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public void leave(final QueryClause clause) {
        if (getDepth() == 1) {
            try {
                writer.writeEndObject();
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        super.leave(clause);
    }

    private void writeClause(final QueryClause clause) throws IOException {
        writer.writeStartObject();
        clause.accept(this);
        if (clause instanceof StandardQuery) {
            writeStandardFields((StandardQuery) clause);
        }
        writer.writeEndObject();
    }

    private void writeClauses(final String name, final List<QueryClause> clauses) throws IOException {
        final int size = QueryClause.countNotEmpty(clauses);
        if (size == 0) {
            return;
        }

        writer.writeFieldName(name);

        final boolean multiple = size != 1;
        if (multiple) {
            writer.writeStartArray();
        }
        for (final QueryClause clause : clauses) {
            if (!clause.isEmpty()) {
                writeClause(clause);
            }
        }
        if (multiple) {
            writer.writeEndArray();
        }
    }

    private void writeFieldValue(final String field, final Object value) throws IOException {
        if (value == null) {
            // omit null values
            return;
        }

        if (value instanceof Collection) {
            final Collection c = (Collection) value;
            if (c.size() == 0) {
                // omit empty collections
                return;
            }
            writer.writeFieldName(field);
            writer.writeStartArray();
            for (final Object o : c) {
                writer.writeObject(o);
            }
            writer.writeEndArray();
            return;
        }

        if (value instanceof CharSequence) {
            final CharSequence csq = (CharSequence) value;
            if (csq.length() == 0) {
                // omit empty strings
                return;
            }
        }

        // output value
        writer.writeFieldName(field);
        writer.writeObject(value);
    }

    private void writeFieldValue(final String field, final String value) throws IOException {
        if (!StringUtils.isEmpty(value)) {
            writer.writeFieldName(field);
            writer.writeString(value);
        }
    }

    private void writeStandardFields(final StandardQuery standardQuery) throws IOException {
        writeFieldValue("_name", standardQuery.getName());
        writeFieldValue("boost", standardQuery.getBoost());
    }
}
