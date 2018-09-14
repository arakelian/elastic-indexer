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

package com.arakelian.elastic.model;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Splitter;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableJsonSelector.class)
@JsonDeserialize(builder = ImmutableJsonSelector.Builder.class)
public abstract class JsonSelector implements Serializable {
    private static final class AlwaysTrue implements Predicate {
        private static final AlwaysTrue INSTANCE = new AlwaysTrue();

        private AlwaysTrue() {
        }

        @Override
        public boolean apply(final PredicateContext ctx) {
            return true;
        }
    }

    private static final Splitter PATH = Splitter.on(Pattern.compile("[/\\.]")).omitEmptyStrings();

    public static JsonSelector of(final String selector) {
        return ImmutableJsonSelector.builder().selector(selector).build();
    }

    @JsonIgnore
    @Value.Lazy
    @Value.Auxiliary
    public JsonPath getJsonPath() throws InvalidPathException {
        final String selector = getSelector();

        // must have a path
        if (StringUtils.isEmpty(selector)) {
            throw new InvalidPathException("path must be non-empty");
        }

        // quick test to see if explicit JsonPath
        final char first = selector.charAt(0);
        if (first == '$') {
            return JsonPath.compile(selector);
        }

        // get list of properties
        final List<String> props = PATH.splitToList(selector);
        final int length = props.size();
        if (length == 0) {
            throw new InvalidPathException("path must have at least one property");
        }

        // convert simple string to a JsonPath
        final StringBuilder buf = new StringBuilder();
        buf.append('$');

        final Predicate[] predicates = new Predicate[length];
        for (int i = 0; i < length; i++) {
            buf.append("[?]");
            predicates[i] = AlwaysTrue.INSTANCE;
            buf.append(props.get(i));
        }

        final String jsonPath = buf.toString();
        return JsonPath.compile(jsonPath, predicates);
    }

    public abstract String getSelector();
}
