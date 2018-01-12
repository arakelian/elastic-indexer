package com.arakelian.elastic.query;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Preconditions;

public class ElasticQueryDslVisitor {
    public ElasticQueryDslVisitor(JsonGenerator writer) {
        this.writer = Preconditions.checkNotNull(writer);
    }

    @SuppressWarnings("unused")
    private final JsonGenerator writer;

}
