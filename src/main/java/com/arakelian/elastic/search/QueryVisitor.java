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

@SuppressWarnings("unused")
public interface QueryVisitor {
    public default boolean enter(final Query query) {
        return true;
    }

    public default boolean enterBoolQuery(final BoolQuery bool) {
        return true;
    }

    public default boolean enterExistsQuery(final ExistsQuery existsQuery) {
        return true;
    }

    public default boolean enterFuzzyQuery(final FuzzyQuery fuzzyQuery) {
        return true;
    }

    public default boolean enterIdsQuery(final IdsQuery idsQuery) {
        return true;
    }

    public default boolean enterMatchQuery(final MatchQuery matchQuery) {
        return true;
    }

    public default boolean enterPrefixQuery(final PrefixQuery prefixQuery) {
        return true;
    }

    public default boolean enterQueryStringQuery(final QueryStringQuery queryStringQuery) {
        return true;
    }

    public default boolean enterRangeQuery(final RangeQuery range) {
        return true;
    }

    public default boolean enterRegexpQuery(final RegexpQuery regexpQuery) {
        return true;
    }

    public default boolean enterTermsQuery(final TermsQuery termsQuery) {
        return true;
    }

    public default boolean enterWildcardQuery(final WildcardQuery wildcardQuery) {
        return true;
    }

    public default void leave(final Query query) {
    }

    public default void leaveBoolQuery(final BoolQuery boolQuery) {
    }

    public default void leaveExistsQuery(final ExistsQuery existsQuery) {
    }

    public default void leaveFuzzyQuery(final FuzzyQuery fuzzyQuery) {
    }

    public default void leaveIdsQuery(final IdsQuery idsQuery) {
    }

    public default void leaveMatchQuery(final MatchQuery matchQuery) {
    }

    public default void leavePrefixQuery(final PrefixQuery prefixQuery) {
    }

    public default void leaveQueryStringQuery(final QueryStringQuery queryStringQuery) {
    }

    public default void leaveRangeQuery(final RangeQuery clause) {
    }

    public default void leaveRegexpQuery(final RegexpQuery regexpQuery) {
    }

    public default void leaveTermsQuery(final TermsQuery termsQuery) {
    }

    public default void leaveWildcardQuery(final WildcardQuery wildcardQuery) {
    }
}
