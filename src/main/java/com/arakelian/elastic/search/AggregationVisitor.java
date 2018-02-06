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

package com.arakelian.elastic.search;

import com.arakelian.elastic.model.aggs.Aggregation;
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

@SuppressWarnings("unused")
public interface AggregationVisitor {
    public default boolean enter(final Aggregation agg) {
        return true;
    }

    public default boolean enterAdjacencyMatrix(final AdjacencyMatrixAggregation agg) {
        return true;
    }

    public default boolean enterAvg(final AvgAggregation agg) {
        return true;
    }

    public default boolean enterCardinality(final CardinalityAggregation agg) {
        return true;
    }

    public default boolean enterDateHistogram(final DateHistogramAggregation agg) {
        return true;
    }

    public default boolean enterDateRange(final DateRangeAggregation agg) {
        return true;
    }

    public default boolean enterExtendedStats(final ExtendedStatsAggregation agg) {
        return true;
    }

    public default boolean enterFilter(final FilterAggregation agg) {
        return true;
    }

    public default boolean enterFilters(final FiltersAggregation agg) {
        return true;
    }

    public default boolean enterGeoBounds(final GeoBoundsAggregation agg) {
        return true;
    }

    public default boolean enterGeoCentroid(final GeoCentroidAggregation agg) {
        return true;
    }

    public default boolean enterGeoDistance(final GeoDistanceAggregation agg) {
        return true;
    }

    public default boolean enterGeoHashGrid(final GeoHashGridAggregation agg) {
        return true;
    }

    public default boolean enterGlobal(final GlobalAggregation agg) {
        return true;
    }

    public default boolean enterHistogram(final HistogramAggregation agg) {
        return true;
    }

    public default boolean enterIpRange(final IpRangeAggregation agg) {
        return true;
    }

    public default boolean enterMax(final MaxAggregation agg) {
        return true;
    }

    public default boolean enterMin(final MinAggregation agg) {
        return true;
    }

    public default boolean enterMissing(final MissingAggregation agg) {
        return true;
    }

    public default boolean enterNested(final NestedAggregation agg) {
        return true;
    }

    public default boolean enterPercentileRanks(final PercentileRanksAggregation agg) {
        return true;
    }

    public default boolean enterPercentiles(final PercentilesAggregation agg) {
        return true;
    }

    public default boolean enterRange(final RangeAggregation agg) {
        return true;
    }

    public default boolean enterReverseNested(final ReverseNestedAggregation agg) {
        return true;
    }

    public default boolean enterSampler(final SamplerAggregation agg) {
        return true;
    }

    public default boolean enterSignificantTerms(final SignificantTermsAggregation agg) {
        return true;
    }

    public default boolean enterSignificantText(final SignificantTextAggregation agg) {
        return true;
    }

    public default boolean enterStats(final StatsAggregation agg) {
        return true;
    }

    public default boolean enterSum(final SumAggregation agg) {
        return true;
    }

    public default boolean enterTerms(final TermsAggregation agg) {
        return true;
    }

    public default boolean enterValueCount(final ValueCountAggregation agg) {
        return true;
    }

    public default void leave(final Aggregation agg) {
    }

    public default void leaveAdjacencyMatrix(final AdjacencyMatrixAggregation agg) {
    }

    public default void leaveAvg(final AvgAggregation agg) {
    }

    public default void leaveCardinality(final CardinalityAggregation agg) {
    }

    public default void leaveDateHistogram(final DateHistogramAggregation agg) {
    }

    public default void leaveDateRange(final DateRangeAggregation agg) {
    }

    public default void leaveExtendedStats(final ExtendedStatsAggregation agg) {
    }

    public default void leaveFilter(final FilterAggregation agg) {
    }

    public default void leaveFilters(final FiltersAggregation agg) {
    }

    public default void leaveGeoBounds(final GeoBoundsAggregation agg) {
    }

    public default void leaveGeoCentroid(final GeoCentroidAggregation agg) {
    }

    public default void leaveGeoDistance(final GeoDistanceAggregation agg) {
    }

    public default void leaveGeoHashGrid(final GeoHashGridAggregation agg) {
    }

    public default void leaveGlobal(final GlobalAggregation agg) {
    }

    public default void leaveHistogram(final HistogramAggregation agg) {
    }

    public default void leaveIpRange(final IpRangeAggregation agg) {
    }

    public default void leaveMax(final MaxAggregation agg) {
    }

    public default void leaveMin(final MinAggregation agg) {
    }

    public default void leaveMissing(final MissingAggregation agg) {
    }

    public default void leaveNested(final NestedAggregation agg) {
    }

    public default void leavePercentileRanks(final PercentileRanksAggregation agg) {
    }

    public default void leavePercentiles(final PercentilesAggregation agg) {
    }

    public default void leaveRange(final RangeAggregation agg) {
    }

    public default void leaveReverseNested(final ReverseNestedAggregation agg) {
    }

    public default void leaveSampler(final SamplerAggregation agg) {
    }

    public default void leaveSignificantTerms(final SignificantTermsAggregation agg) {
    }

    public default void leaveSignificantText(final SignificantTextAggregation agg) {
    }

    public default void leaveStats(final StatsAggregation agg) {
    }

    public default void leaveSum(final SumAggregation agg) {
    }

    public default void leaveTerms(final TermsAggregation agg) {
    }

    public default void leaveValueCount(final ValueCountAggregation agg) {
    }
}
