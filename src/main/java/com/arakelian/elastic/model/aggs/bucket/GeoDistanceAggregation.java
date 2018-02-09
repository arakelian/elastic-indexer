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

import java.util.List;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.aggs.BucketAggregation;
import com.arakelian.elastic.search.AggregationVisitor;
import com.arakelian.jackson.model.GeoPoint;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

/**
 * A multi-bucket aggregation that works on geo_point fields and conceptually works very similar to
 * the range aggregation.
 *
 * <p>
 * The user can define a point of origin and a set of distance range buckets. The aggregation
 * evaluate the distance of each document value from the origin point and determines the buckets it
 * belongs to based on the ranges (a document belongs to a bucket if the distance between the
 * document and the origin falls within the distance range of the bucket).
 * </p>
 *
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-geodistance-aggregation.html">Geo
 *      Distance Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/bucket/range/GeoDistanceAggregationBuilder.java">GeoDistanceAggregationBuilder.java</a>
 */
@Value.Immutable(copy=false)
@JsonSerialize(as = ImmutableGeoDistanceAggregation.class)
@JsonDeserialize(builder = ImmutableGeoDistanceAggregation.Builder.class)
@JsonTypeName(Aggregation.GEO_DISTANCE_AGGREGATION)
public interface GeoDistanceAggregation extends BucketAggregation {
    public enum DistanceType {
        ARC, PLANE;
    }

    public enum Unit {
        M, MI, IN, YD, KM, CM, MM;
    }

    @Nullable
    public DistanceType getDistanceType();

    @Nullable
    public GeoPoint getOrigin();

    @Value.Default
    public default List<Range> getRanges() {
        return ImmutableList.of();
    }

    @Nullable
    public Unit getUnit();

    @Nullable
    public Boolean isKeyed();

    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterGeoDistance(this)) {
                visitor.leaveGeoDistance(this);
            }
        } finally {
            visitor.leave(this);
        }
    }
}
