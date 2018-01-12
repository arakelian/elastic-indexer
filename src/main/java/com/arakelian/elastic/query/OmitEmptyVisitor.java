package com.arakelian.elastic.query;

import com.arakelian.elastic.model.query.QueryClause;

public class OmitEmptyVisitor extends DelegatingQueryVisitor {
    public OmitEmptyVisitor(final QueryVisitor delegate) {
        super(delegate);
    }

    @Override
    public boolean enter(final QueryClause clause) {
        if (clause.isEmpty()) {
            return false;
        }
        return super.enter(clause);
    }

    @Override
    public void leave(final QueryClause clause) {
        if (!clause.isEmpty()) {
            super.leave(clause);
        }
    }
}
