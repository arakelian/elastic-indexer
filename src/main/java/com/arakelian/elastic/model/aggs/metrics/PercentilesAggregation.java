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

import java.util.List;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.aggs.MetricAggregation;
import com.arakelian.elastic.model.aggs.ValuesSourceAggregation;
import com.arakelian.elastic.model.aggs.metrics.PercentileRanksAggregation.Method;
import com.arakelian.elastic.search.AggregationVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

/**
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-percentile-aggregation.html">Percentiles
 *      Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/metrics/percentiles/PercentileRanksAggregationBuilder.java">PercentileRanksAggregationBuilder.java</a>
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePercentilesAggregation.class)
@JsonDeserialize(builder = ImmutablePercentilesAggregation.Builder.class)
@JsonTypeName(Aggregation.PERCENTILES_AGGREGATION)
public interface PercentilesAggregation extends MetricAggregation, ValuesSourceAggregation {
    @Value.Check
    public default void checkMethod() {
        if (getCompression() != null) {
            Preconditions.checkState(
                    getNumberOfSignificantValueDigits() == null,
                    "compression and number_of_significant_value_digits cannot both be set");
        } else if (getNumberOfSignificantValueDigits() != null) {
            Preconditions.checkState(
                    getCompression() == null,
                    "compression and number_of_significant_value_digits cannot both be set");
        }
    }

    /**
     * Returns the compression. Higher values improve accuracy but also memory usage. Only relevant
     * when using {@link Method#TDIGEST}.
     *
     * @return the compression
     *
     * @see <a href=
     *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics-percentile-aggregation.html#search-aggregations-metrics-percentile-aggregation-compression">Compression</a>
     */
    @Nullable
    public Double getCompression();

    @Nullable
    @Value.Derived
    public default Method getMethod() {
        if (getCompression() != null) {
            return Method.TDIGEST;
        } else if (getNumberOfSignificantValueDigits() != null) {
            return Method.HDR;
        }
        return null;
    }

    /**
     * Returns the number of significant digits in the values. Only relevant when using
     * {@link Method#HDR}.
     *
     * @return the number of significant digits in the values
     */
    @Nullable
    public Integer getNumberOfSignificantValueDigits();

    public List<Double> getPercents();

    @Nullable
    public Boolean isKeyed();

    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterPercentiles(this)) {
                visitor.leavePercentiles(this);
            }
        } finally {
            visitor.leave(this);
        }
    }
}
