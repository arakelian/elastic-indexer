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

import java.util.concurrent.atomic.AtomicInteger;

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
public class AggregationVisitor {
    private final AtomicInteger depth = new AtomicInteger();

    public boolean enter(final Aggregation agg) {
        depth.incrementAndGet();
        return true;
    }

    public boolean enterAdjacencyMatrix(final AdjacencyMatrixAggregation agg) {
        return true;
    }

    public boolean enterAvg(final AvgAggregation agg) {
        return true;
    }

    public boolean enterCardinality(final CardinalityAggregation agg) {
        return true;
    }

    public boolean enterDateHistogram(final DateHistogramAggregation agg) {
        return true;
    }

    public boolean enterDateRange(final DateRangeAggregation agg) {
        return true;
    }

    public boolean enterExtendedStats(final ExtendedStatsAggregation agg) {
        return true;
    }

    public boolean enterFilter(final FilterAggregation agg) {
        return true;
    }

    public boolean enterFilters(final FiltersAggregation agg) {
        return true;
    }

    public boolean enterGeoBounds(final GeoBoundsAggregation agg) {
        return true;
    }

    public boolean enterGeoCentroid(final GeoCentroidAggregation agg) {
        return true;
    }

    public boolean enterGeoDistance(final GeoDistanceAggregation agg) {
        return true;
    }

    public boolean enterGeoHashGrid(final GeoHashGridAggregation agg) {
        return true;
    }

    public boolean enterGlobal(final GlobalAggregation agg) {
        return true;
    }

    public boolean enterHistogram(final HistogramAggregation agg) {
        return true;
    }

    public boolean enterIpRange(final IpRangeAggregation agg) {
        return true;
    }

    public boolean enterMax(final MaxAggregation agg) {
        return true;
    }

    public boolean enterMin(final MinAggregation agg) {
        return true;
    }

    public boolean enterMissing(final MissingAggregation agg) {
        return true;
    }

    public boolean enterNested(final NestedAggregation agg) {
        return true;
    }

    public boolean enterPercentileRanks(final PercentileRanksAggregation agg) {
        return true;
    }

    public boolean enterPercentiles(final PercentilesAggregation agg) {
        return true;
    }

    public boolean enterRange(final RangeAggregation agg) {
        return true;
    }

    public boolean enterReverseNested(final ReverseNestedAggregation agg) {
        return true;
    }

    public boolean enterSampler(final SamplerAggregation agg) {
        return true;
    }

    public boolean enterSignificantTerms(final SignificantTermsAggregation agg) {
        return true;
    }

    public boolean enterSignificantText(final SignificantTextAggregation agg) {
        return true;
    }

    public boolean enterStats(final StatsAggregation agg) {
        return true;
    }

    public boolean enterSum(final SumAggregation agg) {
        return true;
    }

    public boolean enterTerms(final TermsAggregation agg) {
        return true;
    }

    public boolean enterValueCount(final ValueCountAggregation agg) {
        return true;
    }

    public void leave(final Aggregation agg) {
        depth.decrementAndGet();
    }

    public void leaveAdjacencyMatrix(final AdjacencyMatrixAggregation agg) {
    }

    public void leaveAvg(final AvgAggregation agg) {
    }

    public void leaveCardinality(final CardinalityAggregation agg) {
    }

    public void leaveDateHistogram(final DateHistogramAggregation agg) {
    }

    public void leaveDateRange(final DateRangeAggregation agg) {
    }

    public void leaveExtendedStats(final ExtendedStatsAggregation agg) {
    }

    public void leaveFilter(final FilterAggregation agg) {
    }

    public void leaveFilters(final FiltersAggregation agg) {
    }

    public void leaveGeoBounds(final GeoBoundsAggregation agg) {
    }

    public void leaveGeoCentroid(final GeoCentroidAggregation agg) {
    }

    public void leaveGeoDistance(final GeoDistanceAggregation agg) {
    }

    public void leaveGeoHashGrid(final GeoHashGridAggregation agg) {
    }

    public void leaveGlobal(final GlobalAggregation agg) {
    }

    public void leaveHistogram(final HistogramAggregation agg) {
    }

    public void leaveIpRange(final IpRangeAggregation agg) {
    }

    public void leaveMax(final MaxAggregation agg) {
    }

    public void leaveMin(final MinAggregation agg) {
    }

    public void leaveMissing(final MissingAggregation agg) {
    }

    public void leaveNested(final NestedAggregation agg) {
    }

    public void leavePercentileRanks(final PercentileRanksAggregation agg) {
    }

    public void leavePercentiles(final PercentilesAggregation agg) {
    }

    public void leaveRange(final RangeAggregation agg) {
    }

    public void leaveReverseNested(final ReverseNestedAggregation agg) {
    }

    public void leaveSampler(final SamplerAggregation agg) {
    }

    public void leaveSignificantTerms(final SignificantTermsAggregation agg) {
    }

    public void leaveSignificantText(final SignificantTextAggregation agg) {
    }

    public void leaveStats(final StatsAggregation agg) {
    }

    public void leaveSum(final SumAggregation agg) {
    }

    public void leaveTerms(final TermsAggregation agg) {
    }

    public void leaveValueCount(final ValueCountAggregation agg) {
    }

    protected int getDepth() {
        return depth.get();
    }
}
