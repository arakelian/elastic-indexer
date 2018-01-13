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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableClusterHealth.class)
@JsonDeserialize(builder = ImmutableClusterHealth.Builder.class)
public interface ClusterHealth {
    public static enum Status {
        GREEN, YELLOW, RED;

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    @JsonProperty("active_primary_shards")
    public Integer getActivePrimaryShards();

    @JsonProperty("active_shards")
    public Integer getActiveShards();

    @JsonProperty("active_shards_percent_as_number")
    public Double getActiveShardsPercentAsNumber();

    @JsonProperty("cluster_name")
    public String getClusterName();

    @JsonProperty("delayed_unassigned_shards")
    public Integer getDelayedUnassignedShards();

    @JsonProperty("initializing_shards")
    public Integer getInitializingShards();

    @JsonProperty("number_of_data_nodes")
    public Integer getNumberOfDataNodes();

    @JsonProperty("number_of_in_flight_fetch")
    public Integer getNumberOfInFlightFetch();

    @JsonProperty("number_of_nodes")
    public Integer getNumberOfNodes();

    @JsonProperty("number_of_pending_tasks")
    public Integer getNumberOfPendingTasks();

    @JsonProperty("relocating_shards")
    public Integer getRelocatingShards();

    @JsonProperty("status")
    public Status getStatus();

    @JsonProperty("task_max_waiting_in_queue_millis")
    public Integer getTaskMaxWaitingInQueueMillis();

    @JsonProperty("timed_out")
    public Boolean getTimedOut();

    @JsonProperty("unassigned_shards")
    public Integer getUnassignedShards();
}
