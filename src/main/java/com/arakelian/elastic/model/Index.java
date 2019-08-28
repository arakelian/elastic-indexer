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
import java.util.HashMap;
import java.util.Map;

import org.immutables.value.Value;

import com.arakelian.elastic.Views.Elastic.Version7;
import com.arakelian.jackson.JsonPointerNotMatchedFilter;
import com.arakelian.jackson.databind.ExcludeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
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

    public static class MappingsSerializer extends StdSerializer<Map> {
        public MappingsSerializer() {
            super(Map.class);
        }

        @Override
        public void serialize(
                final Map mappings,
                final JsonGenerator generator,
                final SerializerProvider provider) throws IOException, JsonProcessingException {
            final Class<?> activeView = provider.getActiveView();
            if (Version7.class.isAssignableFrom(activeView)) {
                final int size = mappings.size();
                final String key;
                if (size == 1) {
                    key = mappings.keySet().iterator().next().toString();
                } else if (size == 2) {
                    key = "_default_";
                } else {
                    throw new IllegalStateException("mappings constraint violated");
                }

                Preconditions.checkState(mappings.containsKey(key), "mappings does not contain %s", key);
                final Mapping value = (Mapping) mappings.get(key);
                provider.defaultSerializeValue(value, generator);
                return;
            }

            // default serialization
            provider.defaultSerializeValue(mappings, generator);
        }
    }

    public class MappingsDeserializer extends StdDeserializer<Map> {
        public MappingsDeserializer() {
            super(Map.class);
        }

        @Override
        public Map deserialize(final JsonParser p, final DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            final Class<?> activeView = ctxt.getActiveView();
            if (Version7.class.isAssignableFrom(activeView)) {
                HashMap<String, Mapping> mappings = new HashMap<String, Mapping>();
                final Mapping mapping = ctxt.readValue(p, Mapping.class);
                mappings.put("_default", mapping);
                return mappings;
            }

            // default deserialization
            return ctxt.readValue(
                    p,
                    ctxt.getTypeFactory().constructMapType(Map.class, String.class, Mapping.class));
        }
    }

    @Value.Check
    public default void checkMappings() {
        // must have _doc mapping
        final Map<String, Mapping> mappings = getMappings();
        Preconditions.checkState(
                mappings != null && (mappings.size() == 1
                        || mappings.size() == 2 && mappings.containsKey("_default_")),
                "Index \"" + getName()
                        + "\" must contain a single mapping type, or if there are two mapping types one of them must be _default_");
    }

    @JsonIgnore
    @Value.Auxiliary
    public default Mapping getDefaultMapping() {
        final Map<String, Mapping> mappings = getMappings();

        // we're suppose to have a single mapping type as we march towards Elastic 7
        if (mappings.size() == 1) {
            return mappings.values().iterator().next();
        }

        // we have more than one, use the _default_
        return getMapping("_default_");
    }

    public default Mapping getMapping(final String name) {
        // use _doc mapping if type does not have custom mapping
        final Mapping mapping = getMappings().get(Mapping._DOC);
        Preconditions.checkState(
                mapping != null,
                "Index \"" + getName() + "\" does not contain mapping \"" + name + "\"");
        return mapping;
    }

    @Value.Auxiliary
    @JsonProperty("mappings")
    @JsonSerialize(using = MappingsSerializer.class)
    @JsonDeserialize(using = MappingsDeserializer.class)
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
