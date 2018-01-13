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
import java.util.Set;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@Value.Immutable
@JsonSerialize(as = ImmutableNodes.class)
@JsonDeserialize(builder = ImmutableNodes.Builder.class)
@JsonPropertyOrder({ "_nodes", "cluster_name", "nodes" })
public interface Nodes extends Serializable {
    @Value.Immutable
    @JsonSerialize(as = ImmutableNodeInfo.class)
    @JsonDeserialize(builder = ImmutableNodeInfo.Builder.class)
    @JsonIgnoreProperties(value = { "master", "data" }, allowGetters = true)
    public interface NodeInfo extends Serializable {
        @Value.Immutable
        @JsonSerialize(as = ImmutableHttp.class)
        @JsonDeserialize(builder = ImmutableHttp.Builder.class)
        @JsonPropertyOrder({ "bound_address", "publish_address", "max_content_length_in_bytes" })
        public interface Http extends Serializable {
            @JsonProperty("bound_address")
            @Value.Default
            public default List<String> getBoundAddresses() {
                return ImmutableList.of();
            }

            @JsonProperty("max_content_length_in_bytes")
            public int getMaxContentLengthInBytes();

            @JsonProperty("publish_address")
            public String getPublishAddress();
        }

        public Map<String, Object> getAttributes();

        @Nullable
        @JsonProperty("build_hash")
        public String getBuildHash();

        public String getHost();

        public Http getHttp();

        @Nullable
        @JsonProperty("http_address")
        public String getHttpAddress();

        public String getIp();

        public String getName();

        @Nullable
        @Value.Default
        @JsonProperty("roles")
        public default Set<String> getRoles() {
            return ImmutableSet.of();
        }

        @JsonProperty("transport_address")
        public String getTransportAddress();

        public String getVersion();

        @Value.Derived
        public default boolean isData() {
            return getRoles().contains("data");
        }

        @Value.Derived
        public default boolean isMaster() {
            return getRoles().contains("master");
        }
    }

    @JsonProperty("cluster_name")
    public String getClusterName();

    @JsonProperty("nodes")
    public Map<String, NodeInfo> getNodes();

    @Nullable
    @JsonProperty("_nodes")
    public Shards getShards();
}
