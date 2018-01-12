package com.arakelian.elastic.model.query;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/PrefixQueryBuilder.java">Prefix
 *      Query</a>
 *
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePrefixQuery.class)
@JsonDeserialize(builder = ImmutablePrefixQuery.Builder.class)
@JsonTypeName(QueryClause.PREFIX_QUERY)
public interface PrefixQuery extends StandardQuery {
    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        if (visitor.enterPrefixQuery(this)) {
            visitor.leavePrefixQuery(this);
        }
        visitor.leave(this);
    }

    @JsonProperty("field")
    public String getFieldName();

    @Nullable
    public Rewrite getRewrite();

    @Nullable
    public String getValue();

    @Override
    default boolean isEmpty() {
        return StringUtils.isEmpty(getValue());
    }
}
