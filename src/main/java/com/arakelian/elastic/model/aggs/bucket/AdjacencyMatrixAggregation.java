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

package com.arakelian.elastic.model.aggs.bucket;

import java.util.Map;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.aggs.BucketAggregation;
import com.arakelian.elastic.model.search.Query;
import com.arakelian.elastic.search.AggregationVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

/**
 * A bucket aggregation returning a form of adjacency matrix. The request provides a collection of
 * named filter expressions, similar to the {@link FiltersAggregation} request. Each bucket in the
 * response represents a non-empty cell in the matrix of intersecting filters.
 *
 * <p>
 * Note: For N filters the matrix of buckets produced can be N²/2 and so there is a default maximum
 * imposed of 100 filters . This setting can be changed using the index.max_adjacency_matrix_filters
 * index-level setting.
 * </p>
 *
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-adjacency-matrix-aggregation.html">Adjacency
 *      Matrix Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/bucket/adjacency/AdjacencyMatrixAggregationBuilder.java">AdjacencyMatrixAggregationBuilder.java</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableAdjacencyMatrixAggregation.class)
@JsonDeserialize(builder = ImmutableAdjacencyMatrixAggregation.Builder.class)
@JsonTypeName(Aggregation.ADJACENCY_MATRIX_AGGREGATION)
public interface AdjacencyMatrixAggregation extends BucketAggregation {
    @Value.Default
    public default Map<String, Query> getFilters() {
        return ImmutableMap.of();
    }

    @Nullable
    public String getSeparator();

    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterAdjacencyMatrix(this)) {
                visitor.leaveAdjacencyMatrix(this);
            }
        } finally {
            visitor.leave(this);
        }
    }
}
