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
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.search.OmitEmptyVisitor;
import com.arakelian.elastic.search.WriteAggregationVisitor;
import com.arakelian.elastic.search.WriteQueryVisitor;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

@Value.Immutable
@JsonSerialize(as = ImmutableSearch.class)
@JsonDeserialize(builder = ImmutableSearch.Builder.class)
@JsonPropertyOrder({ "scroll", "scrollId", "from", "size", "searchType", "terminateAfter", "sourceFilter",
        "query", "sort", "aggregation", "version", "explain", "batchedReduceSize", "preference" })
@Value.Style(from = "using", get = { "is*", "get*" }, depluralize = true)
public interface Search extends Serializable {
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
    public static void serialize(final JsonGenerator writer, final List<Sort> sorts) throws IOException {
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
                    writeFieldValue(writer, "order", order);
                    writeFieldValue(writer, "mode", mode.name().toLowerCase());
                    writer.writeEndObject();
                }
                writer.writeEndObject();
            }
        }
        writer.writeEndArray();
    }

    public static void serialize(final JsonGenerator writer, final Search search) throws IOException {
        writer.writeStartObject();

        writeFieldValue(writer, "scroll_id", search.getScrollId());
        writeFieldValue(writer, "from", search.getFrom());
        writeFieldValue(writer, "size", search.getSize());
        writeFieldValue(writer, "terminate_after", search.getTerminateAfter());

        final SourceFilter sourceFilter = search.getSourceFilter();
        if (sourceFilter != null && !sourceFilter.isEmpty()) {
            if (sourceFilter.isExcludeAll()) {
                writeFieldValue(writer, "_source", Boolean.FALSE);
            } else {
                writer.writeFieldName("_source");
                writer.writeStartObject();
                writeFieldWithValues(writer, "includes", sourceFilter.getIncludes());
                writeFieldWithValues(writer, "excludes", sourceFilter.getExcludes());
                writer.writeEndObject(); // query
            }
        }

        final Query query = search.getQuery();
        if (query != null && !query.isEmpty()) {
            writer.writeFieldName("query");
            writer.writeStartObject();
            query.accept(new OmitEmptyVisitor(new WriteQueryVisitor(writer)));
            writer.writeEndObject(); // query
        }

        final List<Sort> sort = search.getSorts();
        if (sort.size() != 0) {
            writer.writeFieldName("sort");
            serialize(writer, sort);
        }

        final List<Aggregation> aggregations = search.getAggregations();
        if (!aggregations.isEmpty()) {
            writeAggregations(aggregations, new WriteAggregationVisitor(writer));
        }

        writeFieldValue(writer, "version", search.isVersion());
        writeFieldValue(writer, "explain", search.isExplain());
        writeFieldValue(writer, "batched_reduce_size", search.getBatchedReduceSize());

        writer.writeEndObject();
    }

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

    @Value.Default
    public default List<Aggregation> getAggregations() {
        return ImmutableList.of();
    }

    @Nullable
    public Integer getBatchedReduceSize();

    @Nullable
    public Integer getFrom();

    @Nullable
    public String getPreference();

    @Nullable
    public Query getQuery();

    @Nullable
    public String getScroll();

    @Nullable
    public String getScrollId();

    @Nullable
    public SearchType getSearchType();

    @Nullable
    public Integer getSize();

    @Value.Default
    public default List<Sort> getSorts() {
        return ImmutableList.of();
    }

    @Nullable
    public SourceFilter getSourceFilter();

    /**
     * Returns the maximum number of documents to collect for each shard, upon reaching which the
     * query execution will terminate early. If set, the response will have a boolean field
     * terminated_early to indicate whether the query execution has actually terminated_early.
     *
     * @return the maximum number of documents to collect for each shard
     */
    @Nullable
    public Integer getTerminateAfter();

    @Nullable
    public Boolean isExplain();

    /**
     * Returns true to enable caching of search results for requests where size is 0, ie
     * aggregations and suggestions (no top hits returned).
     *
     * @return true to enable caching of search results
     */
    @Nullable
    public Boolean isRequestCache();

    @Nullable
    public Boolean isVersion();
}
