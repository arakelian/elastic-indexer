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
import com.arakelian.elastic.Views.Elastic.Version6;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableAbout.class)
@JsonDeserialize(builder = ImmutableAbout.Builder.class)
public interface About {
    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableVersion.class)
    @JsonDeserialize(builder = ImmutableVersion.Builder.class)
    public interface Version {
        @Nullable
        @JsonProperty("build_date")
        public String getBuildDate();

        @JsonProperty("build_hash")
        public String getBuildHash();

        @JsonProperty("build_snapshot")
        public Boolean getBuildSnapshot();

        @Nullable
        @JsonProperty("build_timestamp")
        public String getBuildTimestamp();

        @JsonIgnore
        @Value.Derived
        public default VersionComponents getComponents() {
            return VersionComponents.of(getNumber());
        }

        @JsonProperty("lucene_version")
        public String getLuceneVersion();

        /**
         * Returns the minimum index compatibility version.
         *
         * @return minimum index compatibility version.
         **/
        @Nullable
        @JsonProperty("minimum_index_compatibility_version")
        @JsonView(Version6.class)
        public String getMinimumIndexCompatibilityVersion();

        /**
         * Returns the minimum wire compatibility version.
         *
         * @since Elastic 6.0
         * @return minimum wire compatibility version.
         **/
        @Nullable
        @JsonProperty("minimum_wire_compatibility_version")
        @JsonView(Version6.class)
        public String getMinimumWireCompatibilityVersion();

        @JsonProperty("number")
        public String getNumber();
    }

    @JsonProperty("cluster_name")
    public String getClusterName();

    @Nullable
    @JsonProperty("cluster_uuid")
    public String getClusterUuid();

    @JsonProperty("name")
    public String getName();

    @JsonProperty("tagline")
    public String getTagline();

    @JsonProperty("version")
    public Version getVersion();
}
