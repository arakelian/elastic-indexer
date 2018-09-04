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

import java.util.SortedSet;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.jackson.AbstractMapPath;
import com.arakelian.jackson.MapPath;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableSortedSet;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableSearchHit.class)
@JsonDeserialize(builder = ImmutableSearchHit.Builder.class)
@JsonPropertyOrder({ "_index", "_type", "_id", "_score", "_source", "matched_queries" })
public abstract class SearchHit extends AbstractMapPath {
    @Value.Default
    public MapPath getHighlight() {
        return MapPath.of();
    }

    @JsonProperty("_id")
    public abstract String getId();

    @Nullable
    @JsonProperty("_index")
    public abstract String getIndex();

    @JsonProperty("matched_queries")
    @Value.Default
    @Value.NaturalOrder
    public SortedSet<String> getMatchedQueries() {
        return ImmutableSortedSet.of();
    }

    /**
     * Returns the score of record.
     *
     * Note that when custom sorting is used, Elastic will omit the scoring and return a null value.
     *
     * @return the score of record, or null if scoring was not applied
     */
    @Nullable
    @JsonProperty("_score")
    public abstract Double getScore();

    @JsonProperty("_source")
    @Value.Default
    public Source getSource() {
        return ImmutableSource.builder().build();
    }

    @Nullable
    @JsonProperty("_type")
    public abstract String getType();

    @Override
    public void setObjectMapper(final ObjectMapper mapper) {
        getSource().setObjectMapper(mapper);
    }
}
