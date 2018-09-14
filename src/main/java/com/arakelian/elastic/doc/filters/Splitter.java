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

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableSplitter.class)
@JsonDeserialize(builder = ImmutableSplitter.Builder.class)
@JsonTypeName(TokenFilter.SPLITTER)
public abstract class Splitter implements TokenFilter, Serializable {
    public static final Splitter WHITESPACE = ImmutableSplitter.builder() //
            .pattern("\\s+") //
            .build();

    @Override
    public <T extends Consumer<String>> T accept(final String value, final T output) {
        if (value == null) {
            // always pass nulls through to signal end of tokens
            output.accept(null);
            return output;
        }

        for (final String token : getSplitter().split(value)) {
            output.accept(token);
        }
        return output;
    }

    @JsonIgnore
    @Value.Default
    @Value.Auxiliary
    public Pattern getCompiledPattern() {
        return Pattern.compile(getPattern());
    }

    public abstract String getPattern();

    @JsonIgnore
    @Value.Auxiliary
    @Value.Lazy
    public com.google.common.base.Splitter getSplitter() {
        return com.google.common.base.Splitter.on(getCompiledPattern()).omitEmptyStrings();
    }
}
