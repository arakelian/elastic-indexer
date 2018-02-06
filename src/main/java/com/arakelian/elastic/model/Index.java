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
import java.util.Map;

import org.immutables.value.Value;

import com.arakelian.jackson.JsonPointerNotMatchedFilter;
import com.arakelian.jackson.databind.ExcludeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableIndex.class)
@JsonDeserialize(builder = ImmutableIndex.Builder.class)
@JsonPropertyOrder({ "name", "settings", "mappings" })
public interface Index extends Serializable {
    public static class WithoutNameSerializer extends ExcludeSerializer<Index> {
        private static final JsonPointerNotMatchedFilter filter = new JsonPointerNotMatchedFilter("/name");

        public WithoutNameSerializer(final JsonSerializer<Object> delegate) {
            super(Index.class, filter, delegate);
        }
    }

    @Value.Check
    public default void checkMappings() {
        // must have _default_ mapping
        final Map<String, Mapping> mappings = getMappings();
        final Mapping mapping = mappings != null ? mappings.get(Mapping._DEFAULT_) : null;
        Preconditions.checkState(
                mapping != null,
                "Index \"" + getName() + "\" does not contain _default_ mapping");
    }

    @JsonIgnore
    @Value.Auxiliary
    public default Mapping getDefaultMapping() {
        return getMapping(Mapping._DEFAULT_);
    }

    public default Mapping getMapping(final String name) {
        // use _default_ mapping if type does not have custom mapping
        final Mapping mapping = getMappings().get(Mapping._DEFAULT_);
        Preconditions.checkState(
                mapping != null,
                "Index \"" + getName() + "\" does not contain mapping \"" + name + "\"");
        return mapping;
    }

    @Value.Auxiliary
    @JsonProperty("mappings")
    public Map<String, Mapping> getMappings();

    /**
     * Returns the index name
     *
     * @return name of index
     */
    public String getName();

    @Value.Default
    @Value.Auxiliary
    @JsonProperty("settings")
    public default IndexSettings getSettings() {
        return ImmutableIndexSettings.builder().build();
    }
}
