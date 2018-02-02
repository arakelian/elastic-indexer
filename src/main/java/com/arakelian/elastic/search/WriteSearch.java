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
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.search.Highlighter;
import com.arakelian.elastic.model.search.Highlighter.Field;
import com.arakelian.elastic.model.search.Highlighter.Highlight;
import com.arakelian.elastic.model.search.Query;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.elastic.model.search.Sort;
import com.arakelian.elastic.model.search.SortMode;
import com.arakelian.elastic.model.search.SourceFilter;
import com.fasterxml.jackson.core.JsonGenerator;

public class WriteSearch {
    public static void writeAggregations(
            final List<Aggregation> aggregations,
            final WriteAggregationVisitor visitor) throws IOException {
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

    public static void writeArrayOf(final JsonGenerator writer, final Collection<String> values)
            throws IOException {
        writer.writeStartArray();
        for (final String value : values) {
            writer.writeString(value);
        }
        writer.writeEndArray();
    }

    public static void writeFieldValue(final JsonGenerator writer, final String field, final Object value)
            throws IOException {
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

        if (value instanceof Enum) {
            // Elastic uses lowercase names
            writer.writeFieldName(field);
            writer.writeString(((Enum) value).name().toLowerCase());
            return;
        }

        // output value
        writer.writeFieldName(field);
        writer.writeObject(value);
    }

    public static void writeFieldValue(final JsonGenerator writer, final String field, final String value)
            throws IOException {
        if (!StringUtils.isEmpty(value)) {
            writer.writeFieldName(field);
            writer.writeString(value);
        }
    }

    public static void writeFieldWithValues(
            final JsonGenerator writer,
            final String field,
            final Collection<String> values) throws IOException {
        if (values != null && values.size() != 0) {
            writer.writeFieldName(field);
            writeArrayOf(writer, values);
        }
    }

    public static void writeHighlight(final JsonGenerator writer, final Highlight highlight)
            throws IOException {
        writeHighlighter(writer, highlight);

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
                writeHighlighterField(writer, field);
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

    public static void writeHighlighter(final JsonGenerator writer, final Highlighter highlighter)
            throws IOException {
        writeFieldValue(writer, "type", highlighter.getType());
        writeFieldValue(writer, "boundary_scanner", highlighter.getBoundaryScanner());
        writeFieldValue(writer, "boundary_scanner_locale", highlighter.getBoundaryScannerLocale());
        writeFieldValue(writer, "boundary_max_scan", highlighter.getBoundaryMaxScan());
        writeFieldValue(writer, "boundary_chars", highlighter.getBoundaryChars());
        writeFieldValue(writer, "encoder", highlighter.getEncoder());
        writeFieldValue(writer, "force_source", highlighter.isForceSource());
        writeFieldValue(writer, "fragment_size", highlighter.getFragmentSize());
        writeFieldValue(writer, "fragmenter", highlighter.getFragmenter());
        writeFieldValue(writer, "no_match_size", highlighter.getNoMatchSize());
        writeFieldValue(writer, "number_of_fragments", highlighter.getNumberOfFragments());
        writeFieldValue(writer, "order", highlighter.getOrder());
        writeFieldValue(writer, "phrase_limit", highlighter.getPhraseLimit());
        writeFieldValue(writer, "post_tags", highlighter.getPostTags());
        writeFieldValue(writer, "pre_tags", highlighter.getPreTags());
        writeFieldValue(writer, "tags_schema", highlighter.getTagsSchema());
        writeFieldValue(writer, "require_field_match", highlighter.isRequireFieldMatch());

        writeQuery(writer, "highlight_query", highlighter.getHighlightQuery());
    }

    public static void writeHighlighterField(final JsonGenerator writer, final Highlighter.Field field)
            throws IOException {
        writer.writeFieldName(field.getName());
        writer.writeStartObject();
        writeHighlighter(writer, field);
        writeFieldValue(writer, "fragment_offset", field.getFragmentOffset());
        writeFieldValue(writer, "matched_fields", field.getMatchedFields());
        writer.writeEndObject(); // field
    }

    public static void writeSearch(final JsonGenerator writer, final Search search) throws IOException {
        writer.writeStartObject();

        WriteSearch.writeFieldValue(writer, "scroll_id", search.getScrollId());
        WriteSearch.writeFieldValue(writer, "from", search.getFrom());
        WriteSearch.writeFieldValue(writer, "size", search.getSize());
        WriteSearch.writeFieldValue(writer, "terminate_after", search.getTerminateAfter());

        final SourceFilter sourceFilter = search.getSourceFilter();
        if (sourceFilter != null && !sourceFilter.isEmpty()) {
            if (sourceFilter.isExcludeAll()) {
                WriteSearch.writeFieldValue(writer, "_source", Boolean.FALSE);
            } else {
                writer.writeFieldName("_source");
                writer.writeStartObject();
                WriteSearch.writeFieldWithValues(writer, "includes", sourceFilter.getIncludes());
                WriteSearch.writeFieldWithValues(writer, "excludes", sourceFilter.getExcludes());
                writer.writeEndObject(); // query
            }
        }

        final Query query = search.getQuery();
        writeQuery(writer, "query", query);

        final List<Sort> sort = search.getSorts();
        if (sort.size() != 0) {
            writer.writeFieldName("sort");
            writeSorts(writer, sort);
        }

        final Highlight highlight = search.getHighlight();
        if (highlight != null) {
            writer.writeFieldName("highlight");
            writer.writeStartObject();
            writeHighlight(writer, highlight);
            writer.writeEndObject(); // highlight
        }

        final List<Aggregation> aggregations = search.getAggregations();
        if (!aggregations.isEmpty()) {
            WriteSearch.writeAggregations(aggregations, new WriteAggregationVisitor(writer));
        }

        WriteSearch.writeFieldValue(writer, "version", search.isVersion());
        WriteSearch.writeFieldValue(writer, "explain", search.isExplain());
        WriteSearch.writeFieldValue(writer, "batched_reduce_size", search.getBatchedReduceSize());

        writer.writeEndObject();
    }

    public static void writeQuery(final JsonGenerator writer, String fieldName, final Query query)
            throws IOException {
        if (query != null && !query.isEmpty()) {
            writer.writeFieldName(fieldName);
            writer.writeStartObject();
            query.accept(new OmitEmptyVisitor(new WriteQueryVisitor(writer)));
            writer.writeEndObject(); // query
        }
    }

    /**
     * Serialize an array of sorts
     *
     * @param writer
     *            JSON generator
     * @param sorts
     *            list of sort fields
     * @throws IOException
     *             if serialization fails
     */
    public static void writeSorts(final JsonGenerator writer, final List<Sort> sorts) throws IOException {
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
                    WriteSearch.writeFieldValue(writer, "order", order);
                    WriteSearch.writeFieldValue(writer, "mode", mode.name().toLowerCase());
                    writer.writeEndObject();
                }
                writer.writeEndObject();
            }
        }
        writer.writeEndArray();
    }
}
