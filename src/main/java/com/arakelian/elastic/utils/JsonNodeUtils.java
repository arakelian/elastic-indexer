package com.arakelian.elastic.utils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.base.Preconditions;

public class JsonNodeUtils {
    public static class ValueCollector implements Consumer<JsonNode> {
        private JsonNode value;

        @Override
        public void accept(final JsonNode node) {
            if (node == null || node.isMissingNode()) {
                return;
            }

            // unwrap arrays
            if (node.isArray()) {
                for (int i = 0, size = node.size(); i < size; i++) {
                    accept(node.get(i));
                }
                return;
            }

            // unwrap objects
            if (node.isObject()) {
                final Iterator<JsonNode> children = node.elements();
                while (children.hasNext()) {
                    final JsonNode child = children.next();
                    accept(child);
                }
                return;
            }

            if (value == null) {
                // first value we received
                value = node;
                return;
            }

            if (!(value instanceof ArrayNode)) {
                // initialize new array with value we have already received
                value = JacksonUtils.getObjectMapper().createArrayNode().add(value);
            }

            // add value
            final ArrayNode array = (ArrayNode) value;
            array.add(node);
        }

        public JsonNode getValue() {
            return value == null ? MissingNode.getInstance() : value;
        }
    }

    public static void read(final JsonNode node, final Consumer<JsonNode> consumer, final List<String> path) {
        Preconditions.checkState(consumer != null, "consumer must be non-null");
        Preconditions.checkState(path != null, "path must be non-null");
        read(node, consumer, path, 0);
    }

    public static JsonNode read(final JsonNode node, final List<String> path) {
        final ValueCollector collector = new ValueCollector();
        read(node, collector, path, 0);
        return collector.getValue();
    }

    private static void read(
            final JsonNode node,
            final Consumer<JsonNode> consumer,
            final List<String> path,
            final int depth) {
        if (node == null) {
            return;
        }

        if (node.isNull() || node.isMissingNode()) {
            return;
        }

        if (node.isArray()) {
            for (int i = 0, size = node.size(); i < size; i++) {
                read(node.get(i), consumer, path, depth);
            }
            return;
        }

        if (depth == path.size()) {
            consumer.accept(node);
            return;
        }

        if (node.isObject()) {
            final String name = path.get(depth);
            read(node.path(name), consumer, path, depth + 1);
        }
    }

    private JsonNodeUtils() {
        // utility class
    }
}
