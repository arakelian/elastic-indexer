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

import com.arakelian.elastic.model.search.BoolQuery;
import com.arakelian.elastic.model.search.ExistsQuery;
import com.arakelian.elastic.model.search.FuzzyQuery;
import com.arakelian.elastic.model.search.GeoBoundingBoxQuery;
import com.arakelian.elastic.model.search.GeoDistanceQuery;
import com.arakelian.elastic.model.search.GeoPolygonQuery;
import com.arakelian.elastic.model.search.GeoShapeQuery;
import com.arakelian.elastic.model.search.IdsQuery;
import com.arakelian.elastic.model.search.MatchQuery;
import com.arakelian.elastic.model.search.MoreLikeThisQuery;
import com.arakelian.elastic.model.search.PrefixQuery;
import com.arakelian.elastic.model.search.Query;
import com.arakelian.elastic.model.search.QueryStringQuery;
import com.arakelian.elastic.model.search.RangeQuery;
import com.arakelian.elastic.model.search.RegexpQuery;
import com.arakelian.elastic.model.search.TermsQuery;
import com.arakelian.elastic.model.search.WildcardQuery;
import com.google.common.base.Preconditions;

public class DelegatingQueryVisitor implements QueryVisitor {
    private final QueryVisitor delegate;

    public DelegatingQueryVisitor(final QueryVisitor delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    @Override
    public boolean enter(final Query query) {
        return delegate.enter(query);
    }

    @Override
    public boolean enterBoolQuery(final BoolQuery bool) {
        return delegate.enterBoolQuery(bool);
    }

    @Override
    public boolean enterExistsQuery(final ExistsQuery exists) {
        return delegate.enterExistsQuery(exists);
    }

    @Override
    public boolean enterFuzzyQuery(final FuzzyQuery fuzzyQuery) {
        return delegate.enterFuzzyQuery(fuzzyQuery);
    }

    @Override
    public boolean enterGeoBoundingBoxQuery(final GeoBoundingBoxQuery geoBoundingBoxQuery) {
        return delegate.enterGeoBoundingBoxQuery(geoBoundingBoxQuery);
    }

    @Override
    public boolean enterGeoDistanceQuery(final GeoDistanceQuery geoDistanceQuery) {
        return delegate.enterGeoDistanceQuery(geoDistanceQuery);
    }

    @Override
    public boolean enterGeoPolygonQuery(final GeoPolygonQuery geoPolygonQuery) {
        return delegate.enterGeoPolygonQuery(geoPolygonQuery);
    }

    @Override
    public boolean enterGeoShapeQuery(final GeoShapeQuery geoShapeQuery) {
        return delegate.enterGeoShapeQuery(geoShapeQuery);
    }

    @Override
    public boolean enterIdsQuery(final IdsQuery idsQuery) {
        return delegate.enterIdsQuery(idsQuery);
    }

    @Override
    public boolean enterMatchQuery(final MatchQuery matchQuery) {
        return delegate.enterMatchQuery(matchQuery);
    }

    @Override
    public boolean enterMoreLikeThisQuery(final MoreLikeThisQuery moreLikeThisQuery) {
        return delegate.enterMoreLikeThisQuery(moreLikeThisQuery);
    }

    @Override
    public boolean enterPrefixQuery(final PrefixQuery prefixQuery) {
        return delegate.enterPrefixQuery(prefixQuery);
    }

    @Override
    public boolean enterQueryStringQuery(final QueryStringQuery queryStringQuery) {
        return delegate.enterQueryStringQuery(queryStringQuery);
    }

    @Override
    public boolean enterRangeQuery(final RangeQuery clause) {
        return delegate.enterRangeQuery(clause);
    }

    @Override
    public boolean enterRegexpQuery(final RegexpQuery regexpQuery) {
        return delegate.enterRegexpQuery(regexpQuery);
    }

    @Override
    public boolean enterTermsQuery(final TermsQuery termsQuery) {
        return delegate.enterTermsQuery(termsQuery);
    }

    @Override
    public boolean enterWildcardQuery(final WildcardQuery wildcardQuery) {
        return delegate.enterWildcardQuery(wildcardQuery);
    }

    @Override
    public void leave(final Query query) {
        delegate.leave(query);
    }

    @Override
    public void leaveBoolQuery(final BoolQuery boolQuery) {
        delegate.leaveBoolQuery(boolQuery);
    }

    @Override
    public void leaveExistsQuery(final ExistsQuery existsQuery) {
        delegate.leaveExistsQuery(existsQuery);
    }

    @Override
    public void leaveFuzzyQuery(final FuzzyQuery fuzzyQuery) {
        delegate.leaveFuzzyQuery(fuzzyQuery);
    }

    @Override
    public void leaveGeoBoundingBoxQuery(final GeoBoundingBoxQuery geoBoundingBoxQuery) {
        delegate.leaveGeoBoundingBoxQuery(geoBoundingBoxQuery);
    }

    @Override
    public void leaveGeoDistanceQuery(final GeoDistanceQuery geoDistanceQuery) {
        delegate.leaveGeoDistanceQuery(geoDistanceQuery);
    }

    @Override
    public void leaveGeoPolygonQuery(final GeoPolygonQuery geoPolygonQuery) {
        delegate.leaveGeoPolygonQuery(geoPolygonQuery);
    }

    @Override
    public void leaveGeoShapeQuery(final GeoShapeQuery geoShapeQuery) {
        delegate.leaveGeoShapeQuery(geoShapeQuery);
    }

    @Override
    public void leaveIdsQuery(final IdsQuery idsQuery) {
        delegate.leaveIdsQuery(idsQuery);
    }

    @Override
    public void leaveMatchQuery(final MatchQuery matchQuery) {
        delegate.leaveMatchQuery(matchQuery);
    }

    @Override
    public void leaveMoreLikeThisQuery(final MoreLikeThisQuery moreLikeThisQuery) {
        delegate.leaveMoreLikeThisQuery(moreLikeThisQuery);
    }

    @Override
    public void leavePrefixQuery(final PrefixQuery prefixQuery) {
        delegate.leavePrefixQuery(prefixQuery);
    }

    @Override
    public void leaveQueryStringQuery(final QueryStringQuery queryStringQuery) {
        delegate.leaveQueryStringQuery(queryStringQuery);
    }

    @Override
    public void leaveRangeQuery(final RangeQuery clause) {
        delegate.leaveRangeQuery(clause);
    }

    @Override
    public void leaveRegexpQuery(final RegexpQuery regexpQuery) {
        delegate.leaveRegexpQuery(regexpQuery);
    }

    @Override
    public void leaveTermsQuery(final TermsQuery termsQuery) {
        delegate.leaveTermsQuery(termsQuery);
    }

    @Override
    public void leaveWildcardQuery(final WildcardQuery wildcardQuery) {
        delegate.leaveWildcardQuery(wildcardQuery);
    }
}
