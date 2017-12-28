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

import org.junit.Assert;
import org.junit.Test;

import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;

public class DefaultValueSerializerTest {
    private final ValueSerializer serializer = new DefaultValueSerializer();

    private void assertEquals(final Field field, final String expected, final Object value)
            throws IOException {
        Assert.assertEquals(expected, serializer.serialize(field, value));

        // make sure Jackson can read value
        final JsonNode node = JacksonUtils.getObjectMapper().readTree(expected);

        // make sure Jackson serializes the same way
        Assert.assertEquals(expected, node.asText());
    }

    @Test
    public void testDouble() throws IOException {
        final ImmutableField field = ImmutableField.builder().name("field").type(Type.DOUBLE).build();
        assertEquals(field, "128.0", Double.valueOf(128.0d));
        assertEquals(field, "1.7976931348623157E308", Double.valueOf(Double.MAX_VALUE));
    }

    @Test
    public void testFloat() throws IOException {
        final ImmutableField field = ImmutableField.builder().name("field").type(Type.FLOAT).build();
        assertEquals(field, "128.0", Float.valueOf(128.0f));
        assertEquals(field, "3.4028235E38", Float.valueOf(Float.MAX_VALUE));
    }
}
