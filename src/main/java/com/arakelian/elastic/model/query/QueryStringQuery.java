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

package com.arakelian.elastic.model.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

@Value.Immutable
@JsonSerialize(as = ImmutableQueryStringQuery.class)
@JsonDeserialize(builder = ImmutableQueryStringQuery.Builder.class)
@JsonTypeName(QueryClause.QUERY_STRING_QUERY)
public interface QueryStringQuery extends StandardQuery {
    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        if (!visitor.enterQueryStringQuery(this)) {
            return;
        }
        visitor.leaveQueryStringQuery(this);
        visitor.leave(this);
    }

    @Nullable
    public String fuzzyRewrite();

    @Nullable
    public String getAnalyzer();

    @Nullable
    public String getDefaultField();

    @Nullable
    public Operator getDefaultOperator();

    @Value.Default
    public default List<String> getFields() {
        return ImmutableList.of();
    }

    @Nullable
    public String getFuzziness();

    @Nullable
    public Integer getFuzzyMaxExpansions();

    @Nullable
    public Integer getFuzzyPrefixLength();

    @Nullable
    public Integer getMaxDeterminizedStates();

    @Nullable
    public String getMinimumShouldMatch();

    @Nullable
    public MultiMatchType getMultiMatchType();

    @Nullable
    public Integer getPhraseSlop();

    public String getQueryString();

    @Nullable
    public String getQuoteAnalyzer();

    @Nullable
    public String getQuoteFieldSuffix();

    @Nullable
    public Rewrite getRewrite();

    @Nullable
    public Float getTieBreaker();

    @Nullable
    public String getTimeZone();

    @Nullable
    public Boolean isAllowLeadingWildcard();

    @Nullable
    public Boolean isAnalyzeWildcard();

    @Nullable
    public Boolean isAutoGenerateSynonymsPhraseQuery();

    @Override
    default boolean isEmpty() {
        return StringUtils.isEmpty(getQueryString());
    }

    @Nullable
    public Boolean isEnablePositionIncrements();

    @Nullable
    public Boolean isEscape();

    @Nullable
    public Boolean isFuzzyTranspositions();

    @Nullable
    public Boolean isLenient();
}
