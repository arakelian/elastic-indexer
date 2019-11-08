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
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.elastic.utils.JsonNodeUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;

@Value.Immutable
@JsonSerialize(as = ImmutableJsonSelector.class)
@JsonDeserialize(builder = ImmutableJsonSelector.Builder.class)
public abstract class JsonSelector implements Serializable {
    public static enum Type {
        PATH, JSON_PATH, FUNCTION, CONCAT;
    }

    /** Regular expression to match an identifier **/
    private static final String IDENTIFIER = "(?:\\b[_a-zA-Z]|\\B)[_a-zA-Z0-9]*+";

    /** Match @FUNCTION name at start of selector **/
    static final Pattern FUNCTION = Pattern.compile("^\\@(" + IDENTIFIER + ")\\b");

    /**
     * Break part a path that uses forward slash or dot notation.
     *
     * Note: By omitting empty strings, we collapse multiple dots or slashes with nothing in
     * between. We do this to be forgiving in the input.
     **/
    private static final Splitter PATH_SPLITTER = Splitter //
            .on(Pattern.compile("[/\\.]")) //
            .trimResults() //
            .omitEmptyStrings();

    /** Break apart a comma separated argument list **/
    private static final Splitter ARG_SPLITTER = Splitter //
            .on(Pattern.compile("\\s*,\\s*")) //
            .trimResults();

    /**
     * Used to build a canonical path, with clean separators
     */
    private static final Joiner PATH_JOINER = Joiner.on("/");

    /**
     * Used to build a canonical path, with clean separators
     */
    private static final Joiner ARG_JOINER = Joiner.on(", ");

    public static JsonSelector of(final String selector) {
        return ImmutableJsonSelector.builder() //
                .selector(selector) //
                .build();
    }

    private static List<String> toPath(final String selector) {
        // split path on / or .
        final List<String> path = PATH_SPLITTER.splitToList(selector);

        // path cannot be empty
        final int length = path.size();
        Preconditions.checkState(length != 0, "path cannot be empty");
        return ImmutableList.copyOf(path);
    }

    @JsonIgnore
    @Value.Lazy
    @Value.Auxiliary
    public Map<String, List<String>> getArguments() {
        final Type type = getType();

        final int start;
        if (type == Type.FUNCTION) {
            // skip @ sign
            start = getFunctionName().length() + 1;
        } else if (type == Type.CONCAT) {
            // skip + sign
            start = 1;
        } else {
            return ImmutableMap.of();
        }

        final ImmutableMap.Builder<String, List<String>> map = ImmutableMap.builder();
        for (final String arg : ARG_SPLITTER.splitToList(StringUtils.substring(getSelector(), start))) {
            final List<String> path = toPath(arg);
            final String normalizePath = PATH_JOINER.join(path);
            map.put(normalizePath, path);
        }
        return map.build();
    }

    @JsonIgnore
    @Value.Lazy
    @Value.Auxiliary
    public String getFunctionName() {
        Preconditions.checkState(getType() == Type.FUNCTION);
        final String selector = getSelector();
        final Matcher matcher = FUNCTION.matcher(selector);
        Preconditions.checkState(matcher.find(), "Invalid function name: " + selector);
        return matcher.group(1);
    }

    @JsonIgnore
    @Value.Lazy
    @Value.Auxiliary
    public JsonPath getJsonPath() throws IllegalStateException {
        Preconditions.checkState(getType() == Type.JSON_PATH);
        try {
            return JsonPath.compile(getSelector());
        } catch (final InvalidPathException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @JsonIgnore
    @Value.Lazy
    @Value.Auxiliary
    public List<String> getPath() {
        Preconditions.checkState(getType() == Type.PATH);
        final String selector = getSelector();
        return toPath(selector);
    }

    public abstract String getSelector();

    @JsonIgnore
    @Value.Lazy
    @Value.Auxiliary
    public Type getType() throws IllegalStateException {
        final String selector = getSelector();

        // must have a path
        if (StringUtils.isEmpty(selector)) {
            throw new IllegalStateException("selector must be non-empty");
        }

        // quick test to see if explicit JsonPath
        final char first = selector.charAt(0);
        switch (first) {
        case '$':
            return Type.JSON_PATH;
        case '@':
            return Type.FUNCTION;
        case '+':
            return Type.CONCAT;
        default:
            return Type.PATH;
        }
    }

    @Value.Check
    public JsonSelector normalizeSelector() {
        final String val = getSelector();
        final String newVal;

        try {
            final Type type = getType();

            switch (type) {
            case PATH:
                newVal = PATH_JOINER.join(getPath());
                break;
            case JSON_PATH:
                newVal = getJsonPath().getPath();
                break;
            case CONCAT:
                newVal = "+ " + ARG_JOINER.join(getArguments().keySet());
                break;
            case FUNCTION:
                newVal = "@" + getFunctionName() + " " + ARG_JOINER.join(getArguments().keySet());
                break;
            default:
                newVal = val;
            }
        } catch (final IllegalStateException e) {
            // cannot normalize invalid selector
            return this;
        }

        if (!StringUtils.equals(val, newVal)) {
            return ((ImmutableJsonSelector) this).withSelector(newVal);
        }
        return this;
    }

    public JsonNode read(final JsonNode node) {
        Preconditions.checkState(getType() == Type.PATH);
        return JsonNodeUtils.read(node, getPath());
    }

    public void read(final JsonNode node, final Consumer<JsonNode> consumer) {
        Preconditions.checkState(getType() == Type.PATH);
        JsonNodeUtils.read(node, consumer, getPath());
    }
}
