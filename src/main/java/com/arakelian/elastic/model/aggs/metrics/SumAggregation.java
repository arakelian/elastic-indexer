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

package com.arakelian.elastic.model.aggs.metrics;

import org.immutables.value.Value;

import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.aggs.MetricAggregation;
import com.arakelian.elastic.model.aggs.ValuesSourceAggregation;
import com.arakelian.elastic.search.AggregationVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Sum aggregation
 * 
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-sum-aggregation.html">Sum
 *      Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/metrics/sum/SumAggregationBuilder.java">SumAggregationBuilder.java</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableSumAggregation.class)
@JsonDeserialize(builder = ImmutableSumAggregation.Builder.class)
@JsonTypeName(Aggregation.SUM_AGGREGATION)
public interface SumAggregation extends MetricAggregation, ValuesSourceAggregation {
    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterSum(this)) {
                visitor.leaveSum(this);
            }
        } finally {
            visitor.leave(this);
        }
    }
}
