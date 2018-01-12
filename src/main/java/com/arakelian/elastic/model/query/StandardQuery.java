package com.arakelian.elastic.model.query;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface StandardQuery extends QueryClause {
    @Nullable
    public Float getBoost();

    /**
     * Returns the name of the query
     *
     * @return name of the query
     * @see <a href=
     *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-named-queries-and-filters.html">Named
     *      Queries</a>
     */
    @Nullable
    @JsonProperty("_name")
    public String getName();
}
