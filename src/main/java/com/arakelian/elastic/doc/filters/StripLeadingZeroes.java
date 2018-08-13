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

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Splitter;

@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableStripLeadingZeroes.class)
@JsonDeserialize(builder = ImmutableStripLeadingZeroes.Builder.class)
@JsonTypeName(TokenFilter.STRIP_LEADING_ZEROES)
public abstract class StripLeadingZeroes implements TokenFilter, Serializable {
    @Override
    public <T extends Consumer<String>> T accept(final String value, final T output) {
        if (value == null) {
            // always pass nulls through to signal end of tokens
            output.accept(null);
            return output;
        }

        final String stripped = strip(value);
        final boolean identical = StringUtils.equals(value, stripped);

        if (isEmitOriginal() || identical) {
            output.accept(value);
        }
        if (!identical) {
            output.accept(stripped);
        }
        return output;
    }

    @JsonIgnore
    @Nullable
    @Value.Default
    @Value.Auxiliary
    public Pattern getCompiledPattern() {
        final String pattern = getPattern();
        return StringUtils.isEmpty(pattern) ? null : Pattern.compile(pattern);
    }

    @Nullable
    public abstract String getPattern();

    @JsonIgnore
    @Nullable
    @Value.Auxiliary
    @Value.Lazy
    public com.google.common.base.Splitter getSplitter() {
        final Pattern pattern = getCompiledPattern();
        if (pattern != null) {
            return com.google.common.base.Splitter.on(pattern).omitEmptyStrings();
        }
        return null;
    }

    @Value.Default
    @JsonProperty("emit_original")
    public boolean isEmitOriginal() {
        return true;
    }

    private String strip(final String input) {
        if (input == null || input.length() == 0) {
            return input;
        }

        final Splitter splitter = getSplitter();
        if (splitter == null) {
            // not splitting input into words
            return stripWord(input);
        }

        // strip from individual words
        StringBuilder buf = null;
        for (final String token : splitter.split(input)) {
            final String stripped = stripWord(token);
            if (StringUtils.isEmpty(stripped)) {
                continue;
            }

            if (buf == null) {
                buf = new StringBuilder();
            } else {
                buf.append(' ');
            }
            buf.append(stripped);
        }

        if (buf == null) {
            // nothing left after stripping leading zeroes
            return StringUtils.EMPTY;
        }

        if (StringUtils.equals(buf, input)) {
            // original is unchanged
            return input;
        }

        // we have removed some zeroes
        return buf.toString();
    }

    private String stripWord(final String input) {
        final int length = input.length();
        int end = length;
        int start = 0;

        for (;;) {
            boolean changed = false;

            // skip leading zeroes
            while (start < end && input.charAt(start) == '0') {
                start++;
                changed = true;
            }

            // trim whitespace
            while (start < end && Character.isWhitespace(input.charAt(start))) {
                start++;
                changed = true;
            }

            while (start < end && Character.isWhitespace(input.charAt(end - 1))) {
                end--;
                changed = true;
            }

            if (!changed) {
                break;
            }
        }

        return start > 0 || end < length ? input.substring(start, end) : input;
    }
}
