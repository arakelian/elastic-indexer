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

import java.util.SortedSet;

import org.immutables.value.Value;

import com.arakelian.elastic.search.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Terms query
 *
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/TermsQueryBuilder.java">Terms
 *      Query</a>
 *
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableTermsQuery.class)
@JsonDeserialize(builder = ImmutableTermsQuery.Builder.class)
@JsonTypeName(Query.TERMS_QUERY)
public interface TermsQuery extends StandardQuery {
    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterTermsQuery(this)) {
                visitor.leaveTermsQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @JsonProperty("field")
    public String getFieldName();

    @Value.Default
    @Value.NaturalOrder
    public default SortedSet<String> getValues() {
        return ImmutableSortedSet.of();
    }

    @Override
    default boolean isEmpty() {
        return getValues().isEmpty();
    }
}
