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

package com.arakelian.elastic.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.Views.Elastic;
import com.arakelian.elastic.Views.Elastic.Version5;
import com.arakelian.elastic.Views.Enhancement;
import com.arakelian.elastic.doc.filters.TokenFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Value.Immutable
@JsonSerialize(as = ImmutableField.class)
@JsonDeserialize(builder = ImmutableField.Builder.class)
@JsonPropertyOrder({ "name", "type", "scaling_factor", "format", "enabled", "store", "index", "index_options",
        "doc_values", "fielddata", "ignore_above", "ignore_malformed", "include_in_all", "copy_to",
        "analyzer", "search_analyzer" })
public abstract class Field implements Serializable {
    // see: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-options.html
    public enum IndexOptions {
        DOCS, //
        FREQS, //
        POSITIONS, //
        OFFSETS;

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    // see: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-options.html
    public enum TermVector {
        NO, //
        YES, //
        WITH_POSITIONS, //
        WITH_OFFSETS, //
        WITH_POSITIONS_OFFSETS;

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    // see: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html
    public enum Type {
        TEXT, //
        KEYWORD, //
        BYTE, //
        SHORT, //
        INTEGER, //
        LONG, //
        FLOAT, //
        DOUBLE, //
        DATE, //
        BOOLEAN, //
        BINARY, //
        HALF_FLOAT, //
        SCALED_FLOAT, //
        INTEGER_RANGE, //
        FLOAT_RANGE, //
        LONG_RANGE, //
        DOUBLE_RANGE, //
        DATE_RANGE, //
        GEO_POINT, //
        // GEO_SHAPE, //
        IP, //
        COMPLETION, //
        TOKEN_COUNT;

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Elastic meta fields, see
     * https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-fields.html
     */
    public static final Set<String> META_FIELDS = Sets.newHashSet( //
            "_all", //
            "_field_names", //
            "_id", //
            "_index", //
            "_meta", //
            "_parent", //
            "_routing", //
            "_source", // The original JSON representing the body of the document.
            "_type", //
            "_uid");

    @Override
    public boolean equals(@javax.annotation.Nullable final Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ImmutableField && equalTo((ImmutableField) another);
    }

    private boolean equalTo(final ImmutableField another) {
        return getName().equals(another.getName());
    }

    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("analyzer")
    @JsonView(Elastic.class)
    public String getAnalyzer() {
        // must specify an analyzer for TOKEN_COUNT fields
        return getType() == Type.TOKEN_COUNT ? "standard" : null;
    }

    /**
     * Returns a list of fields that this field value should be copied to.
     *
     * The {@link #getCopyTo()} allows you to create custom _all fields. The values of multiple
     * fields can be copied into a group field, which can then be queried as a single field.
     *
     * @return list of fields that this field value should be copied to.
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("copy_to")
    @JsonView(Elastic.class)
    public abstract List<String> getCopyTo();

    @Value.Derived
    @Value.Auxiliary
    @JsonProperty("fields")
    @JsonView(Elastic.class)
    public Map<String, Field> getFieldsByName() {
        final Map<String, Field> names = Maps.newLinkedHashMap();
        for (final Field field : getSubfields()) {
            names.put(field.getName(), field);
        }
        return names;
    }

    /**
     * Returns the date format used by Elastic to parse date values.
     *
     * Note that Elastic internally stores dates as a long value representing
     * milliseconds-since-the-epoch in UTC.
     *
     * @return date format used by Elastic to parse date values.
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("format")
    @JsonView(Elastic.class)
    public abstract String getFormat();

    /**
     * Returns the maximum length of strings that can be indexed or stored. Strings longer than the
     * ignore_above setting will be ignored.
     *
     * @return maximum length of strings that can be indexed or stored
     */
    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("ignore_above")
    @JsonView(Elastic.class)
    public Integer getIgnoreAbove() {
        if (isMetaField() || getType() != Type.KEYWORD) {
            return null;
        }
        // see: https://en.wikipedia.org/wiki/Longest_word_in_English
        return Integer.valueOf(45);
    }

    /**
     * Returns setting that determines what information is added to the inverted index.
     *
     * Analyzed string fields use {@link IndexOptions#POSITIONS} as the default, and all other
     * fields use {@link IndexOptions#DOCS} as the default.
     *
     * @return setting that determines what information is added to the inverted index.
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("index_options")
    @JsonView(Elastic.class)
    public abstract IndexOptions getIndexOptions();

    /**
     * Returns the name of the Elastic index field that should be created.
     *
     * @return name of the Elastic index field that should be created.
     */
    public abstract String getName();

    /**
     * Returns a value that will replace null values during indexed. Normally, null values cannot be
     * indexed or searched. This parameter allows you to replace explicit null values with another
     * value that can be indexed and searched instead.
     *
     * @return value that will replace null values during indexed
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("null_value")
    @JsonView(Elastic.class)
    public abstract String getNullValue();

    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("scaling_factor")
    @JsonView(Elastic.class)
    public Integer getScalingFactor() {
        return getType() == Type.SCALED_FLOAT ? 100 : null;
    }

    @Nullable
    @Value.Auxiliary
    @JsonProperty("search_analyzer")
    @JsonView(Elastic.class)
    public abstract String getSearchAnalyzer();

    /**
     * Returns a list of subfields, if any. A field that has subfields is called a "multi-field" in
     * Elastic parlance.
     *
     * Note: It is often useful to index the same field in different ways for different purposes.
     * This is the purpose of multi-fields. For instance, a string field could be mapped as a text
     * field for full-text search, and as a keyword field for sorting or aggregations
     *
     * @return list of subfields if any
     */
    @Value.Auxiliary
    @JsonIgnore
    public abstract List<Field> getSubfields();

    /**
     * Returns setting that determines what information is added to the inverted index.
     *
     * Analyzed string fields use {@link IndexOptions#POSITIONS} as the default, and all other
     * fields use {@link IndexOptions#DOCS} as the default.
     *
     * @return setting that determines what information is added to the inverted index.
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("term_vector")
    @JsonView(Elastic.class)
    public abstract TermVector getTermVector();

    @Value.Default
    @Value.Auxiliary
    @JsonView(Enhancement.class)
    public List<TokenFilter> getTokenFilters() {
        return ImmutableList.of();
    }

    @Nullable
    @Value.Default
    @JsonProperty("type")
    @JsonView(Elastic.class)
    public Type getType() {
        return isMetaField() ? null : Type.TEXT;
    }

    /**
     * Returns true if doc_values is enabled. Note that all fields which support doc values have
     * them enabled by default.
     *
     * @return true if this field has doc_values enabled.
     */
    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("doc_values")
    @JsonView(Elastic.class)
    public Boolean isDocValues() {
        final Type type = getType();
        if (type == null || isMetaField() || type == Type.COMPLETION) {
            return null;
        }
        return Boolean.TRUE.equals(isIndex()) && !(type == Type.TEXT || //
                type == Type.INTEGER_RANGE || //
                type == Type.FLOAT_RANGE || //
                type == Type.LONG_RANGE || //
                type == Type.DOUBLE_RANGE || //
                type == Type.DATE_RANGE);
    }

    /**
     * Returns true if this field is enabled.
     *
     * The enabled setting can be applied only to the mapping type and to object fields, causes
     * Elasticsearch to skip parsing of the contents of the field entirely. The JSON can still be
     * retrieved from the _source field, but it is not searchable or stored in any other way.
     *
     * @return true if this field is enabled.
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("enabled")
    @JsonView(Elastic.class)
    public abstract Boolean isEnabled();

    /**
     * Returns true if field data is enabled for field.
     *
     * Fielddata can consume a lot of heap space, especially when loading high cardinality text
     * fields. Once fielddata has been loaded into the heap, it remains there for the lifetime of
     * the segment. Also, loading fielddata is an expensive process which can cause users to
     * experience latency hits. This is why fielddata is disabled by default.
     *
     * @return true if field data is enabled for field.
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("fielddata")
    @JsonView(Elastic.class)
    public abstract Boolean isFielddata();

    /**
     * Returns true if this field should ignore illegal values detected when building Elastic
     * document.
     *
     * @return true if this field should ignore illegal values detected when building Elastic
     *         document.
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("ignore_malformed")
    @JsonView(Elastic.class)
    public abstract Boolean isIgnoreMalformed();

    /**
     * Returns true if this field should not be included in _all.
     *
     * @return true if this field should not be included in _all.
     *
     * @see <a
     *      href="https://www.elastic.co/guide/en/elasticsearch/reference/current/include-in-all.html">https://www.elastic.co/guide/en/elasticsearch/reference/current/include-in-all.html</a>
     */
    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("include_in_all")
    @JsonView(Version5.class)
    public Boolean isIncludeInAll() {
        if (isMetaField() || getType() == null) {
            return null;
        }

        // Elastic docs: "It defaults to true, unless index is set to no."
        return !Boolean.FALSE.equals(isIndex());
    }

    /**
     * Returns flag that indicates if field is indexed and therefore searchable.
     *
     * Note that prior to ES 5, "analyzed" and "not_analyzed" were acceptable values for this field.
     * Both values implied that index was "true", with "not_analyzed" also implying a field type of
     * "keyword".
     *
     * A non-technical discussion of this on Elastic blog can be found here:
     * https://www.elastic.co/blog/strings-are-dead-long-live-strings
     *
     * @return flag that indicates if field is indexed.
     *
     * @see <a
     *      href="https://github.com/elastic/elasticsearch/issues/21134">https://github.com/elastic/elasticsearch/issues/21134</a>
     */
    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("index")
    @JsonView(Elastic.class)
    public Boolean isIndex() {
        final Type type = getType();
        if (type == null || isMetaField() || type == Type.COMPLETION) {
            return null;
        }
        return type != Type.BINARY;
    }

    @Value.Default
    @Value.Auxiliary
    @JsonIgnore
    @JsonView(Elastic.class)
    public boolean isMetaField() {
        final String name = getName();
        return META_FIELDS.contains(name);
    }

    /**
     * Returns true if the field value is stored.
     *
     * By default, field values are indexed to make them searchable, but they are not stored. This
     * means that the field can be queried, but the original field value cannot be retrieved.
     *
     * @return true if this field value is stored.
     */
    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("store")
    @JsonView(Elastic.class)
    public Boolean isStore() {
        final Type type = getType();
        if (type == null || isMetaField() || type == Type.COMPLETION) {
            return null;
        }
        return type == Type.BINARY;
    }
}
