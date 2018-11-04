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

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

/**
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/SearchHit.java">Search
 *      Hits</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableSearchHits.class)
@JsonDeserialize(builder = ImmutableSearchHits.Builder.class)
@JsonPropertyOrder({ "total", "max_score", "hits" })
public abstract class SearchHits implements Serializable {
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

    @Value.Default
    public long getTotal() {
        return 0;
    }

    public void setObjectMapper(final ObjectMapper objectMapper) {
        for (final SearchHit hit : getHits()) {
            hit.setObjectMapper(objectMapper);
        }
    }
}
