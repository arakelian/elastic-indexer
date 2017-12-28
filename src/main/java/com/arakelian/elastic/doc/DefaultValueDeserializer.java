/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arakelian.elastic.doc;

import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.BooleanUtils;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;

public class DefaultValueDeserializer implements ValueDeserializer {
    protected abstract static class AbstractReader {
        protected abstract void doRead(
                final Field field,
                final JsonNode node,
                final Consumer<Object> consumer) throws ValueException;

        protected void malformed(final Field field, final JsonNode node, final Throwable cause) {
            if (field.isIgnoreMalformed() == null || !field.isIgnoreMalformed()) {
                throw new ValueException(field, node, cause);
            }
        }

        protected void read(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            if (node == null || node.isNull() || node.isMissingNode()) {
                return;
            }

            if (node.isArray()) {
                for (int i = 0, size = node.size(); i < size; i++) {
                    final JsonNode item = node.get(i);
                    read(field, item, consumer);
                }
                return;
            }

            if (node.isObject()) {
                final Iterator<JsonNode> children = node.elements();
                while (children.hasNext()) {
                    final JsonNode child = children.next();
                    read(field, child, consumer);
                }
                return;
            }

            if (node.isPojo()) {
                malformed(field, node, new IllegalStateException("Complex type cannot be coerced"));
                return;
            }

            doRead(field, node, consumer);
        }
    }

    private final class BinaryDeserializer extends AbstractReader {
        @Override
        protected void doRead(final Field field, final JsonNode node, final Consumer<Object> consumer) {
            if (node.isBinary()) {
                final byte[] value = ((BinaryNode) node).binaryValue();
                consumer.accept(value);
                return;
            }

            try {
                final byte[] value = BaseEncoding.base64().decode(asText(field, node));
                consumer.accept(value);
            } catch (final IllegalArgumentException e) {
                malformed(field, node, e);
            }
        }
    }

    private final class BooleanDeserializer extends AbstractReader {
        @Override
        protected void doRead(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            if (node.isBoolean()) {
                final boolean value = ((BooleanNode) node).booleanValue();
                consumer.accept(value);
                return;
            }

            final String text = asText(field, node);
            final Boolean value = BooleanUtils.toBooleanObject(text);
            if (value == null) {
                malformed(field, node, new IllegalArgumentException("Invalid boolean: \"" + text + "\""));
                return;
            }
            consumer.accept(value);
        }
    }

    private final class ByteDeserializer extends AbstractReader {
        @Override
        protected void doRead(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            // we do not truncate byte values!
            if (node.isIntegralNumber() && node.canConvertToInt()) {
                final int value = node.intValue();
                if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                    consumer.accept(Byte.valueOf((byte) value));
                    return;
                }
            }

            try {
                final byte value = Byte.parseByte(asText(field, node));
                consumer.accept(value);
            } catch (final NumberFormatException nfe) {
                malformed(field, node, nfe);
            }
        }
    }

    private final class DateDeserializer extends AbstractReader {
        @Override
        protected void doRead(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            // we support a wide variety of text formats, and always encoding in UTC
            final ZonedDateTime value = DateUtils.toZonedDateTimeUtc(asText(field, node));
            if (value == null) {
                malformed(field, node, null);
            } else {
                consumer.accept(value);
            }
        }
    }

    private final class DoubleDeserializer extends AbstractReader {
        @Override
        protected void doRead(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            try {
                final double value = Double.parseDouble(asText(field, node));
                if (Double.isFinite(value)) {
                    consumer.accept(value);
                } else {
                    malformed(field, node, new NumberFormatException("Number is not finite"));
                }
            } catch (final NumberFormatException nfe) {
                malformed(field, node, nfe);
            }
        }
    }

    private final class FloatDeserializer extends AbstractReader {
        @Override
        protected void doRead(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            try {
                final float value = Float.parseFloat(asText(field, node));
                if (Float.isFinite(value)) {
                    consumer.accept(value);
                } else {
                    malformed(field, node, new NumberFormatException("Number is not finite"));
                }
            } catch (final NumberFormatException nfe) {
                malformed(field, node, nfe);
            }
        }
    }

    private final class IntegerDeserializer extends AbstractReader {
        @Override
        protected void doRead(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            // we do not truncate decimal values!
            if (node.isIntegralNumber() && node.canConvertToInt()) {
                final int value = node.intValue();
                consumer.accept(value);
                return;
            }

            try {
                final int value = Integer.parseInt(asText(field, node));
                consumer.accept(value);
            } catch (final NumberFormatException nfe) {
                malformed(field, node, nfe);
            }
        }
    }

    private final class LongDeserializer extends AbstractReader {
        @Override
        protected void doRead(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            if (node.isIntegralNumber() && node.canConvertToLong()) {
                final long value = node.longValue();
                consumer.accept(value);
                return;
            }

            try {
                final long value = Long.parseLong(asText(field, node));
                consumer.accept(value);
            } catch (final NumberFormatException nfe) {
                malformed(field, node, nfe);
            }
        }
    }

    private final class ShortDeserializer extends AbstractReader {
        @Override
        protected void doRead(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            if (node.isIntegralNumber() && node.canConvertToInt()) {
                final int value = node.intValue();
                if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                    consumer.accept(Short.valueOf((short) value));
                    return;
                }
            }

            try {
                final short value = Short.parseShort(asText(field, node));
                consumer.accept(value);
            } catch (final NumberFormatException nfe) {
                malformed(field, node, nfe);
            }
        }
    }

    private final class TextDeserializer extends AbstractReader {
        @Override
        protected void doRead(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            // pass a default because NullNode.asText() returns "null" string
            final String value = node.asText(null);
            consumer.accept(value);
        }
    }

    protected String asText(@SuppressWarnings("unused") final Field field, final JsonNode node) {
        return node.asText(null);
    }

    @Override
    public void deserialize(final Field field, final JsonNode node, final Consumer<Object> consumer)
            throws ValueException {
        Preconditions.checkArgument(consumer != null, "consumer must be non-null");
        Preconditions.checkArgument(field != null, "field must be non-null");
        if (node == null || node.isNull() || node.isMissingNode()) {
            return;
        }

        final Type type = field.getType();
        if (type == null) {
            // if we don't have type information for the elastic field, consume simple text
            readAny(field, node, consumer);
            return;
        }

        switch (type) {
        case BINARY:
            readBinary(field, node, consumer);
            break;
        case BOOLEAN:
            readBoolean(field, node, consumer);
            break;
        case DATE:
            readDate(field, node, consumer);
            break;
        case BYTE:
            readByte(field, node, consumer);
            break;
        case SHORT:
            readShort(field, node, consumer);
            break;
        case INTEGER:
            readInteger(field, node, consumer);
            break;
        case LONG:
            readLong(field, node, consumer);
            break;
        case DOUBLE:
            readDouble(field, node, consumer);
            break;
        case FLOAT:
            readFloat(field, node, consumer);
            break;
        case TEXT:
        case KEYWORD:
            readText(field, node, consumer);
            break;
        default:
            throw new ValueException("Unrecognized field type: " + field, field, node);
        }
    }

    protected void readAny(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return;
        }

        if (node.isArray()) {
            for (int i = 0, size = node.size(); i < size; i++) {
                final JsonNode item = node.get(i);
                readAny(field, item, consumer);
            }
            return;
        }

        if (node.isTextual()) {
            consumer.accept(node.asText(null));
        }

        if (node.isObject() || node.isPojo()) {
            final Map value = JacksonUtils.getObjectMapper().convertValue(node, Map.class);
            consumer.accept(value);
            return;
        }

        final Object value = JacksonUtils.getObjectMapper().convertValue(node, Object.class);
        consumer.accept(value);
    }

    protected void readBinary(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        new BinaryDeserializer().read(field, node, consumer);
    }

    protected void readBoolean(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        new BooleanDeserializer().read(field, node, consumer);
    }

    protected void readByte(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        new ByteDeserializer().read(field, node, consumer);
    }

    protected void readDate(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        new DateDeserializer().read(field, node, consumer);
    }

    protected void readDouble(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        new DoubleDeserializer().read(field, node, consumer);
    }

    protected void readFloat(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        new FloatDeserializer().read(field, node, consumer);
    }

    protected void readInteger(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        new IntegerDeserializer().read(field, node, consumer);
    }

    protected void readLong(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        new LongDeserializer().read(field, node, consumer);
    }

    protected void readShort(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        new ShortDeserializer().read(field, node, consumer);
    }

    protected void readText(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        new TextDeserializer().read(field, node, consumer);
    }
}
