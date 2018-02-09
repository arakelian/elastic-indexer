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
import com.arakelian.elastic.model.aggs.ValuesSourceAggregation;
import com.arakelian.elastic.search.AggregationVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-sampler-aggregation.html">Sampler
 *      Aggregation</a>
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/bucket/sampler/SamplerAggregationBuilder.java">SamplerAggregationBuilder.java</a>
 */
@Value.Immutable(copy=false)
@JsonSerialize(as = ImmutableSamplerAggregation.class)
@JsonDeserialize(builder = ImmutableSamplerAggregation.Builder.class)
@JsonTypeName(Aggregation.SAMPLER_AGGREGATION)
public interface SamplerAggregation extends BucketAggregation, ValuesSourceAggregation {
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

    @Override
    default void accept(final AggregationVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterSampler(this)) {
                visitor.leaveSampler(this);
            }
        } finally {
            visitor.leave(this);
        }
    }
}
