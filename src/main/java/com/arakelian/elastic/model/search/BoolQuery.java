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

import java.util.List;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.search.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableBoolQuery.class)
@JsonDeserialize(builder = ImmutableBoolQuery.Builder.class)
@JsonTypeName(Query.BOOL_QUERY)
public interface BoolQuery extends StandardQuery {
    @Value.Default
    public default List<Query> getFilterClauses() {
        return ImmutableList.of();
    }

    @Nullable
    public String getMinimumShouldMatch();

    @Value.Default
    public default List<Query> getMustClauses() {
        return ImmutableList.of();
    }

    @Value.Default
    public default List<Query> getMustNotClauses() {
        return ImmutableList.of();
    }

    @Value.Default
    public default List<Query> getShouldClauses() {
        return ImmutableList.of();
    }

    @Nullable
    public Boolean isAdjustPureNegative();

    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (!visitor.enterBoolQuery(this)) {
                return;
            }
            try {
                for (final Query q : getMustClauses()) {
                    q.accept(visitor);
                }
                for (final Query q : getMustNotClauses()) {
                    q.accept(visitor);
                }
                for (final Query q : getFilterClauses()) {
                    q.accept(visitor);
                }
                for (final Query q : getShouldClauses()) {
                    q.accept(visitor);
                }
            } finally {
                visitor.leaveBoolQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @Override
    default boolean isEmpty() {
        return getMustClauses().isEmpty() && //
                getMustNotClauses().isEmpty() && //
                getFilterClauses().isEmpty() && //
                getShouldClauses().isEmpty();
    }
}
