package com.arakelian.elastic.utils;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.model.JsonSelector;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class JsonNodeUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonNodeUtilsTest.class);

    private ObjectNode node;

    @Before
    public void setup() throws IOException {
        final String json = "{\"a\":\"a1\", \"b\":[\"b1\",\"b2\"], \"c\":[{\"ca\":\"c1\",\"cb\":\"c2\"},{\"ca\":\"c3\"}], \"x\":[false,3.141569,9223372036854775807]}";
        node = (ObjectNode) JacksonUtils.readValue(json, JsonNode.class);
    }

    @Test
    public void testEquals() {
        Assert.assertEquals("\"a1\"", read("a"));
        Assert.assertEquals("[\"b1\",\"b2\"]", read("b"));
        Assert.assertEquals("[\"c1\",\"c2\",\"c3\"]", read("c"));
        Assert.assertEquals("[\"c1\",\"c3\"]", read("c/ca"));
        Assert.assertEquals("\"c2\"", read("c/cb"));
        Assert.assertEquals("", read("d"));
    }

    private String read(String selector) {
        final JsonSelector js = JsonSelector.of(selector);
        final JsonNode value = js.read(node);
        LOGGER.info(
                "{} read as {}: {} (len:{})",
                js,
                value.getClass().getName(),
                value.toString(),
                value.toString().length());
        return value.isMissingNode() ? "" : value.toString();
    }

    @Test
    public void testHashCode() {
        // value starts as 'a1'
        Assert.assertEquals(3056, JsonSelector.of("a").hashCode(node));

        // no change, and thus hash doesn't change
        node.set("a", TextNode.valueOf("a1"));
        Assert.assertEquals(3056, JsonSelector.of("a").hashCode(node));

        // value changes slightly
        node.set("a", TextNode.valueOf("a2"));
        Assert.assertEquals(3057, JsonSelector.of("a").hashCode(node));
    }

    @Test
    public void testMd5() {
        Assert.assertEquals("Wwg6eDDfmviapgQkX+LVQw", JsonSelector.of("x").md5(node));

        // small change results in very different hash
        node.set("x", TextNode.valueOf("a2"));
        Assert.assertEquals("EC75pJASF4svvoaTpw9rpg", JsonSelector.of("x").md5(node));
    }
}
