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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.Views.Elastic;
import com.arakelian.elastic.Views.Elastic.Version5;
import com.arakelian.elastic.Views.Enhancement;
import com.arakelian.elastic.doc.filters.TokenChain;
import com.arakelian.elastic.doc.filters.TokenFilter;
import com.arakelian.jackson.JsonPointerNotMatchedFilter;
import com.arakelian.jackson.databind.ExcludeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableMapping.class)
@JsonDeserialize(builder = ImmutableMapping.Builder.class)
@JsonPropertyOrder({ "_all", "_source", "dynamic", "before_token_filters", "after_token_filters",
        "properties" })
public interface Mapping extends Serializable {
    public static enum Dynamic {
        TRUE, FALSE, STRICT;

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static class FieldDeserializer extends JsonDeserializer<Field> {
        @Override
        public Field deserialize(final JsonParser p, final DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            final ObjectNode node = ctxt.readValue(p, ObjectNode.class);
            node.put("name", p.getCurrentName());
            try (TreeTraversingParser tree = new TreeTraversingParser(node, p.getCodec())) {
                tree.nextToken();
                return ctxt.readValue(tree, Field.class);
            }
        }
    }

    public static class FieldSerializer extends ExcludeSerializer<Field> {
        private static final JsonPointerNotMatchedFilter filter = new JsonPointerNotMatchedFilter("/name");

        public FieldSerializer() {
            super(Field.class, filter);
        }
    }

    public String _DOC = "_doc";

    @Value.Default
    @Value.Auxiliary
    @JsonView(Enhancement.class)
    @JsonProperty("after_token_filters")
    public default List<TokenFilter> getAfterTokenFilters() {
        return ImmutableList.of();
    }

    @JsonIgnore
    @Value.Auxiliary
    @Value.Lazy
    public default Map<String, String> getAliases() {
        final ImmutableMap.Builder<String, String> aliases = ImmutableMap.<String, String> builder();

        final List<Field> fields = getFields();
        for (final Field field : fields) {
            for (final String alias : field.getAliases()) {
                aliases.put(alias, field.getName());
            }
        }

        // duplicate keys will cause this to fail with IllegalArgumentException
        return aliases.build();
    }

    /**
     * Returns configuration of _all meta field.
     *
     * @return configuration of _all meta field.
     */
    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("_all")
    @JsonView(Version5.class)
    @JsonSerialize(using = FieldSerializer.class)
    @JsonDeserialize(using = FieldDeserializer.class)
    public default Field getAll() {
        return ImmutableField.builder() //
                .name("_all") //
                .metaField(true) //
                .enabled(true) //
                .build();
    }

    @Value.Default
    @Value.Auxiliary
    @JsonView(Enhancement.class)
    @JsonProperty("before_token_filters")
    public default List<TokenFilter> getBeforeTokenFilters() {
        return ImmutableList.of();
    }

    @Nullable
    @Value.Auxiliary
    @JsonProperty("dynamic")
    @JsonView(Elastic.class)
    public Dynamic getDynamic();

    public default Field getField(final String name) {
        // quick check to see if field exists
        final Map<String, Field> fields = getProperties();
        if (fields.containsKey(name)) {
            return fields.get(name);
        }

        // if not, we'll try using alias
        final Map<String, String> aliases = getAliases();
        Preconditions
                .checkState(aliases.containsKey(name), "Field \"%s\" is not part of index mapping", name);
        final String canonical = aliases.get(name);
        return getField(canonical);
    }

    /**
     * Returns a list of fields in the index.
     *
     * @return list of fields in the index.
     */
    @JsonIgnore
    @Value.Default
    @Value.Auxiliary
    public default List<Field> getFields() {
        return ImmutableList.of();
    }

    public default TokenFilter getFieldTokenFilter(final String name) {
        // quick check to see if field exists
        final Map<String, TokenFilter> tokenFilters = getFieldTokenFilters();
        if (tokenFilters.containsKey(name)) {
            return tokenFilters.get(name);
        }

        // if not, we'll try using alias
        final Map<String, String> aliases = getAliases();
        Preconditions
                .checkState(aliases.containsKey(name), "Field \"%s\" is not part of index mapping", name);
        final String canonical = aliases.get(name);
        return getFieldTokenFilter(canonical);
    }

    @JsonIgnore
    @Value.Lazy
    @Value.Auxiliary
    public default Map<String, TokenFilter> getFieldTokenFilters() {
        // mapping may contain global token filters that are applied before or after the
        // field-specific list
        final List<TokenFilter> before = getBeforeTokenFilters();
        final List<TokenFilter> after = getAfterTokenFilters();

        final ImmutableMap.Builder<String, TokenFilter> tokenFilters = ImmutableMap.builder();
        final Map<String, Field> fields = getProperties();
        for (final String name : fields.keySet()) {
            final Field field = fields.get(name);

            final TokenFilter filter;
            if (before.size() == 0 && after.size() == 0) {
                // optimization: no global filters
                filter = TokenChain.link(field.getTokenFilters());
            } else {
                // combine filters
                filter = TokenChain
                        .link(Lists.newArrayList(Iterables.concat(before, field.getTokenFilters(), after)));
            }

            tokenFilters.put(name, filter);
        }
        return tokenFilters.build();
    }

    @Value.Default
    @Value.Auxiliary
    @JsonProperty("properties")
    @JsonSerialize(contentUsing = FieldSerializer.class)
    @JsonDeserialize(contentUsing = FieldDeserializer.class)
    public default Map<String, Field> getProperties() {
        return ImmutableMap.of();
    }

    /**
     * Returns configuration of _source meta field.
     *
     * @return configuration of _source meta field.
     */
    @Nullable
    @Value.Default
    @Value.Auxiliary
    @JsonProperty("_source")
    @JsonView(Elastic.class)
    @JsonSerialize(using = FieldSerializer.class)
    @JsonDeserialize(using = FieldDeserializer.class)
    public default Field getSource() {
        return ImmutableField.builder() //
                .name("_source") //
                .enabled(true) //
                .build();
    }

    public default boolean hasField(final Field field) {
        return field != null && hasField(field.getName());
    }

    public default boolean hasField(final String name) {
        if (name == null) {
            return false;
        }

        // quick check to see if field exists
        final Map<String, Field> fields = getProperties();
        if (fields.containsKey(name)) {
            return true;
        }

        // if not, we'll try using alias
        final Map<String, String> aliases = getAliases();
        return aliases.containsKey(name) && hasField(aliases.get(name));
    }

    @Value.Check
    public default Mapping normalizeFields() {
        final Map<String, Field> props = getProperties();

        final List<Field> fields = getFields();
        if (fields.size() == 0) {
            // optimization: expose fields
            return ImmutableMapping.builder() //
                    .from(this) //
                    .fields(props.values()) //
                    .build();
        }

        // make sure we have properties for all fields
        if (fields.size() == props.keySet().size()) {
            boolean consistent = true;
            for (final Field field : fields) {
                if (!props.containsKey(field.getName())) {
                    consistent = false;
                    break;
                }
            }
            if (consistent) {
                return this;
            }
        }

        final Map<String, Field> newProps = Maps.newLinkedHashMap();
        newProps.putAll(props);
        for (final Field field : fields) {
            final String name = field.getName();
            if (!newProps.containsKey(name)) {
                newProps.put(name, field);
            }
        }

        return ImmutableMapping.builder() //
                .from(this) //
                .properties(newProps) //
                .fields(newProps.values()) //
                .build();
    }
}
