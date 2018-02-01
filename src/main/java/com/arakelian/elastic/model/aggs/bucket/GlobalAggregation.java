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

import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.aggs.BucketAggregation;
import com.arakelian.elastic.search.AggregationVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Defines a single bucket of all the documents within the search execution context. This context is
 * defined by the indices and the document types you’re searching on, but is not influenced by the
 * search query itself.
 *
 * <p>
 * Note: Global aggregators can only be placed as top level aggregators because it doesn’t make
 * sense to embed a global aggregator within another bucket aggregator.
 * </p>
 *
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-global-aggregation.html">Global
 *      Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/bucket/global/GlobalAggregationBuilder.java">GlobalAggregationBuilder.java</a>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableGlobalAggregation.class)
@JsonDeserialize(builder = ImmutableGlobalAggregation.Builder.class)
@JsonTypeName(Aggregation.GLOBAL_AGGREGATION)
public interface GlobalAggregation extends BucketAggregation {
    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterGlobal(this)) {
                visitor.leaveGlobal(this);
            }
        } finally {
            visitor.leave(this);
        }
    }
}
