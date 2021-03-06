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

package com.arakelian.elastic.doc.filters;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@JsonTypeInfo(property = "type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({ //
        @JsonSubTypes.Type(name = TokenFilter.LENGTH_FILTER, value = LengthFilter.class), //
        @JsonSubTypes.Type(name = TokenFilter.LOWERCASE, value = Lowercase.class),
        @JsonSubTypes.Type(name = TokenFilter.REPLACE_CONTROL_CHARACTERS, value = ReplaceControlCharacters.class), //
        @JsonSubTypes.Type(name = TokenFilter.NORMALIZE_PUNCTUATION, value = NormalizePunctuation.class), //
        @JsonSubTypes.Type(name = TokenFilter.NORMALIZE_VALUES, value = NormalizeValues.class), //
        @JsonSubTypes.Type(name = TokenFilter.PATTERN_CAPTURE, value = PatternCapture.class), //
        @JsonSubTypes.Type(name = TokenFilter.PATTERN_REPLACE, value = PatternReplace.class), //
        @JsonSubTypes.Type(name = TokenFilter.REDUCE_WHITESPACE, value = ReduceWhitespace.class), //
        @JsonSubTypes.Type(name = TokenFilter.SORT, value = SortFilter.class), //
        @JsonSubTypes.Type(name = TokenFilter.SPLITTER, value = Splitter.class), //
        @JsonSubTypes.Type(name = TokenFilter.STRIP_LEADING_ZEROES, value = StripLeadingZeroes.class), //
        @JsonSubTypes.Type(name = TokenFilter.STRIP_WHITESPACE, value = StripWhitespace.class), //
        @JsonSubTypes.Type(name = TokenFilter.TRIM_WHITESPACE, value = TrimWhitespace.class), //
        @JsonSubTypes.Type(name = TokenFilter.TRUNCATE, value = Truncate.class), //
        @JsonSubTypes.Type(name = TokenFilter.UPPERCASE, value = Uppercase.class), //
        @JsonSubTypes.Type(name = TokenFilter.REVERSE, value = Reverse.class), //
        @JsonSubTypes.Type(name = TokenFilter.CUSTOM, value = Custom.class), //
        @JsonSubTypes.Type(name = TokenFilter.NULL, value = NullFilter.class) //
})
@FunctionalInterface
public interface TokenFilter {
    public static final String LOWERCASE = "lowercase";
    public static final String UPPERCASE = "uppercase";
    public static final String REVERSE = "reverse";
    public static final String TRUNCATE = "truncate";
    public static final String NORMALIZE_PUNCTUATION = "normalize_punctuation";
    public static final String REPLACE_CONTROL_CHARACTERS = "replace_control_characters";
    public static final String STRIP_WHITESPACE = "strip_whitespace";
    public static final String TRIM_WHITESPACE = "trim_whitespace";
    public static final String REDUCE_WHITESPACE = "reduce_whitespace";
    public static final String PATTERN_REPLACE = "pattern_replace";
    public static final String LENGTH_FILTER = "length";
    public static final String PATTERN_CAPTURE = "pattern_capture";
    public static final String SPLITTER = "splitter";
    public static final String NORMALIZE_VALUES = "normalize_values";
    public static final String STRIP_LEADING_ZEROES = "strip_leading_zeroes";
    public static final String CUSTOM = "custom";
    public static final String SORT = "sort";
    public static final String NULL = "null";

    public <T extends Consumer<String>> T accept(String value, T output);

    public default List<String> execute(final CharSequence csq) {
        final ImmutableList.Builder<String> tokens = ImmutableList.builder();
        execute(csq, token -> {
            tokens.add(token.toString());
        });
        return tokens.build();
    }

    public default void execute(final CharSequence csq, final Consumer<CharSequence> consumer) {
        // null value is used to flush token filters that buffer
        accept(null, token -> {
            // discard any leftovers from last execution; perhaps there was an exception and
            // pipeline was not reset
        });

        accept(
                csq != null ? csq.toString() : StringUtils.EMPTY,
                token -> {
                    // we only store non-empty strings in document
                    if (!StringUtils.isEmpty(token)) {
                        consumer.accept(token);
                    }
                });

        // null value is used to flush token filters that buffer
        for (final AtomicBoolean changed = new AtomicBoolean();; changed.set(false)) {
            accept(null, token -> {
                if (!StringUtils.isEmpty(token)) {
                    consumer.accept(token);
                    changed.set(true);
                }
            });
            if (!changed.get()) {
                break;
            }
        }
    }

    @Value.Default
    public default Set<String> getTags() {
        return ImmutableSet.of();
    }
}
