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

import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.model.GeoPoint;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class SourceTest {
    public static final Source SAMPLE = ImmutableSource.builder() //
            .putProperty("int", 1) //
            .putProperty("double", 3.0d) //
            .putProperty("string", "hello") //
            .putProperty("geopoint", "drm3btev3e86") //
            .build();

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPath() {
        assertNull(SAMPLE.getObject("geopoint/property"));
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(SAMPLE, Source.class);
    }

    @Test
    public void testMissingPath() {
        // path one-two-three-four doesn't exist
        assertNull(SAMPLE.getObject("one/two/three/four"));
    }

    @Test
    public void testPath() {
        // retrieve Integer
        assertEquals(Integer.valueOf(1), SAMPLE.getInt("int"));

        // retrieve Integer and make sure leading slash is ignored
        assertEquals(Integer.valueOf(1), SAMPLE.getInt("/int"));

        // retrieve Double
        assertEquals(Double.valueOf(3.0d), SAMPLE.getDouble("double"));

        // retrieve String
        assertEquals("hello", SAMPLE.get("string", String.class));

        // retrieve GeoPoint
        assertEquals(GeoPoint.of("drm3btev3e86"), SAMPLE.getGeoPoint("geopoint"));
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(SAMPLE, Source.class);
    }
}
