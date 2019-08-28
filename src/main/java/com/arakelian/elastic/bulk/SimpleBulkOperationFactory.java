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
import java.util.function.Predicate;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.core.utils.DateUtils;
import com.arakelian.elastic.Views;
import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.arakelian.elastic.doc.ElasticDocBuilder;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Value.Immutable
public abstract class SimpleBulkOperationFactory<T> implements BulkOperationFactory {
    @Override
    public List<BulkOperation> createBulkOperations(final Object doc, final Action action)
            throws IOException {
        try {
            if (doc instanceof String) {
                // hard-coded action
                final String id = (String) doc;
                return doCreateBulkOperations(id, action);
            }

            // type safe casting
            final T document = getDocumentClass().cast(doc);
            return doCreateBulkOperations(document, action);
        } catch (IllegalStateException | IllegalArgumentException | ClassCastException
                | UncheckedIOException e) {
            throw new IOException("Unable to create bulk operation (action=" + action + ", " + doc + ")", e);
        }
    }

    /**
     * Returns the date that we should use as delete version passed to Elastic.
     *
     * @return date that we should use as delete version
     */
    @Value.Default
    public Function<T, ZonedDateTime> getDeleteVersion() {
        return document -> {
            // when deleting a document from Elastic, we don't want to use the document date as our
            // timestamp (it would fail with "version conflict, current version [XXX] is higher or
            // equal to the one provided [XXX]"; that's because for deletes, the version we pass is
            // basically saying, "delete any version that is older than this timestamp"
            return DateUtils.nowWithZoneUtc();
        };
    }

    public abstract Class<T> getDocumentClass();

    @Nullable
    public abstract ElasticDocBuilder getElasticDocBuilder();

    @Value.Default
    public Function<CharSequence, CharSequence> getFromCharSequence() {
        return document -> {
            final ElasticDocBuilder elasticDocBuilder = getElasticDocBuilder();
            if (elasticDocBuilder == null) {
                // if no document builder provided, we are an identity transform
                return document;
            }

            // build document
            final CharSequence elasticDoc = elasticDocBuilder.build(document);
            return elasticDoc;
        };
    }

    @Value.Default
    public Function<JsonNode, CharSequence> getFromJsonNode() {
        return document -> {
            final ElasticDocBuilder elasticDocBuilder = getElasticDocBuilder();
            if (elasticDocBuilder == null) {
                // if no document builder provided, we are an identity transform
                return JacksonUtils.toStringSafe(document, false);
            }

            // build document
            final CharSequence elasticDoc = elasticDocBuilder.build(document);
            return elasticDoc;
        };
    }

    public abstract Function<T, String> getId();

    public abstract Index getIndex();

    @Value.Default
    public Function<T, Object> getJson() {
        return document -> {
            return JacksonUtils.toStringSafe(document, false);
        };
    }

    @Value.Default
    public Predicate<T> getPredicate() {
        return document -> true;
    }

    @Value.Default
    public Function<T, String> getType() {
        return document -> "_doc";
    }

    @Value.Default
    @Value.Auxiliary
    public Function<T, VersionComponents> getElasticVersion() {
        return document -> Views.VERSION_6.getVersion();
    }

    /**
     * Returns the date that we should use as index version passed to Elastic.
     *
     * @return date that we should use as index version
     */
    public abstract Function<T, ZonedDateTime> getVersion();

    @Override
    public boolean supports(final Object document) {
        final Class<T> clazz = getDocumentClass();
        return clazz.isInstance(document) && getPredicate().test(clazz.cast(document));
    }

    private List<BulkOperation> doCreateBulkOperations(final String id, final Action action) {
        Preconditions.checkArgument(action == Action.DELETE);

        final String type = getType().apply(null);
        final VersionComponents elasticVersion = getElasticVersion().apply(null);
        final BulkOperation operation = createBulkOperation(elasticVersion, action, type, id, null, null);
        return Lists.newArrayList(operation);
    }

    private List<BulkOperation> doCreateBulkOperations(final T document, final Action action) {
        // type
        final String type = getType()
                .apply(Preconditions.checkNotNull(document, "document must be non-null"));

        // id
        final String id = getId().apply(document);

        // version
        final ZonedDateTime versionDate;
        if (action == Action.DELETE) {
            versionDate = getDeleteVersion().apply(document);
        } else {
            versionDate = getVersion().apply(document);
        }

        final Long version;
        if (versionDate == null) {
            // versioning is disabled
            version = null;
        } else {
            // convert the version date provided to UTC Epoch milliseconds
            final ZonedDateTime utc = DateUtils.toUtc(versionDate);
            version = Long.valueOf(utc.toInstant().toEpochMilli());
        }

        // document
        final CharSequence source;
        if (action.hasSource()) {
            // should be CharSequence or JsonNode
            final Object json = getJson().apply(document);

            if (json instanceof CharSequence) {
                source = getFromCharSequence().apply((CharSequence) json);
            } else if (json instanceof JsonNode) {
                source = getFromJsonNode().apply((JsonNode) json);
            } else {
                throw new IllegalStateException("Cannot build Elastic document from: " + json);
            }
        } else {
            source = null;
        }

        // determine Elastic version
        final VersionComponents elasticVersion = getElasticVersion().apply(document);

        // create operation
        final BulkOperation operation = createBulkOperation(
                elasticVersion,
                action,
                type,
                id,
                source,
                version);
        return Lists.newArrayList(operation);
    }

    protected BulkOperation createBulkOperation(
            final VersionComponents elasticVersion,
            final Action action,
            final String type,
            final String id,
            final CharSequence source,
            final Long version) {
        return ImmutableBulkOperation.builder() //
                .elasticVersion(elasticVersion) //
                .action(action) //
                .index(getIndex()) //
                .type(type) //
                .id(id) //
                .source(source) //
                .version(version) //
                .versionType(EXTERNAL) //
                .build();
    }
}
