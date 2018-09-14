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

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.enums.ShapeRelation;
import com.arakelian.elastic.model.enums.SpatialStrategy;
import com.arakelian.elastic.search.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

/**
 * The geo_shape query uses the same grid square representation as the geo_shape mapping to find
 * documents that have a shape that intersects with the query shape. It will also use the same
 * PrefixTree configuration as defined for the field mapping.
 *
 * The query supports two ways of defining the query shape, either by providing a whole shape
 * definition, or by referencing the name of a shape pre-indexed in another index.
 *
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/GeoShapeQueryBuilder.java">GeoShape
 *      Query</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableGeoShapeQuery.class)
@JsonDeserialize(builder = ImmutableGeoShapeQuery.Builder.class)
@JsonTypeName(Query.GEO_SHAPE_QUERY)
public interface GeoShapeQuery extends StandardQuery {
    @Value.Check
    public default void checkShape() {
        final boolean haveBoth = getShape() != null && getIndexedShape() != null;
        Preconditions.checkState(!haveBoth, "Cannot specify shape and indexed_shape simultaneously");
    }

    @JsonProperty("field")
    public String getFieldName();

    @Nullable
    @JsonProperty("indexed_shape")
    public IndexedShape getIndexedShape();

    @Nullable
    @JsonProperty("relation")
    public ShapeRelation getRelation();

    @Nullable
    @JsonProperty("shape")
    public Shape getShape();

    @Nullable
    @JsonProperty("strategy")
    public SpatialStrategy getStrategy();

    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterGeoShapeQuery(this)) {
                visitor.leaveGeoShapeQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @Override
    default boolean isEmpty() {
        return false;
    }
}
