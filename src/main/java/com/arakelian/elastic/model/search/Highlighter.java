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

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

/**
 * Highlighting settings can be set on a global level and overridden at the field level.
 *
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/search/fetch/subphase/highlight/AbstractHighlighterBuilder.java">AbstractHighlighterBuilder.java</a>
 */
public interface Highlighter extends Serializable {
    public enum BoundaryScanner {
        /**
         * Use the characters specified by boundary_chars as highlighting boundaries. The
         * boundary_max_scan setting controls how far to scan for boundary characters. Only valid
         * for the fvh highlighter.
         **/
        CHARS,

        /**
         * Break highlighted fragments at the next word boundary, as determined by Java’s
         * BreakIterator. You can specify the locale to use with boundary_scanner_locale.
         **/
        WORD,

        /**
         * Break highlighted fragments at the next sentence boundary, as determined by Java’s
         * BreakIterator. You can specify the locale to use with boundary_scanner_locale.
         **/
        SENTENCE;
    }

    public enum Encoder {
        DEFAULT, HTML;
    }

    @Value.Immutable
    @JsonSerialize(as = ImmutableField.class)
    @JsonDeserialize(builder = ImmutableField.Builder.class)
    @JsonPropertyOrder({ "name" })
    public interface Field extends Highlighter {
        public static Field of(final String name) {
            return ImmutableField.builder().name(name).build();
        }

        /**
         * Returns the margin from which you want to start highlighting. Only valid when using the
         * fvh highlighter.
         *
         * @return the margin from which you want to start highlighting
         */
        @Nullable
        public Integer getFragmentOffset();

        /**
         * Returns a list of fields whose matches are combined to highlight a single field.
         *
         * <p>
         * This is most intuitive for multifields that analyze the same string in different ways.
         * All matched_fields must have <code>term_vector</code> set to
         * <code>with_positions_offsets</code>, but only the field to which the matches are combined
         * is loaded so only that field benefits from having store set to yes. Only valid for the
         * fvh highlighter.
         * </p>
         *
         * @return a list of fields whose matches are combined to highlight a single field.
         */
        @Value.Default
        public default List<String> getMatchedFields() {
            return ImmutableList.of();
        }

        /**
         * Returns the field name to retrieve highlights for.
         *
         * <p>
         * You can use wildcards to specify fields. For example, you could specify comment_* to get
         * highlights for all text and keyword fields that start with comment_.
         * </p>
         *
         * @return the field name to retrieve highlights for.
         */
        public String getName();
    }

    public enum Fragmenter {
        SIMPLE, SPAN;
    }

    @Value.Immutable
    @JsonSerialize(as = ImmutableHighlight.class)
    @JsonDeserialize(builder = ImmutableHighlight.Builder.class)
    public interface Highlight extends Highlighter {
        @Value.Default
        public default List<Field> getFields() {
            return ImmutableList.of();
        }

        @Value.Default
        public default boolean isUseExplicitFieldOrder() {
            return true;
        }
    }

    public enum Order {
        NONE,

        /**
         * Sorts highlighted fragments by score. Only valid for the unified highlighter.
         **/
        SCORE;
    }

    public enum TagsSchema {
        DEFAULT, STYLED;
    }

    public enum Type {
        /**
         * The unified highlighter uses the Lucene Unified Highlighter. This highlighter breaks the
         * text into sentences and uses the BM25 algorithm to score individual sentences as if they
         * were documents in the corpus. It also supports accurate phrase and multi-term (fuzzy,
         * prefix, regex) highlighting. This is the default highlighter.
         **/
        UNIFIED,

        /**
         * The plain highlighter uses the standard Lucene highlighter. It attempts to reflect the
         * query matching logic in terms of understanding word importance and any word positioning
         * criteria in phrase queries.
         **/
        PLAIN,

        /**
         * The <code>fvh</code> highlighter uses the Lucene Fast Vector highlighter. This
         * highlighter can be used on fields with term_vector set to
         * <code>with_positions_offsets</code> in the mapping.
         **/
        FVH;
    }

    /**
     * Returns a string that contains each boundary character. Elastic defaults this to
     * <code>.,!? \t\n</code>.
     *
     * @return a string that contains each boundary character.
     */
    @Nullable
    public String getBoundaryChars();

    /**
     * Returns how far to scan for boundary characters. Defaults to 20.
     *
     * @return how far to scan for boundary characters
     */
    @Nullable
    public Integer getBoundaryMaxScan();

    /**
     * Returns how to break the highlighted fragments: <code>chars</code>, <code>sentence</code>, or
     * <code>word</code>
     *
     * @return how to break the highlighted fragments
     */
    @Nullable
    public BoundaryScanner getBoundaryScanner();

    /**
     * Returns the locale that is used to search for sentence and word boundaries.
     *
     * @return the locale that is used to search for sentence and word boundaries.
     */
    @Nullable
    public String getBoundaryScannerLocale();

    /**
     * Returns if the snippet should be HTML encoded: <code>default</code> (no encoding) or
     * <code>html</code> (HTML-escape the snippet text and then insert the highlighting tags)
     *
     * @return if the snippet should be HTML encoded
     */
    @Nullable
    public Encoder getEncoder();

    /**
     * Returns how text should be broken up in highlight snippets: <code>simple</code> or
     * <code>span</code>. Only valid for the <code>plain</code> highlighter.
     *
     * @return how text should be broken up in highlight snippets
     */
    @Nullable
    public Fragmenter getFragmenter();

    /**
     * Returns the size of the highlighted fragment in characters. Defaults to 100.
     *
     * @return the size of the highlighted fragment in characters
     */
    @Nullable
    public Integer getFragmentSize();

    @Nullable
    public Query getHighlightQuery();

    /**
     * Returns the amount of text you want to return from the beginning of the field if there are no
     * matching fragments to highlight. Defaults to 0 (nothing is returned).
     *
     * @return the amount of text you want to return from the beginning of the field if there are no
     *         matching fragments to highlight
     */
    @Nullable
    public Integer getNoMatchSize();

    /**
     * Returns the maximum number of fragments to return.
     *
     * <p>
     * If the number of fragments is set to 0, no fragments are returned. Instead, the entire field
     * contents are highlighted and returned. This can be handy when you need to highlight short
     * texts such as a title or address, but fragmentation is not required. If number_of_fragments
     * is 0, fragment_size is ignored. Defaults to 5.
     * </p>
     *
     * @return the maximum number of fragments to return.
     */
    @Nullable
    public Integer getNumberOfFragments();

    @Nullable
    public Order getOrder();

    @Nullable
    public Integer getPhraseLimit();

    /**
     * Returns a list of HTML tags to use after highlighted text. Used in conjunction with
     * {@link #getPreTags()}.
     *
     * @return a list of HTML tags to use after highlighted text
     */
    @Value.Default
    public default List<String> getPostTags() {
        return ImmutableList.of();
    }

    /**
     * Returns a list of HTML tags to use before highlighted text. Used in conjunction with
     * {@link #getPostTags()}.
     *
     * @return a list of HTML tags to use before highlighted text
     */
    @Value.Default
    public default List<String> getPreTags() {
        return ImmutableList.of();
    }

    @Nullable
    public TagsSchema getTagsSchema();

    @Nullable
    public Type getType();

    @Nullable
    public Boolean isForceSource();

    @Nullable
    public Boolean isHighlightFilter();

    /**
     * Returns true if only fields that contains a query match are highlighted. Set
     * require_field_match to false to highlight all fields. Defaults to true.
     *
     * @return true if only fields that contains a query match are highlighted
     */
    @Nullable
    public Boolean isRequireFieldMatch();
}
