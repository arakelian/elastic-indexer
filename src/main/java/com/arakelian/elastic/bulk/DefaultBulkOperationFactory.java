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

import static com.arakelian.elastic.bulk.BulkOperation.VersionType.EXTERNAL;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.dao.feature.HasId;
import com.arakelian.dao.feature.HasTimestamp;
import com.arakelian.elastic.api.Index;
import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.arakelian.jackson.utils.JacksonUtils;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

public class DefaultBulkOperationFactory<T extends HasId> implements BulkOperationFactory<T> {
    private final Index index;
    private final String type;

    public DefaultBulkOperationFactory(final Index index, final String type) {
        this.index = index;
        this.type = type;
    }

    @Override
    public BulkOperation createBulkOperation(final Action action, final String id, final String source,
            final Long version) {
        return ImmutableBulkOperation.builder() //
                .action(action) //
                .index(index) //
                .type(type) //
                .id(id) //
                .source(source) //
                .version(version) //
                .versionType(EXTERNAL) //
                .build();
    }

    @Override
    public List<BulkOperation> getBulkOperations(final Action action, final T value) throws IOException {
        final String id = value.getId();
        final String source = action.hasSource() ? JacksonUtils.toString(value, false) : null;
        final ZonedDateTime versionDate = getVersionDate(action, value);
        final Long version = Long.valueOf(versionDate.toInstant().toEpochMilli());
        final BulkOperation operation = createBulkOperation(action, id, source, version);
        return Lists.newArrayList(operation);
    }

    /**
     * Returns the date that we should use as the basis of a version passed to Elastic.
     *
     * For {@link Action#CREATE}, {@link Action#INDEX} and {@link Action#UPDATE} requests, we check if
     * the value extends {@link HasTimestamp}, and use either the update date or create date if
     * provided; in all other cases, we'll fall back to use the current date.
     *
     * For {@link Action#DELETE} requests we cannot use the update or create date (as it it would fail
     * with "version conflict, current version [XXX] is higher or *equal* to the one provided [XXX]".
     * The version number we pass on a DELETE is version to be assigned to the DELETED document. It is
     * equivalent to saying, "delete any version that is OLDER than this timestamp, and then use that
     * timestamp as the new version number for the deleted document."
     *
     * @param action
     *            action to perform
     * @param value
     *            value being stored in Elastic
     * @return date that we should use as the basis of a version passed to Elastic.
     */
    protected ZonedDateTime getVersionDate(final Action action, final T value) {
        switch (action) {
        case CREATE:
        case INDEX:
        case UPDATE:
            if (value instanceof HasTimestamp) {
                final HasTimestamp hasTimestamp = (HasTimestamp) value;
                final ZonedDateTime date = MoreObjects.firstNonNull(hasTimestamp.getUpdated(),
                        hasTimestamp.getCreated());
                if (date != null) {
                    // make sure we are in UTC format
                    return DateUtils.toUtc(date);
                }
            }
            break;
        case DELETE:
        default:
            // when deleting a document from Elastic, we don't want to use the document date as our
            // timestamp (it would fail with "version conflict, current version [XXX] is higher or
            // equal to the one provided [XXX]"; that's because for deletes, the version we pass is
            // basically saying, "delete any version that is older than this timestamp"
            break;
        }

        // use current date/time
        return DateUtils.nowWithZoneUtc();
    };
}
