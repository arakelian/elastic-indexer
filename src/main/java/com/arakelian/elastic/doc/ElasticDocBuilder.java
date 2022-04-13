package com.arakelian.elastic.doc;

import com.fasterxml.jackson.databind.JsonNode;

public interface ElasticDocBuilder {
    public CharSequence build(final CharSequence json) throws ElasticDocException;

    public CharSequence build(final JsonNode root) throws ElasticDocException;
}
