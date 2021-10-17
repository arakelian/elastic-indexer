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
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.function.Consumer;

import org.apache.commons.lang3.BooleanUtils;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.elastic.model.DateRange;
import com.arakelian.elastic.model.DoubleRange;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.FloatRange;
import com.arakelian.elastic.model.IntegerRange;
import com.arakelian.elastic.model.LongRange;
import com.arakelian.jackson.model.GeoPoint;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;

public class DefaultValueProducer implements ValueProducer {
    protected abstract static class AbstractProducer {
        protected void collect(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            if (node == null || node.isNull() || node.isMissingNode()) {
                return;
            }

            if (node.isArray()) {
                handleArray(field, (ArrayNode) node, consumer);
                return;
            }

            if (node.isObject()) {
                handleObject(field, (ObjectNode) node, consumer);
                return;
            }

            if (node.isPojo()) {
                malformed(field, node, new IllegalStateException("Complex type cannot be coerced"));
                return;
            }

            handleValue(field, node, consumer);
        }

        protected void handleArray(final Field field, final ArrayNode node, final Consumer<Object> consumer)
                throws ValueException {
            for (int i = 0, size = node.size(); i < size; i++) {
                final JsonNode item = node.get(i);
                collect(field, item, consumer);
            }
        }

        protected void handleObject(final Field field, final ObjectNode node, final Consumer<Object> consumer)
                throws ValueException {
            final Iterator<JsonNode> children = node.elements();
            while (children.hasNext()) {
                final JsonNode child = children.next();
                collect(field, child, consumer);
            }
        }

        protected abstract void handleValue(
                final Field field,
                final JsonNode node,
                final Consumer<Object> consumer) throws ValueException;

        protected void malformed(final Field field, final JsonNode node, final Throwable cause) {
            if (field.isIgnoreMalformed() == null || !field.isIgnoreMalformed()) {
                throw new ValueException(field, node, cause);
            }
        }
    }

    private final class BinaryProducer extends AbstractProducer {
        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer) {
            if (node.isBinary()) {
                final byte[] value = ((BinaryNode) node).binaryValue();
                consumer.accept(value);
                return;
            }

            try {
                final byte[] value = BaseEncoding.base64().decode(asText(field, node));
                if (value != null && value.length != 0) {
                    consumer.accept(value);
                }
            } catch (final IllegalArgumentException e) {
                malformed(field, node, e);
            }
        }
    }

    private final class BooleanProducer extends AbstractProducer {
        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer)
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

    private final class ByteProducer extends AbstractProducer {
        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer)
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

    private final class DateProducer extends AbstractProducer {
        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            if (node.isIntegralNumber() && node.canConvertToLong()) {
                // our implementation is smart about detecting epoch units (seconds, milliseconds,
                // and microseconds)
                final long val = node.longValue();
                @SuppressWarnings("PreferJavaTimeOverload")
                final ZonedDateTime value = DateUtils.toZonedDateTimeUtc(val);
                if (value == null) {
                    malformed(field, node, null);
                } else {
                    consumer.accept(value);
                }
                return;
            }

            // we support a wide variety of text formats, and always encoding in UTC
            try {
                final ZonedDateTime value = DateUtils.toZonedDateTimeUtcChecked(asText(field, node));
                if (value != null) {
                    consumer.accept(value);
                }
            } catch (final DateTimeParseException e) {
                malformed(field, node, e);
            }
        }
    }

    private final class DoubleProducer extends AbstractProducer {
        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer)
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

    private final class FloatProducer extends AbstractProducer {
        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer)
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

    private final class IntegerProducer extends AbstractProducer {
        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer)
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

    private final class LongProducer extends AbstractProducer {
        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer)
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

    protected final class ObjectProducer extends AbstractProducer {
        private final Class<?> clazz;

        public ObjectProducer(final Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        protected void handleArray(final Field field, final ArrayNode node, final Consumer<Object> consumer) {
            final Object value;
            try {
                value = mapper.convertValue(node, clazz);
            } catch (final IllegalArgumentException e) {
                // treat this as an array of values
                super.handleArray(field, node, consumer);
                return;
            }

            if (value != null) {
                consumer.accept(value);
            }
        }

        @Override
        protected void handleObject(
                final Field field,
                final ObjectNode node,
                final Consumer<Object> consumer) {
            handleValue(field, node, consumer);
        }

        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            final Object value;
            try {
                value = mapper.convertValue(node, clazz);
            } catch (final IllegalArgumentException e) {
                throw new ValueException(field, node, e);
            }

            if (value != null) {
                consumer.accept(value);
            }
        }
    }

    private final class ShortProducer extends AbstractProducer {
        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer)
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

    private final static class TextProducer extends AbstractProducer {
        @Override
        protected void handleValue(final Field field, final JsonNode node, final Consumer<Object> consumer)
                throws ValueException {
            // pass a default because NullNode.asText() returns "null" string
            final String value = node.asText(null);
            consumer.accept(value);
        }
    }

    private final ObjectMapper mapper;

    private final BinaryProducer binaryProducer = new BinaryProducer();
    private final BooleanProducer booleanProducer = new BooleanProducer();
    private final ByteProducer byteProducer = new ByteProducer();
    private final DateProducer dateProducer = new DateProducer();
    private final DoubleProducer doubleProducer = new DoubleProducer();
    private final FloatProducer floatProducer = new FloatProducer();
    private final IntegerProducer integerProducer = new IntegerProducer();
    private final LongProducer longProducer = new LongProducer();
    private final ShortProducer shortProducer = new ShortProducer();
    private final TextProducer textProducer = new TextProducer();

    private final ObjectProducer objectProducer = new ObjectProducer(Object.class);
    private final ObjectProducer geopointProducer = new ObjectProducer(GeoPoint.class);
    private final ObjectProducer longRangeProducer = new ObjectProducer(LongRange.class);
    private final ObjectProducer integerRangeProducer = new ObjectProducer(IntegerRange.class);
    private final ObjectProducer floatRangeProducer = new ObjectProducer(FloatRange.class);
    private final ObjectProducer doubleRangeProducer = new ObjectProducer(DoubleRange.class);
    private final ObjectProducer dateRangeProducer = new ObjectProducer(DateRange.class);

    public DefaultValueProducer(final ObjectMapper mapper) {
        this.mapper = Preconditions.checkNotNull(mapper);
    }

    protected String asText(@SuppressWarnings("unused") final Field field, final JsonNode node) {
        return node.asText(null);
    }

    protected void collectBinarys(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        binaryProducer.collect(field, node, consumer);
    }

    protected void collectBooleans(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        booleanProducer.collect(field, node, consumer);
    }

    protected void collectBytes(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        byteProducer.collect(field, node, consumer);
    }

    protected void collectDateRanges(
            final Field field,
            final JsonNode node,
            final Consumer<Object> consumer) {
        dateRangeProducer.collect(field, node, consumer);
    }

    protected void collectDates(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        dateProducer.collect(field, node, consumer);
    }

    protected void collectDoubleRanges(
            final Field field,
            final JsonNode node,
            final Consumer<Object> consumer) {
        doubleRangeProducer.collect(field, node, consumer);
    }

    protected void collectDoubles(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        doubleProducer.collect(field, node, consumer);
    }

    protected void collectFloatRanges(
            final Field field,
            final JsonNode node,
            final Consumer<Object> consumer) {
        floatRangeProducer.collect(field, node, consumer);
    }

    protected void collectFloats(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        floatProducer.collect(field, node, consumer);
    }

    protected void collectGeopoints(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        geopointProducer.collect(field, node, consumer);
    }

    protected void collectIntegerRanges(
            final Field field,
            final JsonNode node,
            final Consumer<Object> consumer) {
        integerRangeProducer.collect(field, node, consumer);
    }

    protected void collectIntegers(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        integerProducer.collect(field, node, consumer);
    }

    protected void collectLongRanges(
            final Field field,
            final JsonNode node,
            final Consumer<Object> consumer) {
        longRangeProducer.collect(field, node, consumer);
    }

    protected void collectLongs(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        longProducer.collect(field, node, consumer);
    }

    protected void collectObjects(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        objectProducer.collect(field, node, consumer);
    }

    protected void collectShorts(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        shortProducer.collect(field, node, consumer);
    }

    protected void collectStrings(final Field field, final JsonNode node, final Consumer<Object> consumer) {
        textProducer.collect(field, node, consumer);
    }

    @Override
    public void traverse(final Field field, final JsonNode node, final Consumer<Object> consumer)
            throws ValueException {
        Preconditions.checkArgument(consumer != null, "consumer must be non-null");
        Preconditions.checkArgument(field != null, "field must be non-null");
        if (node == null || node.isNull() || node.isMissingNode()) {
            return;
        }

        final Field.Type type = field.getType();
        if (type == null) {
            // generic conversion
            collectObjects(field, node, consumer);
            return;
        }

        switch (type) {
        case BINARY:
            collectBinarys(field, node, consumer);
            break;
        case BOOLEAN:
            collectBooleans(field, node, consumer);
            break;
        case DATE:
            collectDates(field, node, consumer);
            break;
        case BYTE:
            collectBytes(field, node, consumer);
            break;
        case SHORT:
            collectShorts(field, node, consumer);
            break;
        case INTEGER:
            collectIntegers(field, node, consumer);
            break;
        case LONG:
            collectLongs(field, node, consumer);
            break;
        case DOUBLE:
            collectDoubles(field, node, consumer);
            break;
        case FLOAT:
            collectFloats(field, node, consumer);
            break;
        case TEXT:
        case KEYWORD:
            collectStrings(field, node, consumer);
            break;
        case GEO_POINT:
            collectGeopoints(field, node, consumer);
            break;
        case INTEGER_RANGE:
            collectIntegerRanges(field, node, consumer);
            break;
        case LONG_RANGE:
            collectLongRanges(field, node, consumer);
            break;
        case FLOAT_RANGE:
            collectFloatRanges(field, node, consumer);
            break;
        case DOUBLE_RANGE:
            collectDoubleRanges(field, node, consumer);
            break;
        case DATE_RANGE:
            collectDateRanges(field, node, consumer);
            break;
        default:
            throw new ValueException("Unrecognized field type: " + field, field, node);
        }
    }
}
