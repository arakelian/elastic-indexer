package com.arakelian.elastic.utils;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.arakelian.elastic.model.JsonSelector;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonNodeUtilsTest {
    private JsonNode node;

    @Before
    public void setup() throws IOException {
        final String json = "{\"a\":\"a1\", \"b\":[\"b1\",\"b2\"], \"c\":[{\"ca\":\"c1\",\"cb\":\"c2\"},{\"ca\":\"c3\"}]}";
        node = JacksonUtils.readValue(json, JsonNode.class);
    }

    @Test
    public void testEquals() {
        Assert.assertEquals("\"a1\"", JsonSelector.of("a").read(node).toString());
        Assert.assertEquals("[\"b1\",\"b2\"]", JsonSelector.of("b").read(node).toString());
        Assert.assertEquals("[\"c1\",\"c2\",\"c3\"]", JsonSelector.of("c").read(node).toString());
        Assert.assertEquals("[\"c1\",\"c3\"]", JsonSelector.of("c/ca").read(node).toString());
        Assert.assertEquals("\"c2\"", JsonSelector.of("c/cb").read(node).toString());
        Assert.assertEquals("", JsonSelector.of("d").read(node).toString());
    }
}
