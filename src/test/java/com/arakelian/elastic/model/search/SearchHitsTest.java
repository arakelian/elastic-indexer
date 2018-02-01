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

package com.arakelian.elastic.model.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.elastic.model.AbstractElasticModelTest;
import com.arakelian.elastic.model.GeoPoint;
import com.arakelian.jackson.utils.JacksonTestUtils;
import com.google.common.collect.ImmutableMap;

public class SearchHitsTest extends AbstractElasticModelTest {
    @SuppressWarnings("MutableConstantField")
    private static final Map<String, Object> SOURCE = ImmutableMap
            .of("int", 1, "double", 3.0d, "string", "hello", "geopoint", "drm3btev3e86");

    public static final SearchHits SAMPLE = ImmutableSearchHits.builder() //
            .total(3) //
            .maxScore(3.0f) //
            .addHit(ImmutableMap.of("_index", "files", "_id", "one", "_score", 1, "_source", SOURCE)) //
            .addHit(ImmutableMap.of("_index", "files", "_id", "two", "_score", 2, "_source", SOURCE)) //
            .addHit(ImmutableMap.of("_index", "files", "_id", "three", "_score", 3, "_source", SOURCE)) //
            .build();

    public SearchHitsTest(final String number) {
        super(number);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPath() {
        assertNull(SAMPLE.get(0, "_source/geopoint/property", Object.class));
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(objectMapper, SAMPLE, SearchHits.class);
    }

    @Test
    public void testMissingPath() {
        // path one-two-three-four doesn't exist
        assertNull(SAMPLE.get(0, "_source/one/two/three/four", Object.class));

        // hit index is out of range
        assertNull(SAMPLE.get(Integer.MAX_VALUE, "index/not/valid", Object.class));
        assertNull(SAMPLE.get(Integer.MIN_VALUE, "index/not/valid", Object.class));
    }

    @Test
    public void testPath() {
        // retrieve Integer
        assertEquals(Integer.valueOf(1), SAMPLE.getInt(0, "_source/int"));

        // retrieve Integer and make sure leading slash is ignored
        assertEquals(Integer.valueOf(1), SAMPLE.getInt(0, "/_source/int"));

        // retrieve Double
        assertEquals(Double.valueOf(3.0d), SAMPLE.getDouble(0, "_source/double"));

        // retrieve String
        assertEquals("hello", SAMPLE.get(0, "_source/string", String.class));

        // retrieve GeoPoint
        assertEquals(GeoPoint.of("drm3btev3e86"), SAMPLE.getGeoPoint(0, "_source/geopoint"));
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(SAMPLE, SearchHits.class);
    }
}
