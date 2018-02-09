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

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.aggs.BucketAggregation;
import com.arakelian.elastic.search.AggregationVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A multi-bucket aggregation that works on geo_point fields and groups points into buckets that
 * represent cells in a grid. The resulting grid can be sparse and only contains cells that have
 * matching data. Each cell is labeled using a geohash which is of user-definable precision.
 *
 * <ul>
 * <li>High precision geohashes have a long string length and represent cells that cover only a
 * small area.</li>
 * <li>Low precision geohashes have a short string length and represent cells that each cover a
 * large area.</li>
 * </ul>
 *
 * <p>
 * Geohashes used in this aggregation can have a choice of precision between 1 and 12.
 * </p>
 *
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-geohashgrid-aggregation.html">GeoHash
 *      Grid Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/bucket/geogrid/GeoGridAggregationBuilder.java">GeoGridAggregationBuilder.java</a>
 */
@Value.Immutable(copy=false)
@JsonSerialize(as = ImmutableGeoHashGridAggregation.class)
@JsonDeserialize(builder = ImmutableGeoHashGridAggregation.Builder.class)
@JsonTypeName(Aggregation.GEOHASH_GRID_AGGREGATION)
public interface GeoHashGridAggregation extends BucketAggregation {
    @Nullable
    public Integer getPrecision();

    @Nullable
    public Integer getRequiredSize();

    @Nullable
    public Integer getShardSize();

    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterGeoHashGrid(this)) {
                visitor.leaveGeoHashGrid(this);
            }
        } finally {
            visitor.leave(this);
        }
    }
}
