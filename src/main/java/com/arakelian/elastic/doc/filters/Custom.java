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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableCustom.class)
@JsonDeserialize(builder = ImmutableCustom.Builder.class)
@JsonTypeName(TokenFilter.CUSTOM)
public abstract class Custom extends AbstractCharFilter {
    @Override
    public String apply(final String value) {
        if (StringUtils.isEmpty(value)) {
            // nulls return null, otherwise always non-null
            return value;
        }

        // forward to delegate
        final CharFilter delegate = getDelegate();
        Preconditions.checkState(delegate != null, "Delegate must be non-null");
        return delegate.apply(value);
    }

    @Value.Default
    @Value.Auxiliary
    public Map<String, Object> getArguments() {
        return ImmutableMap.of();
    }

    public abstract String getClassName();

    @JsonIgnore
    @Nullable
    @Value.Auxiliary
    @Value.Lazy
    public CharFilter getDelegate() {
        final Class<? extends CharFilter> delegateClass = getDelegateClass();
        Preconditions.checkState(delegateClass != null, "Delegate class must be non-null");
        try {
            return JacksonUtils.getObjectMapper().convertValue(getArguments(), delegateClass);
        } catch (final IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Cannot create custom token filter with class \"" + delegateClass.getName() + "\"", e);
        }
    }

    @JsonIgnore
    @Nullable
    @Value.Auxiliary
    @Value.Lazy
    public Class<? extends CharFilter> getDelegateClass() {
        final String className = getClassName();
        Preconditions.checkState(
                !StringUtils.isEmpty(className),
                "Must specifiy class name for custom token filter");
        try {
            final Class<?> clazz = Class.forName(className);
            return clazz.asSubclass(CharFilter.class);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Cannot find custom token filter with class \"" + className + "\"", e);
        }
    }
}
