package com.arakelian.elastic.model.query;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.query.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableSet;

/**
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/RegexpQueryBuilder.java">Regexp
 *      Query</a>
 */
@Value.Immutable
@JsonSerialize(as = ImmutableRegexpQuery.class)
@JsonDeserialize(builder = ImmutableRegexpQuery.Builder.class)
@JsonTypeName(QueryClause.REGEXP_QUERY)
public interface RegexpQuery extends StandardQuery {
    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterRegexpQuery(this)) {
                visitor.leaveRegexpQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @JsonProperty("field")
    public String getFieldName();

    @Value.Default
    public default Set<RegexpFlag> getFlags() {
        return ImmutableSet.of();
    }

    @Nullable
    public Integer getMaxDeterminizedStates();

    @Nullable
    public Rewrite getRewrite();

    public String getValue();

    @Override
    default boolean isEmpty() {
        return StringUtils.isEmpty(getValue());
    }
}
