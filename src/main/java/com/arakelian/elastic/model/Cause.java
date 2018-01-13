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

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableCause.class)
@JsonDeserialize(builder = ImmutableCause.Builder.class)
@JsonPropertyOrder({ "type", "reason", "caused_by", "resource.type", "resource.id", "shard", "index",
        "index_uiud", "root_cause", "stack_trace" })
public interface Cause extends Serializable {
    public static final String VERSION_CONFLICT_ENGINE_EXCEPTION = "version_conflict_engine_exception";
    public static final String STRICT_DYNAMIC_MAPPING_EXCEPTION = "strict_dynamic_mapping_exception";

    @Nullable
    @JsonProperty("caused_by")
    public Cause getCausedBy();

    @Nullable
    @JsonProperty("index")
    public String getIndex();

    @Nullable
    @JsonProperty("index_uuid")
    public String getIndexUuid();

    @JsonProperty("reason")
    public String getReason();

    @Nullable
    @JsonProperty("resource.id")
    public String getResourceId();

    @Nullable
    @JsonProperty("resource.type")
    public String getResourceType();

    @Nullable
    @JsonProperty("root_cause")
    public List<Cause> getRootCause();

    @Nullable
    @JsonProperty("shard")
    public String getShard();

    @Nullable
    @JsonProperty("stack_trace")
    public String getStackTrace();

    /**
     * Returns the type of exception thrown by Elastic.
     *
     * Examples include:
     * <ul>
     * <li>version_conflict_engine_exception (status: 409). When using internal versioning, this
     * exception is thrown if the _version provided does not match version in the index; when using
     * external versioning, this exception indicates that there is a newer version in index
     * already.</li>
     * <li>strict_dynamic_mapping_exception (status:400). Indicates that the source document
     * contained fields which are not part of the _index/_type mapping.</li>
     * </ul>
     *
     * @return the type of exception thrown by Elastic.
     */
    @JsonProperty("type")
    public String getType();
}
