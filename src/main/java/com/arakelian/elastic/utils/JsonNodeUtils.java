package com.arakelian.elastic.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;

public class JsonNodeUtils {
    public static class DigestCollector extends JsonNodeCollector {
        private final MessageDigest digester;

        public DigestCollector(final String algorithm) {
            try {
                digester = MessageDigest.getInstance(algorithm);
            } catch (final NoSuchAlgorithmException e) {
                throw new IllegalStateException("Unable to create digester with algorithm " + algorithm, e);
            }
        }

        public String getValue() {
            final String digest = BaseEncoding.base64().omitPadding().encode(digester.digest());
            return digest;
        }

        private void digest(final BigDecimal val) {
            digest(val.toString());
        }

        private void digest(final BigInteger val) {
            digest(val.toString());
        }

        private void digest(final boolean val) {
            digester.update((byte) (val ? 17 : 19));
        }

        private void digest(final CharSequence val) {
            if (val == null) {
                return;
            }
            for (int i = 0, length = val.length(); i < length; i++) {
                char ch = val.charAt(i);
                digester.update((byte) (ch & 0xFF));
                ch >>= 8;
                digester.update((byte) (ch & 0xFF));
            }
        }

        private void digest(final double val) {
            digest(Double.doubleToRawLongBits(val));
        }

        private void digest(final float val) {
            digest(Float.floatToRawIntBits(val));
        }

        private void digest(int val) {
            for (int i = 0; i < 4; i++) {
                digester.update((byte) (val & 0xFF));
                val >>= 8;
            }
        }

        private void digest(long val) {
            for (int i = 0; i < 8; i++) {
                digester.update((byte) (val & 0xFF));
                val >>= 8;
            }
        }

        private void digest(short val) {
            for (int i = 0; i < 2; i++) {
                digester.update((byte) (val & 0xFF));
                val >>= 8;
            }
        }

        @Override
        protected void collect(final JsonNode node) {
            digest(node);
        }

        protected void digest(final JsonNode node) {
            if (node.isTextual()) {
                digest(node.asText());
                return;
            }

            if (node.isFloatingPointNumber()) {
                if (node.isFloat()) {
                    digest(node.floatValue());
                    return;
                }
                if (node.isDouble()) {
                    digest(node.doubleValue());
                    return;
                }
                if (node.isBigDecimal()) {
                    digest(node.decimalValue());
                    return;
                }
            }

            if (node.isIntegralNumber()) {
                if (node.isShort()) {
                    digest(node.shortValue());
                    return;
                }
                if (node.canConvertToInt()) {
                    digest(node.intValue());
                    return;
                }
                if (node.canConvertToLong()) {
                    digest(node.longValue());
                    return;
                }
                if (node.isBigInteger()) {
                    digest(node.bigIntegerValue());
                    return;
                }
            }

            if (node.isBoolean()) {
                digest(node.booleanValue());
                return;
            }

            if (node.isBinary()) {
                byte[] value;
                try {
                    value = node.binaryValue();
                } catch (final IOException e) {
                    throw new UncheckedIOException(e.getMessage(), e);
                }
                digester.update(value);
                return;
            }
        }

        @Override
        protected void unwrapArray(final JsonNode node) {
            // hash the length of the array
            digest(node.size());

            // continue unwrapping
            super.unwrapArray(node);
        }

        @Override
        protected void unwrapObject(final JsonNode node) {
            // unwrap properties in sorted field order (we don't care about ordering)
            final List<String> names = Lists.newArrayList(node.fieldNames());
            Collections.sort(names);
            for (final String name : names) {
                digest(name);
                final JsonNode child = node.get(name);
                accept(child);
            }
        }
    }

    public static class HashCodeCollector extends JsonNodeCollector {
        private long hashCode;

        public long getValue() {
            return hashCode;
        }

        @Override
        protected void collect(final JsonNode node) {
            hashCode = 31 * hashCode + hashCode(node);
        }

        protected long hashCode(final JsonNode node) {
            if (node.isBinary()) {
                try {
                    // default Jackson computation is merely length of data; we want something much
                    // richer than that
                    final byte[] value = node.binaryValue();
                    int h = 0;
                    final int length = value.length;
                    for (int i = 0; i < length; i++) {
                        h = 31 * h + value[i];
                    }
                    return h;
                } catch (final IOException e) {
                    return node.hashCode();
                }
            }

            // use default
            return node.hashCode();
        }

        @Override
        protected void unwrapArray(final JsonNode node) {
            // hash the length of the array
            hashCode = 31 * hashCode + node.size();

            // continue unwrapping
            super.unwrapArray(node);
        }

        @Override
        protected void unwrapObject(final JsonNode node) {
            // unwrap properties in sorted field order (we don't care about ordering)
            final List<String> names = Lists.newArrayList(node.fieldNames());
            Collections.sort(names);
            for (final String name : names) {
                hashCode = 31 * hashCode + name.hashCode();
                final JsonNode child = node.get(name);
                accept(child);
            }
        }
    }

    public static abstract class JsonNodeCollector implements Consumer<JsonNode> {
        @Override
        public void accept(final JsonNode node) {
            if (node == null || node.isMissingNode()) {
                return;
            }

            // unwrap arrays
            if (node.isArray()) {
                unwrapArray(node);
                return;
            }

            // unwrap objects
            if (node.isObject()) {
                unwrapObject(node);
                return;
            }

            collect(node);
        }

        protected abstract void collect(JsonNode node);

        protected void unwrapArray(final JsonNode node) {
            for (int i = 0, size = node.size(); i < size; i++) {
                accept(node.get(i));
            }
        }

        protected void unwrapObject(final JsonNode node) {
            final Iterator<JsonNode> children = node.elements();
            while (children.hasNext()) {
                final JsonNode child = children.next();
                accept(child);
            }
        }
    }

    public static class ValueCollector extends JsonNodeCollector {
        private JsonNode value;

        public JsonNode getValue() {
            return value == null ? MissingNode.getInstance() : value;
        }

        @Override
        protected void collect(final JsonNode node) {
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
    }

    public static long hashCode(final JsonNode node, final List<String> path) {
        final HashCodeCollector collector = new HashCodeCollector();
        traverse(node, collector, path, 0);
        return collector.getValue();
    }

    public static String md5(final JsonNode node, final List<String> path) {
        final DigestCollector collector = new DigestCollector("MD5");
        traverse(node, collector, path, 0);
        return collector.getValue();
    }

    public static void read(final JsonNode node, final Consumer<JsonNode> consumer, final List<String> path) {
        Preconditions.checkState(consumer != null, "consumer must be non-null");
        Preconditions.checkState(path != null, "path must be non-null");
        traverse(node, consumer, path, 0);
    }

    public static JsonNode read(final JsonNode node, final List<String> path) {
        final ValueCollector collector = new ValueCollector();
        traverse(node, collector, path, 0);
        return collector.getValue();
    }

    private static void traverse(
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
                traverse(node.get(i), consumer, path, depth);
            }
            return;
        }

        if (depth == path.size()) {
            consumer.accept(node);
            return;
        }

        if (node.isObject()) {
            final String name = path.get(depth);
            traverse(node.path(name), consumer, path, depth + 1);
        }
    }

    private JsonNodeUtils() {
        // utility class
    }
}
