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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.GeoPoint;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/SearchHit.java">Search
 *      Hits</a>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableSearchHits.class)
@JsonDeserialize(builder = ImmutableSearchHits.Builder.class)
@JsonPropertyOrder({ "total", "max_score", "hits" })
public abstract class SearchHits implements Serializable {
    public static final char PATH_SEPARATOR = '/';

    public Map<String, Object> get(final int index) {
        final List<Map<String, Object>> hits = getHits();
        if (index < 0 || index > hits.size()) {
            return ImmutableMap.of();
        }

        final Map<String, Object> hit = hits.get(index);
        if (hit == null) {
            return ImmutableMap.of();
        }

        return hit;
    }

    /** ObjectMapper that should be used for deserialization. **/
    @SuppressWarnings("immutables")
    private transient ObjectMapper mapper;

    @JsonIgnore
    public ObjectMapper getObjectMapper() {
        if (mapper == null) {
            mapper = JacksonUtils.getObjectMapper();
        }
        return mapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    @Value.Default
    @JsonProperty("hits")
    public List<Map<String, Object>> getHits() {
        return ImmutableList.of();
    }

    @Nullable
    @JsonProperty("max_score")
    public abstract Float getMaxScore();

    public String getString(final int index, final String path) {
        return get(index, path, String.class);
    }

    public Object getObject(final int index, final String path) {
        return get(index, path, Object.class);
    }

    public Integer getInt(final int index, final String path) {
        return get(index, path, Integer.class);
    }

    public Float getFloat(final int index, final String path) {
        return get(index, path, Float.class);
    }

    public Double getDouble(final int index, final String path) {
        return get(index, path, Double.class);
    }

    public GeoPoint getGeoPoint(final int index, final String path) {
        return get(index, path, GeoPoint.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(final int index, final String path, final Class<T> clazz) {
        Preconditions.checkArgument(clazz != null, "clazz must be non-null");

        Map<String, Object> map = get(index);

        final int length = path.length();
        for (int start = length > 0 && path.charAt(0) == PATH_SEPARATOR ? 1 : 0; start < length;) {
            final int next = path.indexOf(PATH_SEPARATOR, start);
            final boolean lastSegment = next == -1;
            final int end = lastSegment ? length : next;
            final String segment = path.substring(start, end);

            final Object value = map.get(segment);
            if (value == null) {
                return null;
            }
            if (lastSegment) {
                return getObjectMapper().convertValue(value, clazz);
            }
            if (!(value instanceof Map)) {
                throw new IllegalArgumentException("Expected \"" + path.substring(0, end) + "\" of path \""
                        + path + "\" to resolve to Map but was " + value.getClass().getSimpleName());
            }

            map = map.getClass().cast(value);
            start = next + 1;
        }
        return null;
    }

    @Value.Default
    public int getTotal() {
        return 0;
    }
}
