package com.arakelian.elastic.query;

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
    public boolean enter(final QueryClause clause) {
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
