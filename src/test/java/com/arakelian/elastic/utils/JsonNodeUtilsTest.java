package com.arakelian.elastic.utils;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.arakelian.elastic.model.JsonSelector;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class JsonNodeUtilsTest {
    private ObjectNode node;

    @Before
    public void setup() throws IOException {
        final String json = "{\"a\":\"a1\", \"b\":[\"b1\",\"b2\"], \"c\":[{\"ca\":\"c1\",\"cb\":\"c2\"},{\"ca\":\"c3\"}], \"x\":[false,3.141569,9223372036854775807]}";
        node = (ObjectNode) JacksonUtils.readValue(json, JsonNode.class);
    }

    @Test
    public void testEquals() {
        Assert.assertEquals("\"a1\"", JsonSelector.of("a").read(node).toString());
        Assert.assertEquals("[\"b1\",\"b2\"]", JsonSelector.of("b").read(node).toString());
        Assert.assertEquals("[\"c1\",\"c2\",\"c3\"]", JsonSelector.of("c").read(node).toString());
        Assert.assertEquals("[\"c1\",\"c3\"]", JsonSelector.of("c/ca").read(node).toString());
        Assert.assertEquals("\"c2\"", JsonSelector.of("c/cb").read(node).toString());
        Assert.assertEquals(null, JsonSelector.of("d").read(node).toString());
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
