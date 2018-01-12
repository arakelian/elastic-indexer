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

package com.arakelian.elastic.query;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.arakelian.elastic.model.query.BoolQuery;
import com.arakelian.elastic.model.query.ExistsQuery;
import com.arakelian.elastic.model.query.FuzzyQuery;
import com.arakelian.elastic.model.query.IdsQuery;
import com.arakelian.elastic.model.query.MatchQuery;
import com.arakelian.elastic.model.query.PrefixQuery;
import com.arakelian.elastic.model.query.QueryClause;
import com.arakelian.elastic.model.query.QueryStringQuery;
import com.arakelian.elastic.model.query.RangeQuery;
import com.arakelian.elastic.model.query.RegexpQuery;
import com.arakelian.elastic.model.query.TermsQuery;
import com.arakelian.elastic.model.query.WildcardQuery;

@SuppressWarnings("unused")
public class QueryVisitor {
    private final AtomicInteger depth = new AtomicInteger();

    protected int getDepth() {
        return depth.get();
    }

    public boolean enter(final QueryClause clause) {
        depth.incrementAndGet();
        return true;
    }

    public boolean enterBoolQuery(final BoolQuery clause) {
        return true;
    }

    public boolean enterExistsQuery(final ExistsQuery existsQuery) {
        return true;
    }

    public boolean enterFuzzyQuery(final FuzzyQuery fuzzyQuery) {
        return true;
    }

    public boolean enterIdsQuery(final IdsQuery idsQuery) {
        return true;
    }

    public boolean enterMatchQuery(final MatchQuery matchQuery) {
        return true;
    }

    public boolean enterPrefixQuery(final PrefixQuery prefixQuery) {
        return true;
    }

    public boolean enterQueryStringQuery(final QueryStringQuery queryStringQuery) {
        return true;
    }

    public boolean enterRangeQuery(final RangeQuery clause) {
        return true;
    }

    public boolean enterRegexpQuery(final RegexpQuery regexpQuery) {
        return true;
    }

    public boolean enterTermsQuery(final TermsQuery termsQuery) {
        return true;
    }

    public boolean enterWildcardQuery(final WildcardQuery wildcardQuery) {
        return true;
    }

    public void leave(final QueryClause clause) {
        depth.decrementAndGet();
    }

    public void leaveBoolQuery(final BoolQuery clause) {
    }

    public void leaveExistsQuery(final ExistsQuery existsQuery) {
    }

    public void leaveFuzzyQuery(final FuzzyQuery fuzzyQuery) {
    }

    public void leaveIdsQuery(final IdsQuery idsQuery) {
    }

    public void leaveMatchQuery(final MatchQuery matchQuery) {
    }

    public void leavePrefixQuery(final PrefixQuery prefixQuery) {
    }

    public void leaveQueryStringQuery(final QueryStringQuery queryStringQuery) {
    }

    public void leaveRangeQuery(final RangeQuery clause) {
    }

    public void leaveRegexpQuery(final RegexpQuery regexpQuery) {
    }

    public void leaveTermsQuery(final TermsQuery termsQuery) {
    }

    public void leaveWildcardQuery(final WildcardQuery wildcardQuery) {
    }
}
