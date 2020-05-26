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
 * A multi-bucket aggregation similar to @{link {@link HistogramAggregation}} except it can only be
 * applied on date values.
 *
 * <p>
 * Since dates are represented in Elasticsearch internally as long values, it is possible to use the
 * normal histogram on dates as well, though accuracy will be compromised. The reason for this is in
 * the fact that time based intervals are not fixed (think of leap years and on the number of days
 * in a month). For this reason, we need special support for time based data.
 * </p>
 *
 * <p>
 * From a functionality perspective, this histogram supports the same features as the normal
 * histogram. The main difference is that the interval can be specified by date/time expressions.
 * </p>
 *
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-datehistogram-aggregation.html">Date
 *      Histogram Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/bucket/histogram/DateHistogramAggregationBuilder.java">DateHistogramAggregationBuilder.java</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableDateHistogramAggregation.class)
@JsonDeserialize(builder = ImmutableDateHistogramAggregation.Builder.class)
@JsonTypeName(Aggregation.DATE_HISTOGRAM_AGGREGATION)
public interface DateHistogramAggregation extends BucketAggregation, ValuesSourceAggregation {
    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterDateHistogram(this)) {
                visitor.leaveDateHistogram(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @Nullable
    public Long getExtendedBoundsMax();

    @Nullable
    public Long getExtendedBoundsMin();

    @Nullable
    public String getInterval();

    /**
     * Returns the minimum number of hits required before returning a term.
     *
     * @return the minimum number of hits required before returning a term.
     */
    @Nullable
    public Long getMinDocCount();

    @Nullable
    public Long getOffset();

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
}
