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

package com.arakelian.elastic.model.aggs;

import java.io.Serializable;
import java.util.List;

import org.immutables.value.Value;

import com.arakelian.elastic.model.aggs.bucket.AdjacencyMatrixAggregation;
import com.arakelian.elastic.model.aggs.bucket.DateHistogramAggregation;
import com.arakelian.elastic.model.aggs.bucket.DateRangeAggregation;
import com.arakelian.elastic.model.aggs.bucket.FilterAggregation;
import com.arakelian.elastic.model.aggs.bucket.FiltersAggregation;
import com.arakelian.elastic.model.aggs.bucket.GeoDistanceAggregation;
import com.arakelian.elastic.model.aggs.bucket.GeoHashGridAggregation;
import com.arakelian.elastic.model.aggs.bucket.GlobalAggregation;
import com.arakelian.elastic.model.aggs.bucket.HistogramAggregation;
import com.arakelian.elastic.model.aggs.bucket.IpRangeAggregation;
import com.arakelian.elastic.model.aggs.bucket.MissingAggregation;
import com.arakelian.elastic.model.aggs.bucket.NestedAggregation;
import com.arakelian.elastic.model.aggs.bucket.RangeAggregation;
import com.arakelian.elastic.model.aggs.bucket.ReverseNestedAggregation;
import com.arakelian.elastic.model.aggs.bucket.SamplerAggregation;
import com.arakelian.elastic.model.aggs.bucket.SignificantTermsAggregation;
import com.arakelian.elastic.model.aggs.bucket.SignificantTextAggregation;
import com.arakelian.elastic.model.aggs.bucket.TermsAggregation;
import com.arakelian.elastic.model.aggs.metrics.AvgAggregation;
import com.arakelian.elastic.model.aggs.metrics.CardinalityAggregation;
import com.arakelian.elastic.model.aggs.metrics.ExtendedStatsAggregation;
import com.arakelian.elastic.model.aggs.metrics.GeoBoundsAggregation;
import com.arakelian.elastic.model.aggs.metrics.GeoCentroidAggregation;
import com.arakelian.elastic.model.aggs.metrics.MaxAggregation;
import com.arakelian.elastic.model.aggs.metrics.MinAggregation;
import com.arakelian.elastic.model.aggs.metrics.PercentileRanksAggregation;
import com.arakelian.elastic.model.aggs.metrics.PercentilesAggregation;
import com.arakelian.elastic.model.aggs.metrics.StatsAggregation;
import com.arakelian.elastic.model.aggs.metrics.SumAggregation;
import com.arakelian.elastic.model.aggs.metrics.ValueCountAggregation;
import com.arakelian.elastic.search.AggregationVisitor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableList;

/**
 * The aggregations framework helps provide aggregated data based on a search query. It is based on
 * simple building blocks called aggregations, that can be composed in order to build complex
 * summaries of the data.
 *
 * <p>
 * An aggregation can be seen as a unit-of-work that builds analytic information over a set of
 * documents. The context of the execution defines what this document set is (e.g. a top-level
 * aggregation executes within the context of the executed query/filters of the search request).
 * </p>
 */
@JsonTypeInfo(property = "type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({ //
        // metric aggregations
        @JsonSubTypes.Type(name = Aggregation.AVG_AGGREGATION, value = AvgAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.CARDINALITY_AGGREGATION, value = CardinalityAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.EXTENDED_STATS_AGGREGATION, value = ExtendedStatsAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.GEO_BOUNDS_AGGREGATION, value = GeoBoundsAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.GEO_CENTROID_AGGREGATION, value = GeoCentroidAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.MAX_AGGREGATION, value = MaxAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.MIN_AGGREGATION, value = MinAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.PERCENTILES_AGGREGATION, value = PercentilesAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.PERCENTILE_RANKS_AGGREGATION, value = PercentileRanksAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.STATS_AGGREGATION, value = StatsAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.SUM_AGGREGATION, value = SumAggregation.class), //
        @JsonSubTypes.Type(name = Aggregation.VALUE_COUNT_AGGREGATION, value = ValueCountAggregation.class), //

        // bucket aggregations
        @JsonSubTypes.Type(name = Aggregation.ADJACENCY_MATRIX_AGGREGATION, value = AdjacencyMatrixAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.DATE_HISTOGRAM_AGGREGATION, value = DateHistogramAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.DATE_RANGE_AGGREGATION, value = DateRangeAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.FILTER_AGGREGATION, value = FilterAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.FILTERS_AGGREGATION, value = FiltersAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.GEOHASH_GRID_AGGREGATION, value = GeoHashGridAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.GEO_DISTANCE_AGGREGATION, value = GeoDistanceAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.GLOBAL_AGGREGATION, value = GlobalAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.HISTOGRAM_AGGREGATION, value = HistogramAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.IP_RANGE_AGGREGATION, value = IpRangeAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.MISSING_AGGREGATION, value = MissingAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.NESTED_AGGREGATION, value = NestedAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.RANGE_AGGREGATION, value = RangeAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.REVERSE_NESTED_AGGREGATION, value = ReverseNestedAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.SAMPLER_AGGREGATION, value = SamplerAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.SIGNIFICANT_TERMS_AGGREGATION, value = SignificantTermsAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.SIGNIFICANT_TEXT_AGGREGATION, value = SignificantTextAggregation.class),
        @JsonSubTypes.Type(name = Aggregation.TERMS_AGGREGATION, value = TermsAggregation.class), //
})
public interface Aggregation extends Serializable {
    // metric aggregations
    public static final String AVG_AGGREGATION = "avg";
    public static final String CARDINALITY_AGGREGATION = "cardinality";
    public static final String EXTENDED_STATS_AGGREGATION = "extended_stats";
    public static final String GEO_BOUNDS_AGGREGATION = "geo_bounds";
    public static final String GEO_CENTROID_AGGREGATION = "geo_centroid";
    public static final String MAX_AGGREGATION = "max";
    public static final String MIN_AGGREGATION = "min";
    public static final String PERCENTILES_AGGREGATION = "percentiles";
    public static final String PERCENTILE_RANKS_AGGREGATION = "percentile_ranks";
    public static final String STATS_AGGREGATION = "stats";
    public static final String SUM_AGGREGATION = "sum";
    public static final String VALUE_COUNT_AGGREGATION = "value_count";

    // bucket aggregations
    public static final String ADJACENCY_MATRIX_AGGREGATION = "adjacency_matrix";
    public static final String DATE_HISTOGRAM_AGGREGATION = "date_histogram";
    public static final String DATE_RANGE_AGGREGATION = "date_range";
    public static final String FILTER_AGGREGATION = "filter";
    public static final String FILTERS_AGGREGATION = "filters";
    public static final String GEOHASH_GRID_AGGREGATION = "geohash_grid";
    public static final String GEO_DISTANCE_AGGREGATION = "geo_distance";
    public static final String GLOBAL_AGGREGATION = "global";
    public static final String HISTOGRAM_AGGREGATION = "histogram";
    public static final String IP_RANGE_AGGREGATION = "ip_range";
    public static final String MISSING_AGGREGATION = "missing";
    public static final String NESTED_AGGREGATION = "nested";
    public static final String RANGE_AGGREGATION = "range";
    public static final String REVERSE_NESTED_AGGREGATION = "reverse_nested";
    public static final String SAMPLER_AGGREGATION = "sampler";
    public static final String SIGNIFICANT_TERMS_AGGREGATION = "significant_terms";
    public static final String SIGNIFICANT_TEXT_AGGREGATION = "significant_text";
    public static final String TERMS_AGGREGATION = "terms";

    static int countNotEmpty(final List<Aggregation> queries) {
        if (queries == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0, size = queries.size(); i < size; i++) {
            final Aggregation q = queries.get(i);
            if (!q.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        visitor.leave(this);
    }

    /**
     * Returns the name of the aggregation
     *
     * @return name of the aggregation
     */
    public String getName();

    @Value.Default
    public default List<Aggregation> getSubAggregations() {
        return ImmutableList.of();
    }

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public default boolean isEmpty() {
        return false;
    }
}
