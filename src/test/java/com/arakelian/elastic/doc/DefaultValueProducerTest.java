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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.jackson.model.GeoPointTest;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.BaseEncoding;

public class DefaultValueProducerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultValueProducerTest.class);

    private final ValueProducer producer = new DefaultValueProducer(JacksonUtils.getObjectMapper());

    private void assertEquals(final List<?> expected, final List<?> actual) {
        final int size = expected.size();
        Assert.assertEquals(size, actual.size());
        for (int i = 0; i < size; i++) {
            final Object lhs = expected.get(i);
            final Object rhs = actual.get(i);
            if (lhs instanceof byte[] && rhs instanceof byte[]) {
                Assert.assertArrayEquals((byte[]) lhs, (byte[]) rhs);
            } else {
                Assert.assertEquals(lhs, rhs);
            }
        }
    }

    private void assertFailure(final Field field, final List<String> inputs)
            throws IOException, JsonProcessingException {

        // inputs should fail individually
        for (int i = 0, size = inputs.size(); i < size; i++) {
            assertFailure(field, inputs.get(i));
        }

        // inputs should fail in an array
        final String jsonArray = "[" + Joiner.on(",").join(inputs) + "]";
        assertFailure(field, jsonArray);
    }

    private void assertFailure(final Field field, final String input) throws IOException {
        final ValueCollector<Object> result = new ValueCollector<>();
        final ObjectMapper mapper = JacksonUtils.getObjectMapper();
        final JsonNode node = mapper.readTree(input);
        try {
            producer.traverse(field, node, result);
            Assert.fail("Should not have been able to deserialize \"" + input + "\" to " + result);
        } catch (final ValueException ve) {
            // expected
        }
    }

    private List<Object> deserialize(final Field field, final String input)
            throws IOException, JsonProcessingException {
        final ValueCollector<Object> result = new ValueCollector<>();
        final ObjectMapper mapper = JacksonUtils.getObjectMapper();
        final JsonNode node = mapper.readTree(input);
        producer.traverse(field, node, result);
        final List<Object> actual = result.get();
        return actual;
    }

    @Test
    public void testBinary() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.BINARY).build();

        final ImmutableList<byte[]> outputs = ImmutableList.of("hello".getBytes(Charsets.UTF_8));

        final List<String> inputs = outputs.stream() //
                .map(bytes -> {
                    final String val = BaseEncoding.base64().encode(bytes);
                    return "\"" + val + "\"";
                }) //
                .collect(Collectors.toList());

        verifyDeserializer(field, inputs, outputs);

        assertFailure(
                field,
                ImmutableList.of(
                        // invalid base 64 encodings
                        "\"may 2004\"",
                        "\"abc===defg123456789ABCDEF==GHIJKL==============\"", //
                        "\"XYZABCDEF\""));
    }

    @Test
    public void testBoolean() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.BOOLEAN).build();

        verifyDeserializer(
                field,
                ImmutableList.of(
                        "true", //
                        "false", //
                        "\"true\"", //
                        "\"TRUE\"", //
                        "\"yes\"", //
                        "\"YES\"", //
                        "\"false\"", //
                        "\"FALSE\"", //
                        "\"no\"", //
                        "\"NO\"", //
                        "\"on\"", //
                        "\"off\""),
                ImmutableList.of( //
                        Boolean.TRUE, //
                        Boolean.FALSE, //
                        Boolean.TRUE, //
                        Boolean.TRUE, //
                        Boolean.TRUE, //
                        Boolean.TRUE, //
                        Boolean.FALSE, //
                        Boolean.FALSE, //
                        Boolean.FALSE, //
                        Boolean.FALSE, //
                        Boolean.TRUE, //
                        Boolean.FALSE));

        assertFailure(
                field,
                ImmutableList.of(
                        // numbers are not valid
                        "0",
                        "1", //
                        "\"offff\""));
    }

    @Test
    public void testByte() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.BYTE).build();

        verifyDeserializer(
                field,
                ImmutableList.of(
                        Integer.toString(Byte.MIN_VALUE), //
                        Integer.toString(Byte.MAX_VALUE)),
                ImmutableList.of( //
                        Byte.MIN_VALUE, //
                        Byte.MAX_VALUE));

        assertFailure(
                field,
                ImmutableList.of(
                        // should not truncate decimals
                        "2.0",
                        "3.14",
                        // these are outside range of acceptable values
                        Integer.toString(Byte.MIN_VALUE - 1),
                        Integer.toString(Byte.MAX_VALUE + 1)));
    }

    @Test
    public void testDate() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.DATE).build();

        final ImmutableList<String> inputs = ImmutableList.of(
                // see DateUtilsTest for examples
                "1515628964664", //
                Long.toString(new Date().getTime()), //
                Long.toString(System.currentTimeMillis()), //
                Long.toString(DateUtils.nowWithZoneUtc().toEpochSecond()), //
                "\"2/29/2000\"", //
                "\"2003/02/28\"", //
                "\"February 28, 2003\"", //
                "\"2016-12-21T16:46:39.830000000Z\"", //
                "\"sep 4 2016\"", //
                "\"09-04-2016\"", //
                "\"2016-12-21T16:46:39.830Z\"", //
                "\"2016-12-21T16:46:39.830000000Z\"");

        final List<Object> outputs = inputs.stream() //
                .map(text -> {
                    final Object value;
                    final ZonedDateTime date;
                    if (text.startsWith("\"")) {
                        value = text.substring(1, text.length() - 1);
                        date = DateUtils.toZonedDateTimeUtc(value.toString());
                    } else {
                        value = Long.parseLong(text);
                        date = DateUtils.toZonedDateTimeUtc((Long) value);
                    }
                    Assert.assertNotNull(value + " is not a valid date", date);
                    LOGGER.info("{} converted to {}", value, date);
                    return date;
                }) //
                .collect(Collectors.toList());

        verifyDeserializer(field, inputs, outputs);

        assertFailure(
                field,
                ImmutableList.of(
                        // invalid dates
                        "\"may 2004\"",
                        "\"13/13/2013\"", //
                        "\"2016-88-2\""));
    }

    @Test
    public void testDouble() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.DOUBLE).build();

        verifyDeserializer(
                field,
                ImmutableList.of(
                        BigDecimal.valueOf(Double.MIN_VALUE).toPlainString(),
                        BigDecimal.valueOf(Double.MAX_VALUE).toPlainString(), //
                        "2.0", //
                        "3.14"),
                ImmutableList.of(
                        Double.MIN_VALUE,
                        Double.MAX_VALUE, //
                        2.0d, //
                        3.14d));

        assertFailure(
                field,
                ImmutableList.of(
                        // these are outside range of acceptable values
                        BigDecimal.valueOf(-Double.MAX_VALUE).multiply(BigDecimal.TEN).toPlainString(),
                        BigDecimal.valueOf(Double.MAX_VALUE).multiply(BigDecimal.TEN).toPlainString()));
    }

    @Test
    public void testFloat() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.FLOAT).build();

        verifyDeserializer(
                field,
                ImmutableList.of(
                        BigDecimal.valueOf(Float.MIN_VALUE).toPlainString(),
                        BigDecimal.valueOf(Float.MAX_VALUE).toPlainString(),
                        "2.0",
                        "3.14"),
                ImmutableList.of(
                        Float.MIN_VALUE, //
                        Float.MAX_VALUE,
                        2.0f,
                        3.14f));

        assertFailure(
                field,
                ImmutableList.of(
                        // these are outside range of acceptable values
                        BigDecimal.valueOf(-Double.MAX_VALUE).toPlainString(),
                        BigDecimal.valueOf(Double.MAX_VALUE).toPlainString()));
    }

    @Test
    public void testGeopoint() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.GEO_POINT).build();

        verifyDeserializer(
                field,
                ImmutableList.of(
                        "[ -71.34, 41.12 ]", //
                        "\"41.12,-71.34\"", //
                        "{ \n" + //
                                "    \"lat\": 41.12,\n" + //
                                "    \"lon\": -71.34\n" + //
                                "  }",
                        "\"drm3btev3e86\""),
                ImmutableList.of(
                        GeoPointTest.POINT, //
                        GeoPointTest.POINT,
                        GeoPointTest.POINT,
                        GeoPointTest.POINT));
    }

    @Test
    public void testInteger() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.INTEGER).build();

        verifyDeserializer(
                field,
                ImmutableList.of(
                        Long.toString(Integer.MIN_VALUE), //
                        Long.toString(Integer.MAX_VALUE)),
                ImmutableList.of(
                        Integer.MIN_VALUE, //
                        Integer.MAX_VALUE));

        assertFailure(
                field,
                ImmutableList.of(
                        // should not truncate decimals
                        "2.0",
                        "3.14",
                        // these are outside range of acceptable values
                        Long.toString((long) Integer.MIN_VALUE - 1),
                        Long.toString((long) Integer.MAX_VALUE + 1)));
    }

    @Test
    public void testLong() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.LONG).build();

        verifyDeserializer(
                field,
                ImmutableList.of(
                        BigDecimal.valueOf(Long.MIN_VALUE).toPlainString(),
                        BigDecimal.valueOf(Long.MAX_VALUE).toPlainString()),
                ImmutableList.of(
                        Long.MIN_VALUE, //
                        Long.MAX_VALUE));

        assertFailure(
                field,
                ImmutableList.of(
                        // should not truncate decimals
                        "2.0",
                        "3.14",
                        // these are outside range of acceptable values
                        BigDecimal.valueOf(Long.MIN_VALUE - 1.0d).toPlainString(),
                        BigDecimal.valueOf(Long.MAX_VALUE + 1.0d).toPlainString()));
    }

    @Test
    public void testShort() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.SHORT).build();

        verifyDeserializer(
                field,
                ImmutableList.of(
                        Integer.toString(Short.MIN_VALUE), //
                        Integer.toString(Short.MAX_VALUE)),
                ImmutableList.of(
                        Short.MIN_VALUE, //
                        Short.MAX_VALUE));

        assertFailure(
                field,
                ImmutableList.of(
                        // should not truncate decimals
                        "2.0",
                        "3.14",
                        // these are outside range of acceptable values
                        Integer.toString(Short.MIN_VALUE - 1),
                        Integer.toString(Short.MAX_VALUE + 1)));
    }

    @Test
    public void testText() throws IOException {
        final Field field = ImmutableField.builder().name("field").type(Type.TEXT).build();

        verifyDeserializer(
                field,
                ImmutableList.of(
                        "2.5", //
                        "\"3.14\"",
                        "true",
                        "\"false\""),
                ImmutableList.of(
                        "2.5", //
                        "3.14",
                        "true",
                        "false"));

        // arrays should be handled
        assertEquals(deserialize(field, "[1,2,3,4,5]"), ImmutableList.of("1", "2", "3", "4", "5"));

        // objects should be handled
        assertEquals(deserialize(field, "{\"field\":\"value\"}"), ImmutableList.of("value"));
    }

    private void verifyDeserializer(final Field field, final List<String> inputs, final List<?> outputs)
            throws IOException, JsonProcessingException {

        // test inputs individually
        for (int i = 0, size = inputs.size(); i < size; i++) {
            final String input = inputs.get(i);
            verifyDeserializer(field, input, outputs.get(i));
        }

        // test inputs in an array
        final String jsonArray = "[" + Joiner.on(",").join(inputs) + "]";
        assertEquals(deserialize(field, jsonArray), outputs);
    }

    private void verifyDeserializer(final Field field, final String input, final Object... expected)
            throws IOException {
        final List<Object> actual = deserialize(field, input);
        assertEquals(ImmutableList.copyOf(expected), actual);
    }
}
