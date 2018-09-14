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

package com.arakelian.elastic.model.aggs.bucket;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.aggs.BucketAggregation;
import com.arakelian.elastic.search.AggregationVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A special single bucket aggregation that enables aggregating on parent docs from nested
 * documents. Effectively this aggregation can break out of the nested block structure and link to
 * other nested structures or the root document, which allows nesting other aggregations that aren’t
 * part of the nested object in a nested aggregation.
 *
 * <p>
 * The <code>ReverseNestedAggregation</code> must be defined inside a {@link NestedAggregation}.
 * </p>
 *
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-reverse-nested-aggregation.html">Reverse
 *      Nested Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/bucket/nested/ReverseNestedAggregationBuilder.java">ReverseNestedAggregationBuilder.java</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableReverseNestedAggregation.class)
@JsonDeserialize(builder = ImmutableReverseNestedAggregation.Builder.class)
@JsonTypeName(Aggregation.REVERSE_NESTED_AGGREGATION)
public interface ReverseNestedAggregation extends BucketAggregation {
    @Nullable
    public String getPath();

    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterReverseNested(this)) {
                visitor.leaveReverseNested(this);
            }
        } finally {
            visitor.leave(this);
        }
    }
}
