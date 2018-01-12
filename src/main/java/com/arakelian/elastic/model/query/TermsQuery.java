package com.arakelian.elastic.model.query;

import java.util.List;

import org.immutables.value.Value;

import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

/**
 *
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/TermsQueryBuilder.java">Terms
 *      Query</a>
 *
 */
@Value.Immutable
@JsonSerialize(as = ImmutableTermsQuery.class)
@JsonDeserialize(builder = ImmutableTermsQuery.Builder.class)
@JsonTypeName(QueryClause.TERMS_QUERY)
public interface TermsQuery extends StandardQuery {
    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterTermsQuery(this)) {
                visitor.leaveTermsQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @JsonProperty("field")
    public String getFieldName();

    @Value.Default
    public default List<String> getValues() {
        return ImmutableList.of();
    }

    @Override
    default boolean isEmpty() {
        return getValues().isEmpty();
    }
}
