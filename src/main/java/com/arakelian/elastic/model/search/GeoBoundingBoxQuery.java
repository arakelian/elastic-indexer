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
import com.arakelian.elastic.model.enums.ValidationMethod;
import com.arakelian.elastic.search.QueryVisitor;
import com.arakelian.jackson.model.GeoPoint;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

/**
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/GeoBoundingBoxQueryBuilder.java">GeoBoundingBox
 *      Query</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableGeoBoundingBoxQuery.class)
@JsonDeserialize(builder = ImmutableGeoBoundingBoxQuery.Builder.class)
@JsonTypeName(Query.GEO_BOUNDING_BOX_QUERY)
public interface GeoBoundingBoxQuery extends StandardQuery {
    public enum Type {
        INDEXED, MEMORY
    };

    @Value.Check
    public default void checkPoints() {
        final boolean tlbr = getTopLeft() != null && //
                getBottomRight() != null && //
                getTopRight() == null && //
                getBottomLeft() == null;

        final boolean trbl = getTopRight() != null && //
                getBottomLeft() != null && //
                getTopLeft() == null && //
                getBottomRight() == null;

        Preconditions.checkState(
                tlbr || trbl,
                "Must specify top_left and bottom_right, or alternatively, top_right and bottom_left");
    }

    @Nullable
    @JsonProperty("bottom_left")
    public GeoPoint getBottomLeft();

    @Nullable
    @JsonProperty("bottom_right")
    public GeoPoint getBottomRight();

    @JsonProperty("field")
    public String getFieldName();

    @Nullable
    @JsonProperty("top_left")
    public GeoPoint getTopLeft();

    @Nullable
    @JsonProperty("top_right")
    public GeoPoint getTopRight();

    @Nullable
    @JsonProperty("type")
    public Type getType();

    @Nullable
    @JsonProperty("validation_method")
    public ValidationMethod getValidationMethod();

    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterGeoBoundingBoxQuery(this)) {
                visitor.leaveGeoBoundingBoxQuery(this);
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
