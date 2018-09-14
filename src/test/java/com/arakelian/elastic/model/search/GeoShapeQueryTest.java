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

import java.io.IOException;

import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.elastic.model.enums.GeoShapeType;
import com.arakelian.elastic.model.enums.ShapeRelation;
import com.arakelian.elastic.model.enums.SpatialStrategy;
import com.arakelian.jackson.model.Coordinate;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class GeoShapeQueryTest {
    public static final GeoShapeQuery MINIMAL = ImmutableGeoShapeQuery.builder() //
            .fieldName("field") //
            .build();

    public static final GeoShapeQuery INDEXED_SHAPE = ImmutableGeoShapeQuery.builder() //
            .fieldName("field") //
            .indexedShape(
                    ImmutableIndexedShape.builder() //
                            .index("shapes") //
                            .type("_doc") //
                            .id("id") //
                            .path("location") //
                            .build()) //
            .relation(ShapeRelation.CONTAINS) //
            .strategy(SpatialStrategy.RECURSIVE) //
            .build();

    public static final GeoShapeQuery SHAPE = ImmutableGeoShapeQuery.builder() //
            .fieldName("field") //
            .shape(
                    ImmutableShape.builder() //
                            .type(GeoShapeType.LINESTRING) //
                            .addCoordinate(Coordinate.of(102.0, 2.0)) //
                            .addCoordinate(Coordinate.of(103.0, 2.0)) //
                            .build())
            .relation(ShapeRelation.CONTAINS) //
            .strategy(SpatialStrategy.RECURSIVE) //
            .build();

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(MINIMAL, GeoShapeQuery.class);
        JacksonTestUtils.testReadWrite(SHAPE, GeoShapeQuery.class);
        JacksonTestUtils.testReadWrite(INDEXED_SHAPE, GeoShapeQuery.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(MINIMAL, GeoShapeQuery.class);
        SerializableTestUtils.testSerializable(SHAPE, GeoShapeQuery.class);
        SerializableTestUtils.testSerializable(INDEXED_SHAPE, GeoShapeQuery.class);
    }
}
