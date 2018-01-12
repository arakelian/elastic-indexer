package com.arakelian.elastic.model.query;

import java.util.List;

import org.immutables.value.Value;

import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

@Value.Immutable
@JsonSerialize(as = ImmutableIdsQuery.class)
@JsonDeserialize(builder = ImmutableIdsQuery.Builder.class)
@JsonTypeName(QueryClause.IDS_QUERY)
public interface IdsQuery extends StandardQuery {
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

    @Value.Default
    public default List<String> getTypes() {
        return ImmutableList.of();
    }

    @Value.Default
    public default List<String> getValues() {
        return ImmutableList.of();
    }

    @Override
    default boolean isEmpty() {
        return getTypes().isEmpty() && getValues().isEmpty();
    }
}
