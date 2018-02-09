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
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableSortedSet;

@Value.Immutable(copy=false)
@JsonSerialize(as = ImmutableIdsQuery.class)
@JsonDeserialize(builder = ImmutableIdsQuery.Builder.class)
@JsonTypeName(Query.IDS_QUERY)
public interface IdsQuery extends StandardQuery {
    @Value.Default
    @Value.NaturalOrder
    public default SortedSet<String> getTypes() {
        return ImmutableSortedSet.of();
    }

    @Value.Default
    @Value.NaturalOrder
    public default SortedSet<String> getValues() {
        return ImmutableSortedSet.of();
    }

    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterIdsQuery(this)) {
                visitor.leaveIdsQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @Override
    default boolean isEmpty() {
        return getTypes().isEmpty() && getValues().isEmpty();
    }
}
