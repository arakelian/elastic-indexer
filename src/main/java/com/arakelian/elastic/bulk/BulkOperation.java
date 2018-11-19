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

package com.arakelian.elastic.bulk;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.Index;
import com.arakelian.json.JsonFilter;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;

/**
 * Represents a source document that will be indexed into Elastic.
 */
@Value.Immutable(copy = false)
public abstract class BulkOperation {
    /**
     * Bulk actions are INDEX, CREATE, DELETE and UPDATE.
     *
     * @see <a href=
     *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html">Bulk
     *      API</a>
     */
    public static enum Action {
    /**
     * CREATE has put-if-absent semantics. The index operation will fail if a document by that id
     * already exists in the index.
     **/
    CREATE(true),

    /**
     * DELETE does not expect a source on the following line, and has the same semantics as the
     * standard delete API.
     **/
    DELETE(false),

    /**
     * Unlike {@link #CREATE}, INDEX will add or replace document as necessary.
     */
    INDEX(true),

    /**
     * UPDATE expects that next line is a partial document or a script.
     */
    UPDATE(true);

        private final boolean hasSource;

        private Action(final boolean hasSource) {
            this.hasSource = hasSource;
        }

        public final boolean hasSource() {
            return hasSource;
        }

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    // see: https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html
    public enum VersionType {
        INTERNAL, //
        EXTERNAL, //
        EXTERNAL_GTE;

        @Override
        @JsonValue
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Returns mapping type within index.
     *
     * @return mapping type within index.
     */
    public abstract Action getAction();

    @Nullable
    @Value.Derived
    @Value.Auxiliary
    public CharSequence getCompactSource() {
        final CharSequence source = getSource();
        if (source != null && StringUtils.indexOf(source, '\n') != -1) {
            return JsonFilter.compactQuietly(source);
        }
        return source;
    }

    /**
     * Returns the id of the document.
     *
     * @return id of the document.
     */
    public abstract String getId();

    /**
     * Returns index that this document will be indexed into.
     *
     * @return index that this document will be indexed into.
     */
    public abstract Index getIndex();

    @Nullable
    @Value.Auxiliary
    public abstract BulkOperationListener getListener();

    @Value.Derived
    @Value.Auxiliary
    public CharSequence getOperation() {
        final CharSequence source;
        if (getAction().hasSource()) {
            source = getCompactSource();
        } else {
            source = null;
        }

        // try to pre-allocate buffer large enough to hold operation
        final int size = BulkIndexer.roundAllocation(128 + //
                (source != null ? source.length() : 0));

        final StringBuilder buf = new StringBuilder(size);
        addActionAndMetadata(buf);

        if (source != null) {
            buf.append(source).append('\n');
        }
        return buf;
    }

    /**
     * Returns source JSON document that will be indexed by {@link BulkIndexer}
     *
     * @return source JSON document that will be indexed by {@link BulkIndexer}
     */
    @Nullable
    @Value.Auxiliary
    public abstract CharSequence getSource();

    /**
     * Returns mapping type within index.
     *
     * @return mapping type within index.
     */
    @Value.Default
    public String getType() {
        // not meaningful for Elastic 7+
        return "_doc";
    }

    @Nullable
    public abstract Long getVersion();

    @Nullable
    @Value.Default
    public VersionType getVersionType() {
        return getVersion() != null ? VersionType.EXTERNAL : null;
    }

    private void addActionAndMetadata(final StringBuilder buf) {
        final Action action = getAction();

        buf.append('{');
        buf.append("\"").append(action.toString()).append("\"").append(':');
        buf.append('{');

        buf.append("\"_index\"").append(':');
        buf.append('"').append(getIndex().getName()).append('"');

        buf.append(',');
        buf.append("\"_type\"").append(':');
        buf.append('"').append(getType()).append('"');

        buf.append(',');
        buf.append("\"_id\"").append(':');
        buf.append('"').append(getId()).append('"');

        if (getVersion() != null) {
            buf.append(',');
            buf.append("\"_version\"").append(':');
            buf.append(getVersion());
            if (getVersionType() != null) {
                buf.append(',');
                buf.append("\"version_type\"").append(':');
                buf.append('"').append(getVersionType()).append('"');
            }
        }

        buf.append('}');
        buf.append('}');
        buf.append('\n');
    }

    @Value.Check
    protected void checkSource() {
        if (getAction().hasSource()) {
            final CharSequence source = getCompactSource();
            Preconditions.checkState(source != null && source.length() != 0, "Source must be non-empty");
            Preconditions.checkState(
                    StringUtils.indexOf(source, '\n') == -1,
                    "Newlines are not allowed in source JSON document when using Elastic bulk API");
        }
    }
}
