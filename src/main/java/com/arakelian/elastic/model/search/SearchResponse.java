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

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.Shards;
import com.arakelian.jackson.MapPath;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableSearchResponse.class)
@JsonDeserialize(builder = ImmutableSearchResponse.Builder.class)
@JsonPropertyOrder({ "error", "_scroll_id", "took", "timed_out", "num_reduce_phases", "terminated_early",
        "_shards", "hits" })
public interface SearchResponse extends Serializable {
    @Value.Default
    @Value.Auxiliary
    public default Map<String, MapPath> getAggregations() {
        return ImmutableMap.of();
    }

    @Nullable
    @Value.Auxiliary
    public MapPath getError();

    @Value.Default
    @Value.Auxiliary
    public default SearchHits getHits() {
        return ImmutableSearchHits.builder().build();
    }

    @Nullable
    @Value.Auxiliary
    @JsonProperty("num_reduce_phases")
    public Integer getNumberOfReducePhases();

    @JsonAnyGetter
    @Value.Default
    public default Map<String, Object> getProperties() {
        return ImmutableMap.of();
    }

    @Nullable
    @JsonProperty("_scroll_id")
    public String getScrollId();

    @Nullable
    @Value.Auxiliary
    @JsonProperty("_shards")
    public Shards getShards();

    @Nullable
    public Long getTook();

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public default boolean hasError() {
        return getError() != null;
    }

    @Nullable
    @JsonProperty("terminated_early")
    public Boolean isTerminatedEarly();

    @Nullable
    @JsonProperty("timed_out")
    public Boolean isTimedOut();
}
