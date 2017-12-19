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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableIndex;
import com.arakelian.elastic.model.ImmutableMapping;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.Mapping;

public class DocumentBuilderTest {
    /** Plain builder does not do entity extraction **/
    private DocumentBuilder docBuilder;

    /** Sample input **/
    private String sampleJson;

    private Index newIndex(final Mapping mapping) {
        final Index index = ImmutableIndex.builder() //
                .name("testIndex") //
                .putMappings(Mapping._DEFAULT_, mapping) //
                .build();
        return index;
    }

    @Before
    public void setUp() {
        // create document builder
        final DocumentBuilderConfig config = ImmutableDocumentBuilderConfig.builder() //
                .build();
        docBuilder = new DocumentBuilder(config);

        // source: https://cdn.rawgit.com/Marak/faker.js/master/examples/browser/index.html
        sampleJson = "{\n" + //
                "  \"name\": \"Schuyler\",\n" + //
                "  \"username\": \"Schuyler_Bogan\",\n" + //
                "  \"avatar\": \"https://s3.amazonaws.com/uifaces/faces/twitter/omnizya/128.jpg\",\n" + //
                "  \"email\": \"Schuyler_Bogan_Olson@gmail.com\",\n" + //
                "  \"dob\": \"1962-09-01T03:19:25.270Z\",\n" + //
                "  \"friends\": [\"Moe\",\"Larry\",\"Curly\"],\n" + //
                "  \"phone\": \"1-126-999-3806\",\n" + //
                "  \"address\": {\n" + //
                "    \"street\": \"Schumm Grove\",\n" + //
                "    \"suite\": \"Apt. 364\",\n" + //
                "    \"city\": \"East Devin\",\n" + //
                "    \"zipcode\": \"77081-9341\",\n" + //
                "    \"geo\": {\n" + //
                "      \"lat\": \"-3.1352\",\n" + //
                "      \"lng\": \"-25.6319\"\n" + //
                "    }\n" + //
                "  },\n" + //
                "  \"numbers\": {\n" + //
                "    \"bytes\": [\"-128\",-128, 0, 127],\n" + //
                "    \"shorts\": [\"   -32768   \",-32768, 0, 32767],\n" + //
                "    \"int\": [\"\\n\\t\\t-2147483648    \",-2147483648, 0, 2147483647, \"2147483647\"],\n" + //
                "    \"long\": [\" -9223372036854775808 \",-9223372036854775808, 0, 9223372036854775807],\n" + //
                "    \"double\": [\"  3.14  \",2.718,\"6.67e-11\"]\n" + //
                "  },\n" + //
                "  \"base64\": {\n" + //
                "    \"valid\": [\"aGVsbG8gdGhlcmU=\",\"YXJha2VsaWFu\"],\n" + //
                "    \"invalid\": [\"ZZZZZ\",\"\"]\n" + //
                "  },\n" + //
                "  \"dates\": {\n" + //
                "    \"dateOnly\": [\"20160201\",\"09/04/2016\",\"september 04, 2016\"],\n" + //
                "    \"iso\": [\"2016-12-21T16:46:39.830Z\"],\n" + //
                "    \"invalid\": [\"20030229\"]\n" + //
                "  },\n" + //
                "  \"booleans\": {\n" + //
                "    \"true\": {\n" + //
                "      \"true\": [\"yes\"],\n" + //
                "      \"yes\": [\"true\"],\n" + //
                "      \"on\": [\"on\"],\n" + //
                "      \"t\": [\"t\"]\n" + //
                "    },\n" + //
                "    \"false\": {\n" + //
                "      \"no\": [\"no\"],\n" + //
                "      \"false\": [\"false\"],\n" + //
                "      \"off\": [\"off\"],\n" + //
                "      \"f\": [\"f\"]\n" + //
                "    },\n" + //
                "    \"invalid\": [\"blah\"]\n" + //
                "  },\n" + //
                "  \"website\": \"minerva.name\",\n" + //
                "  \"company\": {\n" + //
                "    \"name\": \"Hilll - Kassulke\",\n" + //
                "    \"catchPhrase\": \"Cross-group attitude-oriented knowledge base\",\n" + //
                "    \"bs\": \"dot-com transform mindshare\"\n" + //
                "  }\n" + //
                "}";
    }

    @Test
    public void testBooleansFalse() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(
                                ImmutableField.builder() //
                                        .name("booleans") //
                                        .ignoreMalformed(false) //
                                        .type(Type.BOOLEAN) //
                                        .build())
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("booleans", "booleans/false") //
                .build();
        Assert.assertEquals( //
                "{\"booleans\":\"false\"}", //
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testBooleansTrue() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(
                                ImmutableField.builder() //
                                        .name("booleans") //
                                        .ignoreMalformed(false) //
                                        .type(Type.BOOLEAN) //
                                        .build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("booleans", "booleans/true") //
                .build();
        Assert.assertEquals( //
                "{\"booleans\":\"true\"}", //
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testIgnoreInvalidBinary() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(
                                ImmutableField.builder() //
                                        .name("base64") //
                                        .ignoreMalformed(true) //
                                        .type(Type.BINARY) //
                                        .build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("base64", "base64") //
                .build();
        Assert.assertEquals( //
                "{\"base64\":[\"aGVsbG8gdGhlcmU=\",\"YXJha2VsaWFu\"]}", //
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testIgnoreInvalidBytes() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(
                                ImmutableField.builder() //
                                        .name("numbers") //
                                        .ignoreMalformed(true) //
                                        .type(Type.BYTE) //
                                        .build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("numbers", "numbers") //
                .build();
        Assert.assertEquals( //
                "{\"numbers\":[\"-128\",\"0\",\"127\"]}", //
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testIgnoreInvalidDates() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(
                                ImmutableField.builder() //
                                        .name("dates") //
                                        .ignoreMalformed(true) //
                                        .type(Type.DATE) //
                                        .build()) //
                        .build());
        final String date1 = DateUtils.toStringIsoFormat("2016-02-01");
        final String date2 = DateUtils.toStringIsoFormat("2016-09-04");

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("dates", "dates") //
                .build();
        Assert.assertEquals( //
                "{\"dates\":[\"" + date1 + "\",\"" + date2 + "\",\"2016-12-21T16:46:39.830000000Z\"]}", //
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testIgnoreInvalidDoubles() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(
                                ImmutableField.builder() //
                                        .name("numbers") //
                                        .ignoreMalformed(true) //
                                        .type(Type.DOUBLE) //
                                        .build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .addIdentityFields("numbers") //
                .build();
        Assert.assertEquals( //
                "{\"numbers\":[\"-128\",\"0\",\"127\",\"-32768\",\"32767\",\"-2147483648\",\"2147483647\",\"-9223372036854775808\",\"9223372036854775807\",\"3.14\",\"2.718\",\"6.67e-11\"]}",
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testIgnoreInvalidFloats() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(
                                ImmutableField.builder() //
                                        .name("numbers") //
                                        .ignoreMalformed(true) //
                                        .type(Type.FLOAT) //
                                        .build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("numbers", "numbers") //
                .build();
        Assert.assertEquals( //
                "{\"numbers\":[\"-128\",\"0\",\"127\",\"-32768\",\"32767\",\"-2147483648\",\"2147483647\",\"-9223372036854775808\",\"9223372036854775807\",\"3.14\",\"2.718\",\"6.67e-11\"]}",
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testIgnoreInvalidIntegers() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(
                                ImmutableField.builder() //
                                        .name("numbers") //
                                        .ignoreMalformed(true) //
                                        .type(Type.INTEGER) //
                                        .build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("numbers", "numbers") //
                .build();
        Assert.assertEquals( //
                "{\"numbers\":[\"-128\",\"0\",\"127\",\"-32768\",\"32767\",\"-2147483648\",\"2147483647\"]}",
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testIgnoreInvalidLongs() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(
                                ImmutableField.builder() //
                                        .name("numbers") //
                                        .ignoreMalformed(true) //
                                        .type(Type.LONG) //
                                        .build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("numbers", "numbers") //
                .build();
        Assert.assertEquals( //
                "{\"numbers\":[\"-128\",\"0\",\"127\",\"-32768\",\"32767\",\"-2147483648\",\"2147483647\",\"-9223372036854775808\",\"9223372036854775807\"]}",
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testIgnoreInvalidShorts() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(
                                ImmutableField.builder() //
                                        .name("numbers") //
                                        .ignoreMalformed(true) //
                                        .type(Type.SHORT) //
                                        .build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("numbers", "numbers") //
                .build();
        Assert.assertEquals( //
                "{\"numbers\":[\"-128\",\"0\",\"127\",\"-32768\",\"32767\"]}",
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testSourcePathIsArray() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(ImmutableField.builder().name("friends").build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("friends", "friends") //
                .build();
        Assert.assertEquals( //
                "{\"friends\":[\"Moe\",\"Larry\",\"Curly\"]}",
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testSourcePathIsNested() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(ImmutableField.builder().name("name").build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("name", "name", "company/name") //
                .build();
        Assert.assertEquals( //
                "{\"name\":[\"Schuyler\",\"Hilll - Kassulke\"]}",
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testSourcePathIsObject() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(ImmutableField.builder().name("address").build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("address", "address") //
                .build();
        Assert.assertEquals(
                "{\"address\":[\"Schumm Grove\",\"Apt. 364\",\"East Devin\",\"77081-9341\",\"-3.1352\",\"-25.6319\"]}",
                docBuilder.build(documentMapping, sampleJson));
    }

    @Test
    public void testSourcePathIsString() {
        final Index index = newIndex(
                ImmutableMapping.builder() //
                        .all(null) //
                        .addFields(ImmutableField.builder().name("name").build()) //
                        .addFields(ImmutableField.builder().name("email").build()) //
                        .addFields(ImmutableField.builder().name("phone").build()) //
                        .build());

        final DocumentMapping documentMapping = ImmutableDocumentMapping.builder() //
                .index(index) //
                .putFieldToSourcePaths("name", "name") //
                .putFieldToSourcePaths("email", "email") //
                .putFieldToSourcePaths("phone", "phone") //
                .build();
        Assert.assertEquals(
                "{\"name\":\"Schuyler\",\"email\":\"Schuyler_Bogan_Olson@gmail.com\",\"phone\":\"1-126-999-3806\"}",
                docBuilder.build(documentMapping, sampleJson));
    }
}
