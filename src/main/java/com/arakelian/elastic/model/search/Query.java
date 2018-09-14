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

import java.io.Serializable;
import java.util.List;

import org.immutables.value.Value;

import com.arakelian.elastic.search.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({ //
        @JsonSubTypes.Type(name = Query.BOOL_QUERY, value = BoolQuery.class),
        @JsonSubTypes.Type(name = Query.EXISTS_QUERY, value = ExistsQuery.class), //
        @JsonSubTypes.Type(name = Query.FUZZY_QUERY, value = FuzzyQuery.class), //
        @JsonSubTypes.Type(name = Query.IDS_QUERY, value = IdsQuery.class), //
        @JsonSubTypes.Type(name = Query.MATCH_QUERY, value = MatchQuery.class), //
        @JsonSubTypes.Type(name = Query.PREFIX_QUERY, value = PrefixQuery.class), //
        @JsonSubTypes.Type(name = Query.QUERY_STRING_QUERY, value = QueryStringQuery.class), //
        @JsonSubTypes.Type(name = Query.RANGE_QUERY, value = RangeQuery.class), //
        @JsonSubTypes.Type(name = Query.REGEXP_QUERY, value = RegexpQuery.class), //
        @JsonSubTypes.Type(name = Query.TERMS_QUERY, value = TermsQuery.class), //
        @JsonSubTypes.Type(name = Query.WILDCARD_QUERY, value = WildcardQuery.class), //
        @JsonSubTypes.Type(name = Query.GEO_SHAPE_QUERY, value = GeoShapeQuery.class), //
        @JsonSubTypes.Type(name = Query.GEO_POLYGON_QUERY, value = GeoPolygonQuery.class), //
        @JsonSubTypes.Type(name = Query.GEO_DISTANCE_QUERY, value = GeoDistanceQuery.class), //
        @JsonSubTypes.Type(name = Query.GEO_BOUNDING_BOX_QUERY, value = GeoBoundingBoxQuery.class), //
        @JsonSubTypes.Type(name = Query.MORE_LIKE_THIS_QUERY, value = MoreLikeThisQuery.class), //
})
public interface Query extends Serializable {
    public static final String BOOL_QUERY = "bool";
    public static final String EXISTS_QUERY = "exists";
    public static final String FUZZY_QUERY = "fuzzy";
    public static final String IDS_QUERY = "ids";
    public static final String MATCH_QUERY = "match";
    public static final String PREFIX_QUERY = "prefix";
    public static final String QUERY_STRING_QUERY = "query_string";
    public static final String RANGE_QUERY = "range";
    public static final String REGEXP_QUERY = "regexp";
    public static final String TERMS_QUERY = "terms";
    public static final String WILDCARD_QUERY = "wildcard";
    public static final String GEO_SHAPE_QUERY = "geo_shape";
    public static final String GEO_POLYGON_QUERY = "geo_polygon";
    public static final String GEO_DISTANCE_QUERY = "geo_distance";
    public static final String GEO_BOUNDING_BOX_QUERY = "geo_bounding_box";
    public static final String MORE_LIKE_THIS_QUERY = "more_like_this";

    static int countNotEmpty(final List<Query> queries) {
        if (queries == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0, size = queries.size(); i < size; i++) {
            final Query q = queries.get(i);
            if (!q.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        visitor.leave(this);
    }

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public boolean isEmpty();
}
