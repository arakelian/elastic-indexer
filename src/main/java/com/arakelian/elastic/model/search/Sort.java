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

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.enums.SortMode;
import com.arakelian.elastic.model.enums.SortOrder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableSort.class)
@JsonDeserialize(builder = ImmutableSort.Builder.class)
@JsonPropertyOrder({ "field", "order", "mode" })
public interface Sort extends Serializable {
    public static Sort of(final String name) {
        return ImmutableSort.builder().fieldName(name).build();
    }

    public static Sort of(final String name, final SortOrder order) {
        return ImmutableSort.builder().fieldName(name).order(order).build();
    }

    @JsonProperty("field")
    public String getFieldName();

    @Nullable
    public SortMode getMode();

    @Value.Default
    public default SortOrder getOrder() {
        return SortOrder.ASC;
    }

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public default boolean isDefaults() {
        return getOrder() == SortOrder.ASC && getMode() == null;
    }
}
