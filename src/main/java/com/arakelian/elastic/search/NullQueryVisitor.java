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
import com.arakelian.elastic.model.search.IdsQuery;
import com.arakelian.elastic.model.search.MatchQuery;
import com.arakelian.elastic.model.search.PrefixQuery;
import com.arakelian.elastic.model.search.Query;
import com.arakelian.elastic.model.search.QueryStringQuery;
import com.arakelian.elastic.model.search.RangeQuery;
import com.arakelian.elastic.model.search.RegexpQuery;
import com.arakelian.elastic.model.search.TermsQuery;
import com.arakelian.elastic.model.search.WildcardQuery;

public class NullQueryVisitor implements QueryVisitor {
    public static final NullQueryVisitor INSTANCE = new NullQueryVisitor();

    private NullQueryVisitor() {
    }

    @Override
    public boolean enter(final Query query) {
        return true;
    }

    @Override
    public boolean enterBoolQuery(final BoolQuery bool) {
        return true;
    }

    @Override
    public boolean enterExistsQuery(final ExistsQuery exists) {
        return true;
    }

    @Override
    public boolean enterFuzzyQuery(final FuzzyQuery fuzzyQuery) {
        return true;
    }

    @Override
    public boolean enterIdsQuery(final IdsQuery idsQuery) {
        return true;
    }

    @Override
    public boolean enterMatchQuery(final MatchQuery matchQuery) {
        return true;
    }

    @Override
    public boolean enterPrefixQuery(final PrefixQuery prefixQuery) {
        return true;
    }

    @Override
    public boolean enterQueryStringQuery(final QueryStringQuery queryStringQuery) {
        return true;
    }

    @Override
    public boolean enterRangeQuery(final RangeQuery clause) {
        return true;
    }

    @Override
    public boolean enterRegexpQuery(final RegexpQuery regexpQuery) {
        return true;
    }

    @Override
    public boolean enterTermsQuery(final TermsQuery termsQuery) {
        return true;
    }

    @Override
    public boolean enterWildcardQuery(final WildcardQuery wildcardQuery) {
        return true;
    }

    @Override
    public void leave(final Query query) {
    }

    @Override
    public void leaveBoolQuery(final BoolQuery boolQuery) {
    }

    @Override
    public void leaveExistsQuery(final ExistsQuery existsQuery) {
    }

    @Override
    public void leaveFuzzyQuery(final FuzzyQuery fuzzyQuery) {
    }

    @Override
    public void leaveIdsQuery(final IdsQuery idsQuery) {
    }

    @Override
    public void leaveMatchQuery(final MatchQuery matchQuery) {
    }

    @Override
    public void leavePrefixQuery(final PrefixQuery prefixQuery) {
    }

    @Override
    public void leaveQueryStringQuery(final QueryStringQuery queryStringQuery) {
    }

    @Override
    public void leaveRangeQuery(final RangeQuery clause) {
    }

    @Override
    public void leaveRegexpQuery(final RegexpQuery regexpQuery) {
    }

    @Override
    public void leaveTermsQuery(final TermsQuery termsQuery) {
    }

    @Override
    public void leaveWildcardQuery(final WildcardQuery wildcardQuery) {
    }
}
