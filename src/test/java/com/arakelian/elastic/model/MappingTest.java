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

package com.arakelian.elastic.model;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.google.common.collect.ImmutableSet;

public class MappingTest extends AbstractElasticModelTest {
    public static final Mapping CONTACT = ImmutableMapping.builder() //
            .addField(
                    ImmutableField.builder() //
                            .name("name") //
                            .putField(
                                    "raw",
                                    ImmutableField.builder() //
                                            .name("raw") //
                                            .type(Type.KEYWORD) //
                                            .build()) //
                            .build()) //
            .addField(ImmutableField.builder().name("street").build()) //
            .addField(ImmutableField.builder().name("city").build()) //
            .addField(ImmutableField.builder().name("state").build()) //
            .addField(ImmutableField.builder().name("zip").build()) //
            .build();

    public MappingTest(final String number) {
        super(number);
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(objectMapper, CONTACT, Mapping.class);
    }

    @Test
    public void testNormalization() {
        // getFields() and getProperties() should refer to same list of fields
        Assert.assertEquals(
                ImmutableSet.copyOf(CONTACT.getFields()),
                ImmutableSet.copyOf(CONTACT.getProperties().values()));

        // we should be able to add a field to a mapping
        final Mapping newMapping = ImmutableMapping.builder() //
                .from(MappingTest.CONTACT) //
                .addField(ImmutableField.builder().name("test").build()) //
                .build();

        // the new field should appear in getProperties()
        Assert.assertEquals(
                ImmutableSet.copyOf(newMapping.getFields()),
                ImmutableSet.copyOf(newMapping.getProperties().values()));
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(CONTACT, Mapping.class);
    }
}
