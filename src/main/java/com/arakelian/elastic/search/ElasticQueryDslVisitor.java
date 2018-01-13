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

package com.arakelian.elastic.search;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;

import com.arakelian.elastic.model.search.BoolQuery;
import com.arakelian.elastic.model.search.IdsQuery;
import com.arakelian.elastic.model.search.MatchQuery;
import com.arakelian.elastic.model.search.PrefixQuery;
import com.arakelian.elastic.model.search.Query;
import com.arakelian.elastic.model.search.QueryStringQuery;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.elastic.model.search.StandardQuery;
import com.arakelian.elastic.model.search.TermsQuery;
import com.arakelian.elastic.model.search.WildcardQuery;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Preconditions;

public class ElasticQueryDslVisitor extends QueryVisitor {
    private final JsonGenerator writer;

    public ElasticQueryDslVisitor(final JsonGenerator writer) {
        this.writer = Preconditions.checkNotNull(writer);
    }

    @Override
    public boolean enterBoolQuery(final BoolQuery bool) {
        try {
            writer.writeFieldName("bool");
            writer.writeStartObject();
            writeStandardFields(bool);

            writeClauses("must", bool.getMustClauses());
            writeClauses("filter", bool.getFilterClauses());
            writeClauses("must_not", bool.getMustNotClauses());
            writeClauses("should", bool.getShouldClauses());
            writeFieldValue("minimum_should_match", bool.getMinimumShouldMatch());

            writer.writeEndObject(); // bool
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterIdsQuery(final IdsQuery ids) {
        try {
            writer.writeFieldName("ids");
            writer.writeStartObject();
            writeStandardFields(ids);
            writeFieldWithValues("types", ids.getTypes());
            writeFieldWithValues("values", ids.getValues());
            writer.writeEndObject(); // ids
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterMatchQuery(final MatchQuery match) {
        try {
            writer.writeFieldName("match");
            writer.writeStartObject();
            writer.writeFieldName(match.getFieldName());
            writer.writeStartObject();
            final Object value = match.getValue();
            if (match.hasMatchDefaults()) {
                writer.writeObject(value);
            } else {
                writeStandardFields(match);
                writeFieldValue("query", value);
                writeFieldValue("operator", match.getOperator());
                writeFieldValue("fuzziness", match.getFuzziness());
                writeFieldValue("fuzzy_rewrite", match.getFuzzyRewrite());
                writeFieldValue("prefix_length", match.getPrefixLength());
                writeFieldValue("max_expansions", match.getMaxExpansions());
                writeFieldValue("cutoff_frequency", match.getCutoffFrequency());
                writeFieldValue("zero_terms_query", match.getZeroTermsQuery());
                writeFieldValue(
                        "auto_generate_synonyms_phrase_query",
                        match.isAutoGenerateSynonymsPhraseQuery());
            }
            writer.writeEndObject(); // field
            writer.writeEndObject(); // prefix
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterPrefixQuery(final PrefixQuery prefix) {
        try {
            writer.writeFieldName("prefix");
            writer.writeStartObject();
            writer.writeFieldName(prefix.getFieldName());

            final String value = prefix.getValue();
            if (prefix.hasStandardDefaults()) {
                writer.writeString(value);
            } else {
                writer.writeStartObject();
                writeStandardFields(prefix);
                writeFieldValue("value", value);
                writer.writeEndObject();
            }
            writer.writeEndObject(); // prefix
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

            writeStandardFields(qs);
            writeFieldValue("fields", qs.getFields());
            writeFieldValue("default_field", qs.getDefaultField());
            writeFieldValue("query", qs.getQueryString());
            writeFieldValue("default_operator", qs.getDefaultOperator());
            writeFieldValue("allow_leading_wildcard", qs.isAllowLeadingWildcard());
            writeFieldValue("analyze_wildcard", qs.isAnalyzeWildcard());
            writeFieldValue("analyzer", qs.getAnalyzer());
            writeFieldValue("auto_generate_synonyms_phrase_query", qs.isAutoGenerateSynonymsPhraseQuery());
            writeFieldValue("enable_position_increments", qs.isEnablePositionIncrements());
            writeFieldValue("fuzziness", qs.getFuzziness());
            writeFieldValue("fuzzy_max_expansions", qs.getFuzzyMaxExpansions());
            writeFieldValue("fuzzy_prefix_length", qs.getFuzzyPrefixLength());
            writeFieldValue("fuzzy_transpositions", qs.isFuzzyTranspositions());
            writeFieldValue("lenient", qs.isLenient());
            writeFieldValue("max_determinized_states", qs.getMaxDeterminizedStates());
            writeFieldValue("minimum_should_match", qs.getMinimumShouldMatch());
            writeFieldValue("phrase_slop", qs.getPhraseSlop());
            writeFieldValue("quote_analyzer", qs.getQuoteAnalyzer());
            writeFieldValue("quote_field_suffix", qs.getQuoteFieldSuffix());
            writeFieldValue("time_zone", qs.getTimeZone());

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
            writeStandardFields(terms);
            writeFieldWithValues(terms.getFieldName(), terms.getValues());
            writer.writeEndObject(); // terms
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterWildcardQuery(final WildcardQuery wildcard) {
        try {
            writer.writeFieldName("wildcard");
            writer.writeStartObject();
            writeStandardFields(wildcard);
            writer.writeFieldName(wildcard.getFieldName());
            writer.writeString(wildcard.getValue());
            writer.writeEndObject(); // wildcard
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    private void writeClause(final Query clause) throws IOException {
        writer.writeStartObject();
        clause.accept(this);
        if (clause instanceof StandardQuery) {
            writeStandardFields((StandardQuery) clause);
        }
        writer.writeEndObject();
    }

    private void writeClauses(final String name, final List<Query> clauses) throws IOException {
        final int size = Query.countNotEmpty(clauses);
        if (size == 0) {
            return;
        }

        writer.writeFieldName(name);

        final boolean multiple = size != 1;
        if (multiple) {
            writer.writeStartArray();
        }
        for (final Query clause : clauses) {
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
        Search.writeFieldValue(writer, field, value);
    }

    private void writeFieldWithValues(final String field, final List<String> values) throws IOException {
        Search.writeFieldWithValues(writer, field, values);
    }

    private void writeStandardFields(final StandardQuery standardQuery) throws IOException {
        writeFieldValue("_name", standardQuery.getName());
        writeFieldValue("boost", standardQuery.getBoost());
    }
}
