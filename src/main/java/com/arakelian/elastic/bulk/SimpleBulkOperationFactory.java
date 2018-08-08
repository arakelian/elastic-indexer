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
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

import org.immutables.value.Value;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.arakelian.elastic.model.Index;
import com.arakelian.jackson.utils.JacksonUtils;
import com.google.common.collect.Lists;

@Value.Immutable
public interface SimpleBulkOperationFactory<T> extends BulkOperationFactory {
    public default BulkOperation createBulkOperation(
            final Action action,
            final String type,
            final String id,
            final String source,
            final Long version) {
        return ImmutableBulkOperation.builder() //
                .action(action) //
                .index(getIndex()) //
                .type(type) //
                .id(id) //
                .source(source) //
                .version(version) //
                .versionType(EXTERNAL) //
                .build();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.arakelian.elastic.bulk.BulkOperationFactory#getBulkOperations(com.arakelian.elastic.bulk.
     * BulkOperation.Action, java.lang.Object)
     */
    @Override
    public default List<BulkOperation> getBulkOperations(final Action action, final Object doc)
            throws IOException {
        try {
            // type safe casting
            final T document = getDocumentClass().cast(doc);

            // type
            final String type = getType().apply(document);

            // id
            final String id = getId().apply(document);

            // version
            ZonedDateTime versionDate = getVersion().apply(document);

            final Long version;
            if (versionDate == null) {
                // versioning is disabled
                version = null;
            } else {
                if (action == Action.DELETE) {
                    versionDate = getDeleteVersion().apply(document);
                }
                final ZonedDateTime utc = DateUtils.toUtc(versionDate);
                version = Long.valueOf(utc.toInstant().toEpochMilli());
            }

            // document
            final String source = action.hasSource() ? getElasticDocument().apply(document) : null;

            // create operation
            final BulkOperation operation = createBulkOperation(action, type, id, source, version);
            return Lists.newArrayList(operation);
        } catch (IllegalStateException | IllegalArgumentException | ClassCastException
                | UncheckedIOException e) {
            throw new IOException("Unable to create bulk operation (action=" + action + ", " + doc + ")", e);
        }
    }

    /**
     * Returns the date that we should use as delete version passed to Elastic.
     *
     * For {@link Action#DELETE} requests we cannot use the update or create date (as it it would
     * fail with "version conflict, current version [XXX] is higher or *equal* to the one provided
     * [XXX]". The version number we pass on a DELETE is version to be assigned to the DELETED
     * document. It is equivalent to saying, "delete any version that is OLDER than this timestamp,
     * and then use that timestamp as the new version number for the deleted document."
     *
     * @return date that we should use as delete version
     */
    @Value.Default
    public default Function<T, ZonedDateTime> getDeleteVersion() {
        return document -> {
            // when deleting a document from Elastic, we don't want to use the document date as our
            // timestamp (it would fail with "version conflict, current version [XXX] is higher or
            // equal to the one provided [XXX]"; that's because for deletes, the version we pass is
            // basically saying, "delete any version that is older than this timestamp"
            return DateUtils.nowWithZoneUtc();
        };
    }

    public Class<T> getDocumentClass();

    @Value.Default
    public default Function<T, String> getElasticDocument() {
        return document -> {
            try {
                return JacksonUtils.toString(document, false);
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    public Function<T, String> getId();

    public Index getIndex();

    @Value.Default
    public default Function<T, String> getType() {
        return document -> "document";
    }

    /**
     * Returns the date that we should use as index version passed to Elastic.
     *
     * @return date that we should use as index version
     */
    public Function<T, ZonedDateTime> getVersion();
}
