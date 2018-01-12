package com.arakelian.elastic.model.query;

import org.immutables.value.Value;

import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/ExistsQueryBuilder.java">Exists
 *      Query</a>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableExistsQuery.class)
@JsonDeserialize(builder = ImmutableExistsQuery.Builder.class)
@JsonTypeName(QueryClause.EXISTS_QUERY)
public interface ExistsQuery extends StandardQuery {
    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterExistsQuery(this)) {
                visitor.leaveExistsQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @JsonProperty("field")
    public String getFieldName();

    @Override
    default boolean isEmpty() {
        return false;
    }
}
