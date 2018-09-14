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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableIndexedShape.class)
@JsonDeserialize(builder = ImmutableIndexedShape.Builder.class)
@JsonPropertyOrder({ "index", "type", "id", "path" })
public interface IndexedShape extends Serializable {
    /**
     * Returns the ID of the document that containing the pre-indexed shape.
     *
     * @return ID of the document that containing the pre-indexed shape.
     */
    @Nullable
    @JsonProperty("id")
    public String getId();

    /**
     * Returns name of the index where the pre-indexed shape is. Defaults to "shapes".
     *
     * @return name of the index where the pre-indexed shape is
     */
    @JsonProperty("index")
    @Value.Default
    public default String getIndex() {
        return "shapes";
    }

    /**
     * Returns the field specified as path containing the pre-indexed shape. Defaults to "shape".
     *
     * @return the field specified as path containing the pre-indexed shape
     */
    @Nullable
    @JsonProperty("path")
    public String getPath();

    /**
     * Returns index type where the pre-indexed shape is.
     *
     * @return index type where the pre-indexed shape is.
     */
    @JsonProperty("type")
    @Value.Default
    public default String getType() {
        return "_doc";
    }
}
