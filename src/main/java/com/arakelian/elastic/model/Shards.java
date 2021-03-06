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
import java.util.Map;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableShards.class)
@JsonDeserialize(builder = ImmutableShards.Builder.class)
public interface Shards extends Serializable {
    @Nullable
    @JsonProperty("failed")
    public Integer getFailed();

    @JsonAnyGetter
    @Value.Default
    public default Map<String, Object> getProperties() {
        return ImmutableMap.of();
    }

    @Nullable
    @JsonProperty("skipped")
    public Integer getSkipped();

    @Nullable
    @JsonProperty("successful")
    public Integer getSuccessful();

    @Nullable
    @JsonProperty("total")
    public Integer getTotal();
}
