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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.TreeMultimap;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableNormalizeValues.class)
@JsonDeserialize(builder = ImmutableNormalizeValues.Builder.class)
@JsonTypeName(TokenFilter.NORMALIZE_VALUES)
public abstract class NormalizeValues implements TokenFilter, Serializable {
    @Override
    public <T extends Consumer<String>> T accept(final String value, final T output) {
        if (value == null) {
            // always pass nulls through to signal end of tokens
            output.accept(null);
        } else {
            final String normalized = normalize(value);
            output.accept(normalized);
        }
        return output;
    }

    @Value.Default
    @Value.Auxiliary
    @JsonProperty("codes")
    public Map<String, String> getCodes() {
        return ImmutableMap.of();
    }

    @JsonIgnore
    @Nullable
    @Value.Auxiliary
    @Value.Lazy
    public Method getFactory() throws IllegalStateException {
        final String className = getFactoryClass();
        final String methodName = getFactoryMethod();

        if (StringUtils.isEmpty(className)) {
            Preconditions.checkState(
                    StringUtils.isEmpty(methodName),
                    "factory_method cannot be specified if factory_class is empty");
            return null;
        }

        Preconditions.checkState(getCodes().size() == 0, "Cannot specify codes if factory_method is used");

        try {
            final Class<?> clazz = Class.forName(className);
            return clazz.getMethod(methodName, String.class);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(
                    "Cannot create factory method for " + className + "::" + methodName, e);
        }
    }

    @Nullable
    @JsonProperty("factory_class")
    public abstract String getFactoryClass();

    @Nullable
    @JsonProperty("factory_method")
    public abstract String getFactoryMethod();

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public Map<String, String> getForwardMapping() {
        if (isCaseSensitive()) {
            // map is already case-sensitive
            return getCodes();
        }

        // convert to case-insensitive mapping
        final Map<String, String> codes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        codes.putAll(getCodes());
        return Collections.unmodifiableMap(codes);
    }

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public Multimap<String, String> getInverseMapping() {
        final Multimap<String, String> values;

        if (!isCaseSensitive()) {
            values = TreeMultimap.create(String.CASE_INSENSITIVE_ORDER, String.CASE_INSENSITIVE_ORDER);
        } else {
            values = LinkedListMultimap.create();
        }

        final Map<String, String> codes = getCodes();
        for (final Map.Entry<String, String> entry : codes.entrySet()) {
            final String code = entry.getKey();
            final String value = entry.getValue();
            values.put(value, code);
        }

        return Multimaps.<String, String> unmodifiableMultimap(values);
    }

    @Value.Default
    @Value.Auxiliary
    @JsonProperty("case_senstive")
    public boolean isCaseSensitive() {
        return false;
    }

    private String lookup(final String input) {
        return getForwardMapping().get(input);
    }

    private String normalize(final String input) throws IllegalArgumentException, IllegalStateException {
        if (StringUtils.isEmpty(input)) {
            // null returns null, otherwise always non-null
            return input;
        }

        final Method method = getFactory();
        if (method == null) {
            // find code
            final Map<String, String> forwardMapping = getForwardMapping();
            if (forwardMapping.containsKey(input)) {
                return lookup(input);
            }

            // find value
            final Multimap<String, String> inverseMapping = getInverseMapping();
            if (inverseMapping.containsKey(input)) {
                final String code = inverseMapping.get(input).iterator().next();
                return lookup(code);
            }

            // original input
            return input;
        }

        // use factory method
        try {
            final Object result = method.invoke(null, new Object[] { input });

            final String normalized = Objects.toString(result, null);
            if (StringUtils.isEmpty(normalized)) {
                // return original value if we cannot normalize it
                return input;
            }

            return normalized;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot normalize input '" + input + "'", e);
        }
    }
}
