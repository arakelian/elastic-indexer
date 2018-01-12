package com.arakelian.elastic.model.query;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/RangeQueryBuilder.java">Range
 *      Query</a>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableRangeQuery.class)
@JsonDeserialize(builder = ImmutableRangeQuery.Builder.class)
@JsonTypeName(QueryClause.RANGE_QUERY)
@Value.Style(from = "using")
public interface RangeQuery extends StandardQuery {
    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterRangeQuery(this)) {
                visitor.leaveRangeQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    public String getFieldName();

    @JsonProperty("from")
    @Nullable
    public Object getLower();

    @Nullable
    public ShapeRelation getRelation();

    @JsonProperty("to")
    @Nullable
    public Object getUpper();

    @Override
    default boolean isEmpty() {
        return getLower() == null && getUpper() == null;
    }

    @Value.Default
    public default boolean isIncludeLower() {
        return true;
    }

    @Value.Default
    public default boolean isIncludeUpper() {
        return true;
    }
}
