package com.arakelian.elastic.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.model.query.QueryClause;

public class LoggingQueryVisitor extends DelegatingQueryVisitor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingQueryVisitor.class);

    public LoggingQueryVisitor(final QueryVisitor delegate) {
        super(delegate);
    }

    @Override
    public boolean enter(final QueryClause clause) {
        LOGGER.info("Entering {}", clause);
        return super.enter(clause);
    }

    @Override
    public void leave(final QueryClause clause) {
        LOGGER.info("Leaving {}", clause);
        super.leave(clause);
    }
}
