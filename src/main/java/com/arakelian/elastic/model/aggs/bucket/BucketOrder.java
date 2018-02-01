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

import java.io.Serializable;

import org.immutables.value.Value;

import com.arakelian.elastic.model.search.SortOrder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableBucketOrder.class)
@JsonDeserialize(builder = ImmutableBucketOrder.Builder.class)
@JsonPropertyOrder({ "field", "order" })
public interface BucketOrder extends Serializable {
    public static final BucketOrder KEY_ASC = BucketOrder.of("_key", SortOrder.ASC);
    public static final BucketOrder KEY_DESC = BucketOrder.of("_key", SortOrder.DESC);
    public static final BucketOrder COUNT_ASC = BucketOrder.of("_count", SortOrder.ASC);
    public static final BucketOrder COUNT_DESC = BucketOrder.of("_count", SortOrder.DESC);

    public static BucketOrder of(final String name) {
        return ImmutableBucketOrder.builder().fieldName(name).build();
    }

    public static BucketOrder of(final String name, final SortOrder order) {
        return ImmutableBucketOrder.builder().fieldName(name).order(order).build();
    }

    /**
     * Returns the field to sort by.
     *
     * <ul>
     * <li>The field names <code>_term</code> and <code>_time</code> are deprecated, but synonymous
     * with reserved field name <code>_key</code> which orders the buckets alphabetically by
     * term.</li>
     * <li>The field name <code>_count</code> is reserved and order the buckets by their document
     * count</li>
     * <li>Any other field name is are sorting on a sub-aggregation</li>
     * </ul>
     *
     * @return the field to sort by
     *
     * @see <a href=
     *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/aggregations/InternalOrder.java">InternalOrder.java</a>
     */
    @JsonProperty("field")
    public String getFieldName();

    @Value.Default
    public default SortOrder getOrder() {
        return SortOrder.ASC;
    }
}
