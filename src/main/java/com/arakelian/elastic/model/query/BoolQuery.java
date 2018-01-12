package com.arakelian.elastic.model.query;

import java.util.List;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

@Value.Immutable
@JsonSerialize(as = ImmutableBoolQuery.class)
@JsonDeserialize(builder = ImmutableBoolQuery.Builder.class)
@JsonTypeName(QueryClause.BOOL_QUERY)
public interface BoolQuery extends StandardQuery {
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
                for (final QueryClause c : getMustClauses()) {
                    c.accept(visitor);
                }
                for (final QueryClause c : getMustNotClauses()) {
                    c.accept(visitor);
                }
                for (final QueryClause c : getFilterClauses()) {
                    c.accept(visitor);
                }
                for (final QueryClause c : getShouldClauses()) {
                    c.accept(visitor);
                }
            } finally {
                visitor.leaveBoolQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @Value.Default
    public default List<QueryClause> getFilterClauses() {
        return ImmutableList.of();
    }

    @Nullable
    public String getMinimumShouldMatch();

    @Value.Default
    public default List<QueryClause> getMustClauses() {
        return ImmutableList.of();
    }

    @Value.Default
    public default List<QueryClause> getMustNotClauses() {
        return ImmutableList.of();
    }

    @Value.Default
    public default List<QueryClause> getShouldClauses() {
        return ImmutableList.of();
    }

    @Nullable
    public Boolean isAdjustPureNegative();

    @Override
    default boolean isEmpty() {
        return getMustClauses().isEmpty() && //
                getMustNotClauses().isEmpty() && //
                getFilterClauses().isEmpty() && //
                getShouldClauses().isEmpty();
    }
}
