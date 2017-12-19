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

import java.util.Arrays;
import java.util.List;

import org.immutables.value.Value;

import com.arakelian.elastic.Elastic.Version6;
import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.arakelian.elastic.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableBulkResponse.class)
@JsonDeserialize(builder = ImmutableBulkResponse.Builder.class)
public interface BulkResponse {
    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableBulkOperationResponse.class)
    @JsonDeserialize(builder = ImmutableBulkOperationResponse.Builder.class)
    @JsonPropertyOrder({ "_index", "_type", "_id", "_version", "result", "status", "created", "found",
            "error", "_shards" })
    public interface BulkOperationResponse extends VersionedDocumentId {
        @Nullable
        @Value.Auxiliary
        @JsonProperty("created")
        public Boolean getCreated();

        @Nullable
        @JsonProperty("error")
        public Cause getError();

        @Nullable
        @Value.Auxiliary
        @JsonProperty("found")
        public Boolean getFound();

        @Nullable
        @JsonProperty("_primary_term")
        @JsonView(Version6.class)
        public Integer getPrimaryTerm();

        @Nullable
        @JsonProperty("result")
        public String getResult();

        @Nullable
        @JsonProperty("_seq_no")
        @JsonView(Version6.class)
        public Integer getSeqNo();

        @Nullable
        @Value.Auxiliary
        @JsonProperty("_shards")
        public Shards getShards();

        @JsonProperty("status")
        public int getStatus();
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableItem.class)
    @JsonDeserialize(builder = ImmutableItem.Builder.class)
    public abstract class Item {
        @Value.Check
        protected void checkItem() {
            Preconditions.checkState(get() != null, "Must have one of " + Arrays.toString(Action.values()));
        }

        @Value.Derived
        public BulkOperationResponse get() {
            if (getCreate() != null) {
                return getCreate();
            }
            if (getDelete() != null) {
                return getDelete();
            }
            if (getIndex() != null) {
                return getIndex();
            }
            if (getUpdate() != null) {
                return getUpdate();
            }
            return null;
        }

        @Value.Derived
        public Action getAction() {
            if (getCreate() != null) {
                return Action.CREATE;
            }
            if (getDelete() != null) {
                return Action.DELETE;
            }
            if (getIndex() != null) {
                return Action.INDEX;
            }
            if (getUpdate() != null) {
                return Action.UPDATE;
            }
            return null;
        }

        @Nullable
        @JsonProperty("create")
        public abstract BulkOperationResponse getCreate();

        @Nullable
        @JsonProperty("delete")
        public abstract BulkOperationResponse getDelete();

        @Nullable
        @JsonProperty("index")
        public abstract BulkOperationResponse getIndex();

        @Nullable
        @JsonProperty("update")
        public abstract BulkOperationResponse getUpdate();

        @Value.Derived
        public boolean isSuccessful() {
            final int code = get().getStatus();
            return code >= 200 && code < 300;
        }
    }

    @Nullable
    @JsonProperty("errors")
    public Boolean getErrors();

    @Nullable
    @JsonProperty("items")
    public List<Item> getItems();

    @Nullable
    @JsonProperty("took")
    public Integer getTook();
}
