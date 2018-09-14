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
import com.arakelian.elastic.doc.ElasticDocBuilder;
import com.arakelian.elastic.doc.filters.TokenFilter;
import com.arakelian.elastic.model.Mapping.FieldDeserializer;
import com.arakelian.elastic.model.enums.Orientation;
import com.arakelian.elastic.model.enums.SpatialStrategy;
import com.arakelian.elastic.model.enums.Tree;
import com.arakelian.jackson.CompoundTokenFilter;
import com.arakelian.jackson.JsonPointerNotMatchedFilter;
import com.arakelian.jackson.databind.ExcludeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableField.class)
@JsonDeserialize(builder = ImmutableField.Builder.class)
@JsonPropertyOrder({ "name", "aliases", "description", "type", "scaling_factor", "format", "enabled", "store",
        "index", "index_options", "norms", "doc_values", "additional_targets", "ignore_global_token_filters",
        "sort_tokens", "token_filters", "fielddata", "tree", "precision", "tree_levels", "strategy",
        "orientation", "points_only", "ignore_z_value", "ignore_above", "ignore_malformed",
        "position_increment_gap", "eager_global_ordinals", "include_in_all", "copy_to", "normalizer",
        "analyzer", "search_analyzer", "include_plugins", "exclude_plugins" })
public abstract class Field implements Serializable {
    // see: https://www.elastic.co/guide/en/elasticsearch/reference/current/index-options.html
    public enum IndexOptions {
        /**
         * Only the doc number is indexed. Can answer the question Does this term exist in this
         * field?
         **/
        DOCS, //

        /**
         * Doc number and term frequencies are indexed. Term frequencies are used to score repeated
         * terms higher than single terms.
         **/
        FREQS, //

        /**
         * Doc number, term frequencies, and term positions (or order) are indexed. Positions can be
         * used for proximity or phrase queries.
         **/
        POSITIONS, //

        /**
         * Doc number, term frequencies, positions, and start and end character offsets (which map
         * the term back to the original string) are indexed. Offsets are used by the
         * {@link com.arakelian.elastic.model.search.Highlighter.Type#UNIFIED} highlighter to speed
         * up highlighting.
         **/
        OFFSETS;

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static class SubfieldSerializer extends ExcludeSerializer<Field> {
        private static final com.fasterxml.jackson.core.filter.TokenFilter FILTER = CompoundTokenFilter.of( //
                new JsonPointerNotMatchedFilter("/name"), //
                new JsonPointerNotMatchedFilter("/include_in_all") //
        );

        public SubfieldSerializer() {
            super(Field.class, FILTER);
        }
    }

    // see: https://www.elastic.co/guide/en/elasticsearch/reference/current/term-vector.html
    public enum TermVector {
        /** No term vectors are stored. (default) **/
        NO, //

        /** Just the terms in the field are stored. **/
        YES, //

        /** Terms and positions are stored. **/
        WITH_POSITIONS, //

        /** Terms and character offsets are stored. **/
        WITH_OFFSETS, //

        /**
         * Terms, positions, and character offsets are stored. The fast vector highlighter requires
         * <code>with_positions_offsets</code>
         *
         * <p>
         * WARNING: Setting <code>with_positions_offsets</code> will double the size of a field’s
         * index.
         * </p>
         **/
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
        GEO_SHAPE, //
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
    @SuppressWarnings("EqualsHashCode")
    public boolean equals(@javax.annotation.Nullable final Object another) {
        if (this == another) {
            return true;
        }
        return another instanceof ImmutableField && equalTo((ImmutableField) another);
    }

    /**
     * Returns a list of fields that should be targeted by {@link ElasticDocBuilder} whenever this
     * field is targeted.
     *
     * @return list of fields that should be targeted by {@link ElasticDocBuilder} whenever this
     *         field is targeted.
     */
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("additional_targets")
    @JsonView(Enhancement.class)
    public List<String> getAdditionalTargets() {
        return ImmutableList.of();
    }

    /**
     * Returns a list of aliases for this field.
     *
     * @return list of aliases for this field.
     */
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("aliases")
    @JsonView(Enhancement.class)
    public Set<String> getAliases() {
        return ImmutableSet.of();
    }

    /**
     * Returns the analyzer used by this field.
     *
     * For TOKEN_COUNT fields, the analyzer is required, and so therefore we default one below to
     * make it easy to create new mappings by just specifyig a field type.
     *
     * @return the analyzer used by this field.
     */
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
     * Returns a query time boosting. Accepts a floating point number, defaults to 1.0.
     *
     * @return a query time boosting
     *
     * @see <a href=
     *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-boost.html">boost</a>
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("boost")
    @JsonView(Elastic.class)
    public abstract Double getBoost();

    /**
     * Returns true if Elastic should try to convert strings to numbers and truncate fractions for
     * integers.
     *
     * @return true if Elastic should try to convert strings to numbers and truncate fractions for
     *         integers
     *
     * @see <a href=
     *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/range.html">Range
     *      Datatypes</a>
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("coerce")
    @JsonView(Elastic.class)
    public abstract Boolean getCoerce();

    /**
     * Returns a list of fields that this field value should be copied to.
     *
     * The {@link #getCopyTo()} allows you to create custom _all fields. The values of multiple
     * fields can be copied into a group field, which can then be queried as a single field.
     *
     * @return list of fields that this field value should be copied to.
     */
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("copy_to")
    @JsonView(Elastic.class)
    public List<String> getCopyTo() {
        return ImmutableList.of();
    }

    @Nullable
    @Value.Auxiliary
    @JsonProperty("description")
    @JsonView(Enhancement.class)
    public abstract String getDescription();

    @Value.Default
    @Value.Auxiliary
    @JsonProperty("exclude_plugins")
    @JsonView(Enhancement.class)
    public Set<String> getExcludePlugins() {
        return ImmutableSet.of();
    }

    @Value.Default
    @Value.Auxiliary
    @JsonProperty("fields")
    @JsonSerialize(contentUsing = SubfieldSerializer.class)
    @JsonDeserialize(contentUsing = FieldDeserializer.class)
    public Map<String, Field> getFields() {
        return ImmutableMap.of();
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

        // see: https://github.com/elastic/elasticsearch/issues/27992
        // - ignore_above has a default value of 2^31-1 on keyword fields
        // - The default dynamic mappings for strings has ignore_above: 256.
        return Integer.valueOf(256);
    }

    @Value.Default
    @Value.Auxiliary
    @JsonProperty("include_plugins")
    @JsonView(Enhancement.class)
    public Set<String> getIncludePlugins() {
        return ImmutableSet.of();
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
     * Returns the normalizer used by this field.
     *
     * @return the normalizer used by this field.
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("normalizer")
    @JsonView(Elastic.class)
    public abstract String getNormalizer();

    /**
     * Returns a value that will replace null values during indexed.
     *
     * Normally, null values cannot be indexed or searched. This parameter allows you to replace
     * explicit null values with another value that can be indexed and searched instead.
     *
     * @return value that will replace null values during indexed
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("null_value")
    @JsonView(Elastic.class)
    public abstract String getNullValue();

    /**
     * Returns how to interpret vertex order for polygons / multipolygons.
     *
     * @return how to interpret vertex order for polygons / multipolygons.
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("orientation")
    @JsonView(Elastic.class)
    public abstract Orientation getOrientation();

    @Nullable
    @Value.Auxiliary
    @JsonProperty("position_increment_gap")
    @JsonView(Elastic.class)
    public abstract Integer getPositionIncrementGap();

    @Nullable
    @Value.Auxiliary
    @JsonProperty("precision")
    @JsonView(Elastic.class)
    public abstract String getPrecision();

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
     * Returns the approach for how to represent shapes at indexing and search time.
     *
     * @return the approach for how to represent shapes at indexing and search time.
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("strategy")
    @JsonView(Elastic.class)
    public abstract SpatialStrategy getStrategy();

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
    @JsonProperty("token_filters")
    public List<TokenFilter> getTokenFilters() {
        return ImmutableList.of();
    }

    @Nullable
    @Value.Auxiliary
    @JsonProperty("tree")
    @JsonView(Elastic.class)
    public abstract Tree getTree();

    /**
     * Returns maximum number of layers to be used by the PrefixTree. This can be used to control
     * the precision of shape representations and therefore how many terms are indexed. Defaults to
     * the default value of the chosen PrefixTree implementation
     *
     * @return maximum number of layers to be used by the PrefixTree
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("tree_levels")
    @JsonView(Elastic.class)
    public abstract String getTreeLevels();

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
        if (type == null || isMetaField() || type == Type.COMPLETION || type == Type.GEO_SHAPE) {
            return null;
        }
        return Boolean.TRUE.equals(isIndex()) && !(type == Type.TEXT || //
                type == Type.INTEGER_RANGE || //
                type == Type.FLOAT_RANGE || //
                type == Type.LONG_RANGE || //
                type == Type.DOUBLE_RANGE || //
                type == Type.DATE_RANGE);
    }

    @Nullable
    @Value.Auxiliary
    @JsonProperty("eager_global_ordinals")
    @JsonView(Elastic.class)
    public abstract Boolean isEagerGlobalOrdinals();

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

    @Nullable
    @Value.Auxiliary
    @JsonProperty("ignore_global_token_filters")
    @JsonView(Enhancement.class)
    public abstract Boolean isIgnoreGlobalTokenFilters();

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
     * Returns true (default) if three dimension points will be accepted (stored in source) but only
     * latitude and longitude values will be indexed; the third dimension is ignored. If false,
     * geo-points containing any more than latitude and longitude (two dimensions) values throw an
     * exception and reject the whole document.
     *
     * @return true if three dimension points will be accepted but only latitude and longitude
     *         values will be indexed
     */
    @Nullable
    @Value.Auxiliary
    @JsonProperty("ignore_z_value")
    @JsonView(Elastic.class)
    public abstract Boolean isIgnoreZValue();

    /**
     * Returns true if this field should not be included in _all.
     *
     * @return true if this field should not be included in _all.
     *
     * @see <a href=
     *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/include-in-all.html">https://www.elastic.co/guide/en/elasticsearch/reference/current/include-in-all.html</a>
     */
    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("include_in_all")
    @JsonView(Version5.class)
    public Boolean isIncludeInAll() {
        final Type type = getType();
        if (type == null || isMetaField() || type == Type.COMPLETION || type == Type.GEO_SHAPE) {
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
     * @see <a href=
     *      "https://github.com/elastic/elasticsearch/issues/21134">https://github.com/elastic/elasticsearch/issues/21134</a>
     */
    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("index")
    @JsonView(Elastic.class)
    public Boolean isIndex() {
        final Type type = getType();
        if (type == null || isMetaField() || type == Type.COMPLETION || type == Type.GEO_SHAPE) {
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

    @Nullable
    @Value.Auxiliary
    @JsonProperty("norms")
    @JsonView(Elastic.class)
    public abstract Boolean isNorms();

    @Nullable
    @Value.Auxiliary
    @JsonProperty("points_only")
    @JsonView(Elastic.class)
    public abstract Boolean isPointsOnly();

    @Nullable
    @Value.Auxiliary
    @JsonProperty("sort_tokens")
    @JsonView(Enhancement.class)
    public abstract Boolean isSortTokens();

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
        if (type == null || isMetaField() || type == Type.COMPLETION || type == Type.GEO_SHAPE) {
            return null;
        }
        return type == Type.BINARY;
    }

    private boolean equalTo(final ImmutableField another) {
        return getName().equals(another.getName());
    }
}
