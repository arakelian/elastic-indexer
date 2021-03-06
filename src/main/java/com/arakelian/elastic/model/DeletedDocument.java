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

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableDeletedDocument.class)
@JsonDeserialize(builder = ImmutableDeletedDocument.Builder.class)
@JsonPropertyOrder({ "_index", "_type", "_id", "_version", "_shards", "_seq_no", "_primary_term", "found",
        "result" })
public interface DeletedDocument extends VersionedDocumentId {
    @Nullable
    @JsonProperty("result")
    public String getResult();

    @Nullable
    @JsonProperty("found")
    @Value.Default
    public default Boolean isFound() {
        // Elastic 6.x doesn't return this flag so we simulate it
        final String result = getResult();
        if (result == null) {
            return null;
        }
        return "deleted".equals(result);
    }
}
