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

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(property = "type", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({ //
        @JsonSubTypes.Type(name = TokenFilter.LOWERCASE, value = Lowercase.class),
        @JsonSubTypes.Type(name = TokenFilter.UPPERCASE, value = Uppercase.class), //
        @JsonSubTypes.Type(name = TokenFilter.PATTERN_REPLACE, value = PatternReplace.class), //
        @JsonSubTypes.Type(name = TokenFilter.NORMALIZE_PUNCTUATION, value = NormalizePunctuation.class), //
        @JsonSubTypes.Type(name = TokenFilter.STRIP_WHITESPACE, value = StripWhitespace.class), //
        @JsonSubTypes.Type(name = TokenFilter.REDUCE_WHITESPACE, value = ReduceWhitespace.class), //
        @JsonSubTypes.Type(name = TokenFilter.TRIM_WHITESPACE, value = TrimWhitespace.class), //
        @JsonSubTypes.Type(name = TokenFilter.LENGTH_FILTER, value = LengthFilter.class), //
        @JsonSubTypes.Type(name = TokenFilter.PATTERN_CAPTURE, value = PatternCapture.class), //
        @JsonSubTypes.Type(name = TokenFilter.SPLITTER, value = Splitter.class), //
        @JsonSubTypes.Type(name = TokenFilter.NULL, value = NullFilter.class) //
})
@FunctionalInterface
public interface TokenFilter {
    public static final String LOWERCASE = "Lowercase";
    public static final String UPPERCASE = "Uppercase";
    public static final String NORMALIZE_PUNCTUATION = "NormalizeTypography";
    public static final String STRIP_WHITESPACE = "StripWhitespace";
    public static final String TRIM_WHITESPACE = "TrimWhitespace";
    public static final String REDUCE_WHITESPACE = "ReduceWhitespace";
    public static final String PATTERN_REPLACE = "PatternReplace";
    public static final String LENGTH_FILTER = "LengthFilter";
    public static final String PATTERN_CAPTURE = "PatternCapture";
    public static final String SPLITTER = "Splitter";
    public static final String NULL = "Null";

    public <T extends Consumer<String>> T accept(String value, T output);
}
