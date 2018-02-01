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
import com.google.common.base.Preconditions;

public class DelegatingAggregationVisitor {
    private final AggregationVisitor delegate;

    public DelegatingAggregationVisitor(final AggregationVisitor delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    public boolean enter(final Aggregation agg) {
        return delegate.enter(agg);
    }

    public boolean enterAdjacencyMatrix(final AdjacencyMatrixAggregation agg) {
        return delegate.enterAdjacencyMatrix(agg);
    }

    public boolean enterAvg(final AvgAggregation agg) {
        return delegate.enterAvg(agg);
    }

    public boolean enterCardinality(final CardinalityAggregation agg) {
        return delegate.enterCardinality(agg);
    }

    public boolean enterDateHistogram(final DateHistogramAggregation agg) {
        return delegate.enterDateHistogram(agg);
    }

    public boolean enterDateRange(final DateRangeAggregation agg) {
        return delegate.enterDateRange(agg);
    }

    public boolean enterExtendedStats(final ExtendedStatsAggregation agg) {
        return delegate.enterExtendedStats(agg);
    }

    public boolean enterFilter(final FilterAggregation agg) {
        return delegate.enterFilter(agg);
    }

    public boolean enterFilters(final FiltersAggregation agg) {
        return delegate.enterFilters(agg);
    }

    public boolean enterGeoBounds(final GeoBoundsAggregation agg) {
        return delegate.enterGeoBounds(agg);
    }

    public boolean enterGeoCentroid(final GeoCentroidAggregation agg) {
        return delegate.enterGeoCentroid(agg);
    }

    public boolean enterGeoDistance(final GeoDistanceAggregation agg) {
        return delegate.enterGeoDistance(agg);
    }

    public boolean enterGeoHashGrid(final GeoHashGridAggregation agg) {
        return delegate.enterGeoHashGrid(agg);
    }

    public boolean enterGlobal(final GlobalAggregation agg) {
        return delegate.enterGlobal(agg);
    }

    public boolean enterHistogram(final HistogramAggregation agg) {
        return delegate.enterHistogram(agg);
    }

    public boolean enterIpRange(final IpRangeAggregation agg) {
        return delegate.enterIpRange(agg);
    }

    public boolean enterMax(final MaxAggregation agg) {
        return delegate.enterMax(agg);
    }

    public boolean enterMin(final MinAggregation agg) {
        return delegate.enterMin(agg);
    }

    public boolean enterMissing(final MissingAggregation agg) {
        return delegate.enterMissing(agg);
    }

    public boolean enterNested(final NestedAggregation agg) {
        return delegate.enterNested(agg);
    }

    public boolean enterPercentileRanks(final PercentileRanksAggregation agg) {
        return delegate.enterPercentileRanks(agg);
    }

    public boolean enterPercentiles(final PercentilesAggregation agg) {
        return delegate.enterPercentiles(agg);
    }

    public boolean enterRange(final RangeAggregation agg) {
        return delegate.enterRange(agg);
    }

    public boolean enterReverseNested(final ReverseNestedAggregation agg) {
        return delegate.enterReverseNested(agg);
    }

    public boolean enterSampler(final SamplerAggregation agg) {
        return delegate.enterSampler(agg);
    }

    public boolean enterSignificantTerms(final SignificantTermsAggregation agg) {
        return delegate.enterSignificantTerms(agg);
    }

    public boolean enterSignificantText(final SignificantTextAggregation agg) {
        return delegate.enterSignificantText(agg);
    }

    public boolean enterStats(final StatsAggregation agg) {
        return delegate.enterStats(agg);
    }

    public boolean enterSum(final SumAggregation agg) {
        return delegate.enterSum(agg);
    }

    public boolean enterTerms(final TermsAggregation agg) {
        return delegate.enterTerms(agg);
    }

    public boolean enterValueCount(final ValueCountAggregation agg) {
        return delegate.enterValueCount(agg);
    }

    public void leave(final Aggregation agg) {
        delegate.leave(agg);
    }

    public void leaveAdjacencyMatrix(final AdjacencyMatrixAggregation agg) {
        delegate.leaveAdjacencyMatrix(agg);
    }

    public void leaveAvg(final AvgAggregation agg) {
        delegate.leaveAvg(agg);
    }

    public void leaveCardinality(final CardinalityAggregation agg) {
        delegate.leaveCardinality(agg);
    }

    public void leaveDateHistogram(final DateHistogramAggregation agg) {
        delegate.leaveDateHistogram(agg);
    }

    public void leaveDateRange(final DateRangeAggregation agg) {
        delegate.leaveDateRange(agg);
    }

    public void leaveExtendedStats(final ExtendedStatsAggregation agg) {
        delegate.leaveExtendedStats(agg);
    }

    public void leaveFilter(final FilterAggregation agg) {
        delegate.leaveFilter(agg);
    }

    public void leaveFilters(final FiltersAggregation agg) {
        delegate.leaveFilters(agg);
    }

    public void leaveGeoBounds(final GeoBoundsAggregation agg) {
        delegate.leaveGeoBounds(agg);
    }

    public void leaveGeoCentroid(final GeoCentroidAggregation agg) {
        delegate.leaveGeoCentroid(agg);
    }

    public void leaveGeoDistance(final GeoDistanceAggregation agg) {
        delegate.leaveGeoDistance(agg);
    }

    public void leaveGeoHashGrid(final GeoHashGridAggregation agg) {
        delegate.leaveGeoHashGrid(agg);
    }

    public void leaveGlobalAggregation(final GlobalAggregation agg) {
        delegate.leaveGlobal(agg);
    }

    public void leaveHistogram(final HistogramAggregation agg) {
        delegate.leaveHistogram(agg);
    }

    public void leaveIpRange(final IpRangeAggregation agg) {
        delegate.leaveIpRange(agg);
    }

    public void leaveMax(final MaxAggregation agg) {
        delegate.leaveMax(agg);
    }

    public void leaveMin(final MinAggregation agg) {
        delegate.leaveMin(agg);
    }

    public void leaveMissing(final MissingAggregation agg) {
        delegate.leaveMissing(agg);
    }

    public void leaveNested(final NestedAggregation agg) {
        delegate.leaveNested(agg);
    }

    public void leavePercentileRanks(final PercentileRanksAggregation agg) {
        delegate.leavePercentileRanks(agg);
    }

    public void leavePercentiles(final PercentilesAggregation agg) {
        delegate.leavePercentiles(agg);
    }

    public void leaveRange(final RangeAggregation agg) {
        delegate.leaveRange(agg);
    }

    public void leaveReverseNested(final ReverseNestedAggregation agg) {
        delegate.leaveReverseNested(agg);
    }

    public void leaveSampler(final SamplerAggregation agg) {
        delegate.leaveSampler(agg);
    }

    public void leaveSignificantTerms(final SignificantTermsAggregation agg) {
        delegate.leaveSignificantTerms(agg);
    }

    public void leaveSignificantText(final SignificantTextAggregation agg) {
        delegate.leaveSignificantText(agg);
    }

    public void leaveStats(final StatsAggregation agg) {
        delegate.leaveStats(agg);
    }

    public void leaveSum(final SumAggregation agg) {
        delegate.leaveSum(agg);
    }

    public void leaveTerms(final TermsAggregation agg) {
        delegate.leaveTerms(agg);
    }

    public void leaveValueCount(final ValueCountAggregation agg) {
        delegate.leaveValueCount(agg);
    }
}
