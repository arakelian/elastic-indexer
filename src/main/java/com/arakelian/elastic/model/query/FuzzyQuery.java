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
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/FuzzyQueryBuilder.java">Fuzzy
 *      Query</a>
 *
 */
@Value.Immutable
@JsonSerialize(as = ImmutableFuzzyQuery.class)
@JsonDeserialize(builder = ImmutableFuzzyQuery.Builder.class)
@JsonTypeName(QueryClause.FUZZY_QUERY)
public interface FuzzyQuery extends StandardQuery {
    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterFuzzyQuery(this)) {
                visitor.leaveFuzzyQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @JsonProperty("field")
    public String getFieldName();

    @Nullable
    public String getFuzziness();

    @Nullable
    public Integer getMaxExpansions();

    @Nullable
    public Integer getPrefixLength();

    @Nullable
    public Rewrite getRewrite();

    @Nullable
    public Boolean getTranspositions();

    @Nullable
    public Object getValue();

    @Override
    default boolean isEmpty() {
        return getValue() == null;
    }
}
