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

import java.util.List;
import java.util.Set;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.search.QueryVisitor;
import com.arakelian.jackson.MapPath;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/MoreLikeThisQueryBuilder.java">More
 *      Like This Query</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableMoreLikeThisQuery.class)
@JsonDeserialize(builder = ImmutableMoreLikeThisQuery.Builder.class)
@JsonTypeName(Query.MORE_LIKE_THIS_QUERY)
public interface MoreLikeThisQuery extends StandardQuery {
    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableItem.class)
    @JsonDeserialize(builder = ImmutableItem.Builder.class)
    @JsonPropertyOrder({ "_index", "_type", "_id", "doc" })
    public interface Item {
        @Value.Check
        public default void checkItem() {
            Preconditions.checkState(
                    getId() == null && getDoc() != null || getId() != null && getDoc() == null,
                    "id or doc may be specified, but not both");
        }

        @Nullable
        @JsonProperty("doc")
        public MapPath getDoc();

        @Nullable
        @JsonProperty("_id")
        public String getId();

        @Nullable
        @JsonProperty("_index")
        public String getIndex();

        @Nullable
        @JsonProperty("_type")
        public String getType();
    }

    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterMoreLikeThisQuery(this)) {
                visitor.leaveMoreLikeThisQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    /**
     * Returns the analyzer that is used to analyze the free form text. Defaults to the analyzer
     * associated with the first field in fields
     *
     * @return the analyzer that is used to analyze the free form text
     */
    @Nullable
    @JsonProperty("analyzer")
    public String getAnalyzer();

    /**
     * Returns the additional boost for each term in the formed query. Defaults to deactivated (0).
     * Any other positive value activates terms boosting with the given boost factor.
     *
     * @return the additional boost for each term in the formed query
     */
    @Nullable
    @JsonProperty("boost_terms")
    public Float getBoostTerms();

    @Value.Default
    @JsonProperty("fields")
    public default Set<String> getFields() {
        return ImmutableSet.of();
    }

    @JsonIgnore
    @Value.Lazy
    public default List<Object> getLike() {
        return ImmutableList.builder() //
                .addAll(getLikeTexts()) //
                .addAll(getLikeItems()) //
                .build();
    }

    @Value.Default
    @JsonProperty("like_items")
    public default List<Item> getLikeItems() {
        return ImmutableList.of();
    }

    @Value.Default
    @JsonProperty("like_texts")
    public default List<String> getLikeTexts() {
        return ImmutableList.of();
    }

    /**
     * Returns the maximum document frequency above which the terms will be ignored from the input
     * document. This could be useful in order to ignore highly frequent words such as stop words.
     * Defaults to unbounded (0)
     *
     * @return the maximum document frequency above which the terms will be ignored from the input
     *         document
     */
    @Nullable
    @JsonProperty("max_doc_freq")
    public Integer getMaxDocFrequency();

    /**
     * Returns the maximum number of query terms that will be selected. Increasing this value gives
     * greater accuracy at the expense of query execution speed. Defaults to 25.
     *
     * @return the maximum number of query terms that will be selected
     */
    @Nullable
    @JsonProperty("max_query_terms")
    public Integer getMaxQueryTerms();

    /**
     * Returns the maximum word length above which the terms will be ignored. Defaults to unbounded
     * (0)
     *
     * @return the maximum word length above which the terms will be ignored
     */
    @Nullable
    @JsonProperty("max_word_length")
    public Integer getMaxWordLength();

    /**
     * Returns the minimum document frequency below which the terms will be ignored from the input
     * document. Defaults to 5
     *
     * @return the minimum document frequency below which the terms will be ignored from the input
     *         document
     */
    @Nullable
    @JsonProperty("min_doc_freq")
    public Integer getMinDocFrequency();

    /**
     * Returns the number of terms that must match after the disjunctive query has been formed. The
     * syntax is the same as the minimum should match. (Defaults to "30%").
     *
     * @return the number of terms that must match after the disjunctive query has been formed
     */
    @Nullable
    @JsonProperty("minimum_should_match")
    public String getMinimumShouldMatch();

    /**
     * Returns the minimum term frequency below which the terms will be ignored from the input
     * document. Defaults to 2
     *
     * @return the minimum term frequency below which the terms will be ignored from the input
     *         document
     */
    @Nullable
    @JsonProperty("min_term_freq")
    public Integer getMinTermFrequency();

    /**
     * Returns the minimum word length below which the terms will be ignored. Defaults to 0
     *
     * @return the minimum word length below which the terms will be ignored
     */
    @Nullable
    @JsonProperty("min_word_length")
    public Integer getMinWordLength();

    /**
     * Returns an array of stop words. Any word in this set is considered "uninteresting" and
     * ignored. If the analyzer allows for stop words, you might want to tell MLT to explicitly
     * ignore them, as for the purposes of document similarity it seems reasonable to assume that "a
     * stop word is never interesting".
     *
     * @return an array of stop words
     */
    @Value.Default
    @JsonProperty("stop_words")
    public default Set<String> getStopWords() {
        return ImmutableSet.of();
    }

    @JsonIgnore
    @Value.Lazy
    public default List<Object> getUnlike() {
        return ImmutableList.builder() //
                .addAll(getUnlikeTexts()) //
                .addAll(getUnlikeItems()) //
                .build();
    }

    @Value.Default
    @JsonProperty("unlike_items")
    public default List<Item> getUnlikeItems() {
        return ImmutableList.of();
    }

    @Value.Default
    @JsonProperty("unlike_texts")
    public default List<String> getUnlikeTexts() {
        return ImmutableList.of();
    }

    @Override
    default boolean isEmpty() {
        return false;
    }

    /**
     * Returns true if query should fail (throw an exception) if any of the specified fields are not
     * of the supported types (text or keyword'). Set this to `false to ignore the field and
     * continue processing. Defaults to true.
     *
     * @return true if query should fail if any of the specified fields are not of the supported
     *         types (text or keyword')
     */
    @Nullable
    @JsonProperty("fail_on_unsupported_field")
    public Boolean isFailOnUnsupportedField();

    /**
     * Returns whether the input documents should also be included in the search results returned.
     * Defaults to false
     *
     * @return whether the input documents should also be included in the search results returned
     */
    @Nullable
    @JsonProperty("include")
    public Boolean isInclude();
}
