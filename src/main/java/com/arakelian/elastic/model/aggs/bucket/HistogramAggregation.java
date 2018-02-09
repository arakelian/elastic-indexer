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
 * A multi-bucket values source based aggregation that can be applied on numeric values extracted
 * from the documents.
 *
 * <p>
 * This aggregation dynamically builds fixed size (a.k.a. interval) buckets over the values. For
 * example, if the documents have a field that holds a price (numeric), we can configure this
 * aggregation to dynamically build buckets with interval 5 (in case of price it may represent $5).
 * </p>
 *
 * <p>
 * When the aggregation executes, the price field of every document will be evaluated and will be
 * rounded down to its closest bucket - for example, if the price is 32 and the bucket size is 5
 * then the rounding will yield 30 and thus the document will "fall" into the bucket that is
 * associated with the key 30. To make this more formal, here is the rounding function that is used:
 * </p>
 *
 * <p>
 * bucket_key = Math.floor((value - offset) / interval) * interval + offset
 * </p>
 *
 * <p>
 * The interval must be a positive decimal, while the offset must be a decimal in [0, interval) (a
 * decimal greater than or equal to 0 and less than interval)
 * </p>
 *
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-histogram-aggregation.html">Histogram
 *      Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/bucket/histogram/HistogramAggregationBuilder.java">HistogramAggregationBuilder.java</a>
 */
@Value.Immutable(copy=false)
@JsonSerialize(as = ImmutableHistogramAggregation.class)
@JsonDeserialize(builder = ImmutableHistogramAggregation.Builder.class)
@JsonTypeName(Aggregation.HISTOGRAM_AGGREGATION)
public interface HistogramAggregation extends BucketAggregation, ValuesSourceAggregation {
    @Nullable
    public Double getInterval();

    @Nullable
    public Double getMaxBound();

    @Nullable
    public Double getMinBound();

    /**
     * Returns the minimum number of hits required before returning a term.
     *
     * @return the minimum number of hits required before returning a term.
     */
    @Nullable
    public Long getMinDocCount();

    @Nullable
    public Double getOffset();

    /**
     * Returns the bucket order.
     *
     * @return the bucket order
     *
     * @see <a href=
     *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/InternalOrder.java">InternalOrder.java</a>
     */
    @Value.Default
    public default List<BucketOrder> getOrder() {
        return ImmutableList.of();
    }

    @Nullable
    public Boolean isKeyed();

    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterHistogram(this)) {
                visitor.leaveHistogram(this);
            }
        } finally {
            visitor.leave(this);
        }
    }
}
