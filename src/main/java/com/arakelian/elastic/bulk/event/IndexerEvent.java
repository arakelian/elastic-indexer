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

package com.arakelian.elastic.bulk.event;

import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.arakelian.elastic.bulk.BulkOperation.VersionType;
import com.arakelian.elastic.model.Index;
import com.google.common.base.MoreObjects;

public class IndexerEvent {
    public static enum Status {
        FAILED, SUCCEEDED;
    }

    private Action action;
    private Index index;
    private String id;
    private String type;
    private Long version;
    private VersionType versionType;
    private Status status;

    public final Action getAction() {
        return action;
    }

    public final String getId() {
        return id;
    }

    public final Index getIndex() {
        return index;
    }

    public final Status getStatus() {
        return status;
    }

    public final String getType() {
        return type;
    }

    public final Long getVersion() {
        return version;
    }

    public final VersionType getVersionType() {
        return versionType;
    }

    public void reset() {
        action = null;
        index = null;
        id = null;
        type = null;
        version = null;
        versionType = null;
        status = null;
    }

    public final void setAction(final Action action) {
        this.action = action;
    }

    public final void setId(final String id) {
        this.id = id;
    }

    public final void setIndex(final Index index) {
        this.index = index;
    }

    public final void setStatus(final Status status) {
        this.status = status;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public final void setVersion(final Long version) {
        this.version = version;
    }

    public final void setVersionType(final VersionType versionType) {
        this.versionType = versionType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("action", action) //
                .add("index", index) //
                .add("id", id) //
                .add("type", type) //
                .add("version", version) //
                .add("versionType", versionType) //
                .add("status", status) //
                .toString();
    }
}
