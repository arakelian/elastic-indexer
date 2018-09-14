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

package com.arakelian.elastic.model.search;

import java.io.Serializable;
import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableSourceFilter.class)
@JsonDeserialize(builder = ImmutableSourceFilter.Builder.class)
@JsonPropertyOrder({ "includes", "excludes" })
public interface SourceFilter extends Serializable {
    public static SourceFilter EXCLUDE_ALL = ImmutableSourceFilter.builder() //
            .addExclude("*") //
            .build();

    @Value.Default
    public default List<String> getExcludes() {
        return ImmutableList.of();
    }

    @Value.Default
    public default List<String> getIncludes() {
        return ImmutableList.of();
    }

    @JsonIgnore
    @Value.Derived
    public default boolean isEmpty() {
        return getIncludes().size() == 0 && getExcludes().size() == 0;
    }

    @JsonIgnore
    @Value.Derived
    public default boolean isExcludeAll() {
        return getIncludes().size() == 0 && getExcludes().size() == 1 && "*".equals(getExcludes().get(0));
    }
}
