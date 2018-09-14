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
import java.util.List;

import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.enums.SortMode;
import com.arakelian.elastic.model.search.Highlighter;
import com.arakelian.elastic.model.search.Highlighter.Field;
import com.arakelian.elastic.model.search.Highlighter.Highlight;
import com.arakelian.elastic.model.search.Query;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.elastic.model.search.Sort;
import com.arakelian.elastic.model.search.SourceFilter;
import com.fasterxml.jackson.core.JsonGenerator;

public class WriteSearchVisitor extends AbstractVisitor {
    public WriteSearchVisitor(final JsonGenerator writer, final VersionComponents version) {
        super(writer, version);
    }

    public void writeAggregations(final List<Aggregation> aggregations, final WriteAggregationVisitor visitor)
            throws IOException {
        if (aggregations.isEmpty()) {
            return;
        }

        final JsonGenerator writer = visitor.getWriter();
        writer.writeFieldName("aggregations");
        writer.writeStartObject();
        for (final Aggregation agg : aggregations) {
            writer.writeFieldName(agg.getName());
            writer.writeStartObject();
            agg.accept(visitor);
            writeAggregations(agg.getSubAggregations(), visitor);
            writer.writeEndObject(); // name
        }
        writer.writeEndObject(); // aggregations
    }

    public void writeHighlight(final Highlight highlight) throws IOException {
        writeHighlighter(highlight);

        final List<Field> fields = highlight.getFields();
        if (fields.size() != 0) {
            writer.writeFieldName("fields");

            final boolean useExplicitFieldOrder = highlight.isUseExplicitFieldOrder();
            if (useExplicitFieldOrder) {
                writer.writeStartArray();
            } else {
                writer.writeStartObject();
            }

            for (final Highlighter.Field field : fields) {
                if (useExplicitFieldOrder) {
                    writer.writeStartObject();
                }
                writeHighlighterField(field);
                if (useExplicitFieldOrder) {
                    writer.writeEndObject();
                }
            }

            if (useExplicitFieldOrder) {
                writer.writeEndArray();
            } else {
                writer.writeEndObject();
            }
        }
    }

    public void writeHighlighter(final Highlighter highlighter) throws IOException {
        writeFieldValue("type", highlighter.getType());
        writeFieldValue("boundary_scanner", highlighter.getBoundaryScanner());
        writeFieldValue("boundary_scanner_locale", highlighter.getBoundaryScannerLocale());
        writeFieldValue("boundary_max_scan", highlighter.getBoundaryMaxScan());
        writeFieldValue("boundary_chars", highlighter.getBoundaryChars());
        writeFieldValue("encoder", highlighter.getEncoder());
        writeFieldValue("force_source", highlighter.isForceSource());
        writeFieldValue("fragment_size", highlighter.getFragmentSize());
        writeFieldValue("fragmenter", highlighter.getFragmenter());
        writeFieldValue("no_match_size", highlighter.getNoMatchSize());
        writeFieldValue("number_of_fragments", highlighter.getNumberOfFragments());
        writeFieldValue("order", highlighter.getOrder());
        writeFieldValue("phrase_limit", highlighter.getPhraseLimit());
        writeFieldValue("post_tags", highlighter.getPostTags());
        writeFieldValue("pre_tags", highlighter.getPreTags());
        writeFieldValue("tags_schema", highlighter.getTagsSchema());
        writeFieldValue("require_field_match", highlighter.isRequireFieldMatch());

        writeQuery("highlight_query", highlighter.getHighlightQuery());
    }

    public void writeHighlighterField(final Highlighter.Field field) throws IOException {
        writer.writeFieldName(field.getName());
        writer.writeStartObject();
        writeHighlighter(field);
        writeFieldValue("fragment_offset", field.getFragmentOffset());
        writeFieldValue("matched_fields", field.getMatchedFields());
        writer.writeEndObject(); // field
    }

    public void writeQuery(final String fieldName, final Query query) throws IOException {
        if (query != null && !query.isEmpty()) {
            writer.writeFieldName(fieldName);
            writer.writeStartObject();
            query.accept(new OmitEmptyVisitor(new WriteQueryVisitor(writer, version)));
            writer.writeEndObject(); // query
        }
    }

    public void writeSearch(final Search search) throws IOException {
        writer.writeStartObject();

        writeFieldValue("scroll_id", search.getScrollId());
        writeFieldValue("from", search.getFrom());
        writeFieldValue("size", search.getSize());
        writeFieldValue("track_scores", search.isTrackScores());
        writeFieldValue("track_total_hits", search.isTrackTotalHits());
        writeFieldValue("allow_partial_search_results", search.isAllowPartialSearchResults());
        writeFieldValue("timeout", search.getTimeout());
        writeFieldValue("terminate_after", search.getTerminateAfter());

        final SourceFilter sourceFilter = search.getSourceFilter();
        if (sourceFilter != null && !sourceFilter.isEmpty()) {
            if (sourceFilter.isExcludeAll()) {
                writeFieldValue("_source", Boolean.FALSE);
            } else {
                writer.writeFieldName("_source");
                writer.writeStartObject();
                writeFieldWithValues("includes", sourceFilter.getIncludes());
                writeFieldWithValues("excludes", sourceFilter.getExcludes());
                writer.writeEndObject(); // query
            }
        }

        List<String> fields = search.getFields();
        if (fields.size() != 0) {
            writer.writeFieldName("fields");
            writer.writeStartArray();
            for (final String field : fields) {
                writer.writeString(field);
            }
            writer.writeEndArray();
        }

        final Query query = search.getQuery();
        writeQuery("query", query);

        final List<Sort> sort = search.getSorts();
        if (sort.size() != 0) {
            writer.writeFieldName("sort");
            writeSorts(sort);
        }

        final Highlight highlight = search.getHighlight();
        if (highlight != null) {
            writer.writeFieldName("highlight");
            writer.writeStartObject();
            writeHighlight(highlight);
            writer.writeEndObject(); // highlight
        }

        final List<Aggregation> aggregations = search.getAggregations();
        if (!aggregations.isEmpty()) {
            writeAggregations(aggregations, new WriteAggregationVisitor(writer, version));
        }

        writeFieldValue("version", search.isVersion());
        writeFieldValue("explain", search.isExplain());
        writeFieldValue("batched_reduce_size", search.getBatchedReduceSize());

        writer.writeEndObject();
    }

    /**
     * Serialize an array of sorts
     *
     * @param sorts
     *            list of sort fields
     *
     * @throws IOException
     *             if serialization fails
     */
    public void writeSorts(final List<Sort> sorts) throws IOException {
        writer.writeStartArray();
        for (final Sort sort : sorts) {
            final String field = sort.getFieldName();
            if (sort.isDefaults()) {
                // compact form
                writer.writeString(field);
            } else {
                writer.writeStartObject();
                writer.writeFieldName(field);
                final String order = sort.getOrder().name().toLowerCase();
                final SortMode mode = sort.getMode();
                if (mode == null) {
                    // field with descending order
                    writer.writeString(order);
                } else {
                    // field with order and/or mode specified
                    writer.writeStartObject();
                    writeFieldValue("order", order);
                    writeFieldValue("mode", mode.name().toLowerCase());
                    writer.writeEndObject();
                }
                writer.writeEndObject();
            }
        }
        writer.writeEndArray();
    }
}
