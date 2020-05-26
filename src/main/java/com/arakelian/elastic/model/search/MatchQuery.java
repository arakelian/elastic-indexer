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
import com.arakelian.elastic.model.enums.Operator;
import com.arakelian.elastic.search.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableMatchQuery.class)
@JsonDeserialize(builder = ImmutableMatchQuery.Builder.class)
@JsonTypeName(Query.MATCH_QUERY)
public interface MatchQuery extends StandardQuery {
    public enum ZeroTermsQuery {
        NONE, ALL;
    }

    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        if (visitor.enterMatchQuery(this)) {
            visitor.leaveMatchQuery(this);
        }
        visitor.leave(this);
    }

    @Nullable
    public String getAnalyzer();

    @Nullable
    public Float getCutoffFrequency();

    public String getFieldName();

    @Nullable
    public String getFuzziness();

    @Nullable
    public String getFuzzyRewrite();

    @Nullable
    public Integer getMaxExpansions();

    @Nullable
    public String getMinimumShouldMatch();

    @Nullable
    public Operator getOperator();

    @Nullable
    public Integer getPrefixLength();

    @Nullable
    public Object getValue();

    @Nullable
    public ZeroTermsQuery getZeroTermsQuery();

    @Value.Derived
    @JsonIgnore
    @Value.Auxiliary
    public default boolean hasMatchDefaults() {
        return hasStandardDefaults() && //
                getOperator() == null && //
                getFuzziness() == null && //
                getFuzzyRewrite() == null && //
                getPrefixLength() == null && //
                getMaxExpansions() == null && //
                getCutoffFrequency() == null && //
                getZeroTermsQuery() == null && //
                isAutoGenerateSynonymsPhraseQuery() == null;
    }

    @Nullable
    public Boolean isAutoGenerateSynonymsPhraseQuery();

    @Override
    default boolean isEmpty() {
        return getValue() == null;
    }

    @Nullable
    public Boolean isFuzzyTranspositions();

    @Nullable
    public Boolean isLenient();
}
