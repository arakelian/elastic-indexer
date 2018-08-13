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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy=false)
@JsonSerialize(as = ImmutablePatternReplace.class)
@JsonDeserialize(builder = ImmutablePatternReplace.Builder.class)
@JsonTypeName(TokenFilter.PATTERN_REPLACE)
public abstract class PatternReplace extends AbstractCharFilter {
    @Override
    public String apply(final String value) {
        if (StringUtils.isEmpty(value)) {
            // nulls return null, otherwise always non-null
            return value;
        }

        final Matcher matcher = getCompiledPattern().matcher(value);
        return matcher.replaceAll(getReplacement());
    }

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public Pattern getCompiledPattern() {
        return Pattern.compile(getPattern());
    }

    public abstract String getPattern();

    public abstract String getReplacement();
}
