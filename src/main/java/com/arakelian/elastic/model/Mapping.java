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

import java.util.List;
import java.util.Map;

import org.immutables.value.Value;

import com.arakelian.elastic.Elastic.Version5;
import com.arakelian.elastic.doc.DocumentBuilderPlugin;
import com.arakelian.elastic.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableMapping.class)
@JsonDeserialize(builder = ImmutableMapping.Builder.class)
@JsonPropertyOrder({ "_all", "_source", "dynamic", "properties" })
public interface Mapping {
    public static enum Dynamic {
        TRUE, FALSE, STRICT;

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    String _DEFAULT_ = "_default_";

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
    public default Field getAll() {
        return ImmutableField.builder() //
                .name("_all") //
                .metaField(true) //
                .enabled(true) //
                .build();
    }

    @Nullable
    @Value.Auxiliary
    @JsonProperty("dynamic")
    public Dynamic getDynamic();

    public default Field getField(final String name) {
        return getFieldsByName().get(name);
    }

    /**
     * Returns a list of fields in the index.
     *
     * @return list of fields in the index.
     */
    @Value.Auxiliary
    @JsonIgnore
    public List<Field> getFields();

    @Value.Derived
    @Value.Auxiliary
    @JsonProperty("properties")
    public default Map<String, Field> getFieldsByName() {
        final Map<String, Field> names = Maps.newLinkedHashMap();
        for (final Field field : getFields()) {
            names.put(field.getName(), field);
        }
        return names;
    }

    /**
     * Returns a list of plugins configured for this mapping.
     *
     * @return list of plugins configured for this mapping.
     */
    @Value.Default
    @Value.Auxiliary
    @JsonIgnore
    public default List<DocumentBuilderPlugin> getPlugins() {
        return ImmutableList.of();
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
    public default Field getSource() {
        return ImmutableField.builder() //
                .name("_source") //
                .enabled(true) //
                .build();
    }
}
