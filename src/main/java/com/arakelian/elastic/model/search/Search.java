/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arakelian.elastic.model.search;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.aggs.Aggregation;
import com.arakelian.elastic.model.enums.SearchType;
import com.arakelian.elastic.model.search.Highlighter.Highlight;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableSearch.class)
@JsonDeserialize(builder = ImmutableSearch.Builder.class)
@JsonPropertyOrder({ "scroll", "scrollId", "from", "size", "searchType", "terminateAfter", "_source",
        "stored_fields", "query", "sort", "aggregation", "version", "explain", "batchedReduceSize",
        "preference" })
@Value.Style(from = "using", get = { "is*", "get*" }, depluralize = true)
public interface Search extends Serializable {
    @Value.Default
    public default List<Aggregation> getAggregations() {
        return ImmutableList.of();
    }

    @Nullable
    public Integer getBatchedReduceSize();

    @Nullable
    public Integer getFrom();

    @Nullable
    public Highlight getHighlight();

    @Nullable
    public String getPreference();

    @Nullable
    public Query getQuery();

    @Nullable
    public String getScroll();

    @Nullable
    public String getScrollId();

    @Nullable
    public SearchType getSearchType();

    @Nullable
    public Integer getSize();

    @Value.Default
    public default List<Sort> getSorts() {
        return ImmutableList.of();
    }

    @Nullable
    @JsonProperty("_source")
    public SourceFilter getSourceFilter();

    /**
     * Returns a list of JSON fields that are explicitly stored in the mapping and should be
     * returned with search hits. Elastic recommends using source filtering instead to select
     * subsets of the original source document to be returned.
     *
     * <ul>
     * <li><code>*</code> can be used to load all stored fields from the document.</li>
     * <li><code>_none_</code> can be used to disable returning stored fields and metadata fields
     * entirely</li>
     * <li>An empty array will cause only the _id and _type for each hit to be returned</li>
     * <li>Only leaf fields can be returned; object fields can't be returned and such requests will
     * fail.</li>
     * </ul>
     *
     * @return a list of JSON fields that are explicitly stored in the mapping and should be
     *         returned with search hits
     */
    @Nullable
    @Value.Default
    @JsonProperty("stored_fields")
    public default Set<String> getStoredFields() {
        return ImmutableSet.of();
    }

    /**
     * Returns the maximum number of documents to collect for each shard, upon reaching which the
     * query execution will terminate early. If set, the response will have a boolean field
     * terminated_early to indicate whether the query execution has actually terminated_early.
     *
     * @return the maximum number of documents to collect for each shard
     */
    @Nullable
    public Integer getTerminateAfter();

    /**
     * Returns the explicit operation timeout, e.g. "20s"
     *
     * @return the explicit operation timeout
     */
    @Nullable
    public String getTimeout();

    /**
     * Returns true if an error should be returned if there is a partial search failure or timeout
     *
     * @return true if an error should be returned if there is a partial search failure or timeout
     */
    @Nullable
    public Boolean isAllowPartialSearchResults();

    @Nullable
    public Boolean isExplain();

    /**
     * Returns true to enable caching of search results for requests where size is 0, ie
     * aggregations and suggestions (no top hits returned).
     *
     * @return true to enable caching of search results
     */
    @Nullable
    public Boolean isRequestCache();

    /**
     * Returns true to calculate and return scores even if they are not used for sorting
     *
     * @return true to calculate and return scores even if they are not used for sorting
     */
    @Nullable
    public Boolean isTrackScores();

    /**
     * Returns true if the number of documents that match the query should be tracked
     *
     * @return true if the number of documents that match the query should be tracked
     */
    @Nullable
    public Boolean isTrackTotalHits();

    @Nullable
    public Boolean isVersion();
}
