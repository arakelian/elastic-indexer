package com.arakelian.elastic.model.query;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
