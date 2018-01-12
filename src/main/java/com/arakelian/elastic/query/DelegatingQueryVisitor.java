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
import com.google.common.base.Preconditions;

public class DelegatingQueryVisitor extends QueryVisitor {
    private final QueryVisitor delegate;

    public DelegatingQueryVisitor(final QueryVisitor delegate) {
        this.delegate = Preconditions.checkNotNull(delegate);
    }

    @Override
    public boolean enter(final QueryClause clause) {
        return delegate.enter(clause);
    }

    @Override
    public boolean enterBoolQuery(final BoolQuery clause) {
        return delegate.enterBoolQuery(clause);
    }

    @Override
    public boolean enterExistsQuery(final ExistsQuery existsQuery) {
        return delegate.enterExistsQuery(existsQuery);
    }

    @Override
    public boolean enterFuzzyQuery(final FuzzyQuery fuzzyQuery) {
        return delegate.enterFuzzyQuery(fuzzyQuery);
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
    public void leave(final QueryClause clause) {
        delegate.leave(clause);
    }

    @Override
    public void leaveBoolQuery(final BoolQuery clause) {
        delegate.leaveBoolQuery(clause);
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
    public void leaveIdsQuery(final IdsQuery idsQuery) {
        delegate.leaveIdsQuery(idsQuery);
    }

    @Override
    public void leaveMatchQuery(final MatchQuery matchQuery) {
        delegate.leaveMatchQuery(matchQuery);
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
