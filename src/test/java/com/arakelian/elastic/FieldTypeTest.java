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

package com.arakelian.elastic;

import static com.arakelian.elastic.model.Mapping.Dynamic.STRICT;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.model.DateRangeTest;
import com.arakelian.elastic.model.DoubleRangeTest;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.FloatRangeTest;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableMapping;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.IndexedDocument;
import com.arakelian.elastic.model.IntegerRangeTest;
import com.arakelian.elastic.model.LongRangeTest;
import com.arakelian.elastic.model.search.ImmutableIdsQuery;
import com.arakelian.elastic.model.search.ImmutableQueryStringQuery;
import com.arakelian.elastic.model.search.ImmutableRangeQuery;
import com.arakelian.elastic.model.search.ImmutableSearch;
import com.arakelian.elastic.model.search.ImmutableTermsQuery;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.elastic.model.search.SearchHits;
import com.arakelian.elastic.model.search.SearchResponse;
import com.arakelian.elastic.model.search.Source;
import com.arakelian.jackson.model.ImmutableGeoPoint;
import com.arakelian.jackson.utils.JacksonUtils;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class FieldTypeTest extends AbstractElasticDockerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldTypeTest.class);

    private Object getTestValue(final Field.Type type) {
        switch (type) {
        case BINARY:
            return "hello".getBytes(Charsets.UTF_8);
        case BOOLEAN:
            return Boolean.TRUE;
        case BYTE:
            return Byte.valueOf(Byte.MAX_VALUE);
        case DATE:
            return DateUtils.nowWithZoneUtc();
        case DATE_RANGE:
            return DateRangeTest.EXTREMA;
        case DOUBLE:
            return Double.valueOf(Math.PI);
        case DOUBLE_RANGE:
            return DoubleRangeTest.EXTREMA;
        case FLOAT:
        case HALF_FLOAT:
        case SCALED_FLOAT:
            return Float.valueOf((float) Math.PI);
        case FLOAT_RANGE:
            return FloatRangeTest.EXTREMA;
        case GEO_POINT:
            return ImmutableGeoPoint.builder().lat(38.8977d).lon(77.0365d).build();
        case INTEGER:
            return Integer.valueOf(Integer.MAX_VALUE);
        case INTEGER_RANGE:
            return IntegerRangeTest.EXTREMA;
        case IP:
            return ImmutableList.of("192.168.1.1", "::ffff:10.0.0.1");
        case LONG:
            return Long.valueOf(Long.MAX_VALUE);
        case LONG_RANGE:
            return LongRangeTest.EXTREMA;
        case SHORT:
            return Short.valueOf(Short.MAX_VALUE);
        case KEYWORD:
        case COMPLETION:
        case TEXT:
        case TOKEN_COUNT:
            return "Sample " + type.name().toLowerCase() + " value";
        case GEO_SHAPE:
            return "POINT(-77.03653 38.897676)";
        default:
            throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    /**
     * Creates index mapping for field type.
     *
     * @param type
     *            data type
     * @throws IOException
     *             if index cannot be created
     */
    @ParameterizedTest
    @EnumSource(Field.Type.class)
    public void testType(final Field.Type type) throws IOException {
        final ImmutableMapping mapping = ImmutableMapping.builder() //
                .dynamic(STRICT) //
                .addField(
                        ImmutableField.builder() //
                                .name("value") //
                                .type(type) //
                                .build())
                .build();

        withIndex(mapping, index -> {
            final String id = MoreStringUtils.shortUuid();

            final Map<String, Object> doc = Maps.newLinkedHashMap();
            doc.put("value", getTestValue(type));

            final IndexedDocument response = assertSuccessful( //
                    getElasticClient().indexDocument(
                            index.getName(), //
                            _DOC, //
                            id, //
                            JacksonUtils.toString(doc, false)));

            assertEquals(index.getName(), response.getIndex());
            assertEquals(_DOC, response.getType());
            assertEquals(id, response.getId());
            assertEquals("created", response.getResult());
            assertEquals(Boolean.TRUE, response.isCreated());
        });
    }

    @Test
    public void testDateHandling() throws IOException {
        final ImmutableMapping mapping = ImmutableMapping.builder() //
                .dynamic(STRICT) //
                .addField(
                        ImmutableField.builder() //
                                .name("value") //
                                .type(Field.Type.DATE) //
                                .format("strict_date_optional_time||epoch_millis") //
                                .store(true) //
                                .build())
                .build();

        withIndex(mapping, index -> {
            final String id = MoreStringUtils.shortUuid();

            final Map<String, Object> doc = Maps.newLinkedHashMap();
            ZonedDateTime zdt = DateUtils.toZonedDateTimeUtcChecked("1951-07-29");
            LOGGER.info("Value {}", zdt);
            doc.put("value", zdt);

            final IndexedDocument response = assertSuccessful( //
                    getElasticClient().indexDocument(
                            index.getName(), //
                            _DOC, //
                            id, //
                            JacksonUtils.toString(doc, false)));

            assertEquals(index.getName(), response.getIndex());
            assertEquals(_DOC, response.getType());
            assertEquals(id, response.getId());
            assertEquals("created", response.getResult());
            assertEquals(Boolean.TRUE, response.isCreated());

            // make sure data is indexed
            getElasticClient().refreshIndex(index.getName());

            // find using id
            assertFindDate(
                    index,
                    zdt,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableIdsQuery.builder() //
                                            .name("ids_query") //
                                            .addValue(id) //
                                            .build())
                            .build());

            // find using term query (exact match)
            assertFindDate(
                    index,
                    zdt,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableTermsQuery.builder() //
                                            .fieldName("value") //
                                            .addValue("1951-07-29T04:00:00.000Z") //
                                            .build())
                            .build());

            // find using range
            assertFindDate(
                    index,
                    zdt,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableRangeQuery.builder() //
                                            .fieldName("value") //
                                            .lower("1951-07-29T04:00:00.000Z") //
                                            .upper("1951-07-29T04:00:00.000Z||+1d") //
                                            .build())
                            .build());

            // find using query string
            assertFindDate(
                    index,
                    zdt,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableQueryStringQuery.builder() //
                                            .defaultField("value") //
                                            .queryString(
                                                    "value:[\"1951-07-29T04:00:00.000Z\" TO \"1951-07-29T04:00:00.000Z||+1d\"}") //
                                            .build())
                            .build());
        });
    }

    private void assertFindDate(Index index, ZonedDateTime zdt, Search search) {
        SearchResponse sr = assertSearchFinds(index, search, 1);
        SearchHits hits = sr.getHits();
        assertEquals(1, hits.getSize());

        Source source = hits.get(0).getSource();
        String raw = source.getString("value");
        LOGGER.info("Raw {}", raw);
        ZonedDateTime zdt2 = DateUtils.toZonedDateTimeUtcChecked(raw);
        LOGGER.info("Found {}", zdt2);
        assertEquals(DateUtils.toStringIsoFormat(zdt), DateUtils.toStringIsoFormat(zdt2));
    }
}
