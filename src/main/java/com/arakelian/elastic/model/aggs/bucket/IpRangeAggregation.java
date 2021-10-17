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
import com.arakelian.elastic.model.aggs.ValuesSourceAggregation;
import com.arakelian.elastic.search.AggregationVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

/**
 * IP range aggregation
 * 
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-iprange-aggregation.html">IP
 *      Range Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/bucket/range/IpRangeAggregationBuilder.java">IpRangeAggregationBuilder.java</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableIpRangeAggregation.class)
@JsonDeserialize(builder = ImmutableIpRangeAggregation.Builder.class)
@JsonTypeName(Aggregation.IP_RANGE_AGGREGATION)
public interface IpRangeAggregation extends BucketAggregation, ValuesSourceAggregation {
    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterIpRange(this)) {
                visitor.leaveIpRange(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @Value.Default
    public default List<Range> getRanges() {
        return ImmutableList.of();
    }

    @Nullable
    public Boolean isKeyed();
}
