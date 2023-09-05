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
import java.util.List;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.Views.Elastic.Version7;
import com.arakelian.elastic.Views.Elastic.Version8;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.ImmutableList;

/**
 * Search hit response
 * 
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/SearchHit.java">Search
 *      Hits</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableSearchHits.class)
@JsonDeserialize(builder = ImmutableSearchHits.Builder.class)
@JsonPropertyOrder({ "total", "max_score", "hits" })
public abstract class SearchHits implements Serializable {
    public enum Relation {
        EQ, GTE;
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableTotal.class)
    @JsonDeserialize(builder = ImmutableTotal.Builder.class)
    @JsonPropertyOrder({ "total", "relation" })
    public interface Total extends Serializable {
        @Nullable
        public Relation getRelation();

        public long getValue();
    }

    public static class TotalDeserializer extends StdDeserializer<Object> {
        public TotalDeserializer() {
            super(Object.class);
        }

        @Override
        public Object deserialize(final JsonParser p, final DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            final JsonToken token = p.getCurrentToken();
            if (token == JsonToken.START_OBJECT) {
                return ctxt.readValue(p, Total.class);
            } else if (token == JsonToken.VALUE_NUMBER_INT) {
                return ImmutableTotal.builder() //
                        .value(p.getLongValue()) //
                        .build();
            }
            throw new IllegalStateException("Expecting a number");
        }
    }

    public static class TotalSerializer extends StdSerializer<Object> {
        public TotalSerializer() {
            super(Object.class);
        }

        @Override
        public void serialize(
                final Object val,
                final JsonGenerator generator,
                final SerializerProvider provider) throws IOException, JsonProcessingException {
            if (val instanceof Total) {
                final Class<?> activeView = provider.getActiveView();
                if (Version7.class.isAssignableFrom(activeView) || Version8.class.isAssignableFrom(activeView)) {
                    provider.defaultSerializeValue(val, generator);
                } else {
                    final Total tt = (Total) val;
                    generator.writeNumber(tt.getValue());
                }
            } else {
                generator.writeNumber(((Number) val).longValue());
            }
        }
    }

    public SearchHit get(final int index) {
        final List<SearchHit> hits = getHits();
        return hits.get(index);
    }

    @Value.Default
    @JsonProperty("hits")
    public List<SearchHit> getHits() {
        return ImmutableList.of();
    }

    @Nullable
    @JsonProperty("max_score")
    public abstract Float getMaxScore();

    @JsonIgnore
    @Value.Derived
    public int getSize() {
        return getHits().size();
    }

    @Value.Lazy
    @JsonIgnore
    public long getTotal() {
        final Total total = getTotalObject();
        if (total != null) {
            return total.getValue();
        }
        return -1;
    }

    @Nullable
    @JsonProperty("total")
    @JsonSerialize(using = TotalSerializer.class)
    @JsonDeserialize(using = TotalDeserializer.class)
    @Value.Auxiliary
    public abstract Total getTotalObject();

    @Value.Lazy
    @JsonIgnore
    public Relation getTotalRelation() {
        final Total total = getTotalObject();
        if (total != null) {
            return total.getRelation();
        }
        return null;
    }

    public void setObjectMapper(final ObjectMapper objectMapper) {
        for (final SearchHit hit : getHits()) {
            hit.setObjectMapper(objectMapper);
        }
    }
}
