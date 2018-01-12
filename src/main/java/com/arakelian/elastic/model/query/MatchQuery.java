package com.arakelian.elastic.model.query;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableMatchQuery.class)
@JsonDeserialize(builder = ImmutableMatchQuery.Builder.class)
@JsonTypeName(QueryClause.MATCH_QUERY)
public interface MatchQuery extends StandardQuery {
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
    public String getZeroTermsQuery();

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
