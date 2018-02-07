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
import java.util.Map;
import java.util.SortedSet;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.jackson.MapPath;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;

@Value.Immutable
@JsonSerialize(as = ImmutableSearchHit.class)
@JsonDeserialize(builder = ImmutableSearchHit.Builder.class)
@JsonPropertyOrder({ "_index", "_type", "_id", "_score", "_source", "matched_queries" })
public abstract class SearchHit implements Serializable {
    @JsonProperty("_id")
    public abstract String getId();

    @JsonProperty("_index")
    public abstract String getIndex();

    @JsonProperty("matched_queries")
    @Value.Default
    @Value.NaturalOrder
    public SortedSet<String> getMatchedQueries() {
        return ImmutableSortedSet.of();
    }

    @JsonAnyGetter
    @Value.Default
    public Map<String, Object> getProperties() {
        return ImmutableMap.of();
    }

    @JsonProperty("_score")
    public abstract Double getScore();

    @JsonProperty("_source")
    @Value.Default
    public Source getSource() {
        return ImmutableSource.builder().build();
    }

    @Value.Default
    public MapPath getHighlight() {
        return MapPath.of();
    }

    @Nullable
    @JsonProperty("_type")
    public abstract String getType();

    public void setObjectMapper(final ObjectMapper mapper) {
        getSource().setObjectMapper(mapper);
    }
}
