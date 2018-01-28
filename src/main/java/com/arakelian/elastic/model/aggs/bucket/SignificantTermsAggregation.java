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

import java.util.List;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.aggs.BucketAggregation;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

/**
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-significantterms-aggregation.html">Significant
 *      Terms Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/bucket/significant/SignificantTermsAggregationBuilder.java">SignificantTermsAggregationBuilder.java</a>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableSignificantTermsAggregation.class)
@JsonDeserialize(builder = ImmutableSignificantTermsAggregation.Builder.class)
@JsonTypeName(Aggregation.SIGNIFICANT_TERMS_AGGREGATION)
public interface SignificantTermsAggregation extends BucketAggregation {
    @Value.Default
    public default List<Object> getExclude() {
        return ImmutableList.of();
    }

    /**
     * Returns the mechanism by which terms aggregation is executed.
     *
     * @return the mechanism by which terms aggregation is executed.
     */
    @Nullable
    public ExecutionHint getExecutionHint();

    @Value.Default
    public default List<Object> getInclude() {
        return ImmutableList.of();
    }

    /**
     * Returns the minimum number of hits required before returning a term.
     *
     * @return the minimum number of hits required before returning a term.
     */
    @Nullable
    public Long getMinDocCount();

    /**
     * Returns the minimum number of hits required on a local shard before a term is returned.
     *
     * @return the minimum number of hits required on a local shard before a term is returned.
     */
    @Nullable
    public Long getShardMinDocCount();

    /**
     * Returns how many terms the coordinating node will request from each shard. Once all the
     * shards responded, the coordinating node will then reduce them to a final result which will be
     * based on the size parameter - this way, one can increase the accuracy of the returned terms
     * and avoid the overhead of streaming a big list of buckets back to the client.
     *
     * @return the number of terms the coordinating node will request from each shard.
     */
    @Nullable
    public Long getShardSize();

    /**
     * Returns how many term buckets should be returned out of the overall terms list. By default,
     * the node coordinating the search process will request each shard to provide its own top size
     * term buckets and once all shards respond, it will reduce the results to the final list that
     * will then be returned to the client.
     *
     * @return how many term buckets should be returned out of the overall terms list.
     */
    @Nullable
    public Integer getSize();
}
