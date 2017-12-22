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
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

@Value.Immutable
@JsonSerialize(as = ImmutablePatternCapture.class)
@JsonDeserialize(builder = ImmutablePatternCapture.Builder.class)
@JsonTypeName(TokenFilter.PATTERN_CAPTURE)
public abstract class PatternCapture implements TokenFilter, Serializable {
    @Override
    public <T extends Consumer<String>> T accept(final String value, final T output) {
        if (value == null) {
            // ignore nulls
            return output;
        }

        if (isPreserveOriginal()) {
            output.accept(value);
        }

        for (final Pattern pattern : getCompilePatterns()) {
            final Matcher matcher = pattern.matcher(value);
            while (matcher.find()) {
                final int count = matcher.groupCount();
                if (count == 0) {
                    // no capture groups, so output entire match
                    output.accept(matcher.group());
                } else {
                    // when capture groups are defined, only output those
                    for (int i = 1; i <= count; i++) {
                        output.accept(matcher.group(i));
                    }
                }
            }
        }

        // allow chaining
        return output;
    }

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public List<Pattern> getCompilePatterns() {
        final ImmutableList.Builder<Pattern> patterns = ImmutableList.builder();
        for (final String pattern : getPatterns()) {
            patterns.add(Pattern.compile(pattern));
        }
        return patterns.build();
    }

    public abstract List<String> getPatterns();

    @Value.Default
    public boolean isPreserveOriginal() {
        return false;
    }
}
