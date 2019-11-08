package com.arakelian.elastic.doc;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonNodeFunction {
    public JsonNode apply(JsonNode[] args);
}
