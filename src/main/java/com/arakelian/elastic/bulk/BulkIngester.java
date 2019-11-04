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

import static com.arakelian.elastic.bulk.BulkOperation.Action.DELETE;
import static com.arakelian.elastic.bulk.BulkOperation.Action.INDEX;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.arakelian.elastic.model.BulkResponse;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Indexes or deletes a group of documents from one or more Elastic indexes using the Elastic Bulk
 * Index API.
 */
public class BulkIngester {
    /** Configuration **/
    private final BulkIndexer bulkIndexer;

    private final BulkOperationFactory factory;

    public BulkIngester(final BulkOperationFactory factory, final BulkIndexer bulkIndexer) {
        this.bulkIndexer = Preconditions.checkNotNull(bulkIndexer, "bulkIndexer must be non-null");
        this.factory = Preconditions.checkNotNull(factory, "factory must be non-null");
    }

    /**
     * Deletes a list of documents from their respective Elastic indexes.
     *
     * @param documents
     *            list of documents to be removed
     * @return an optional future for retrieving bulk responses associated with this request
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> delete(final Collection<?> documents)
            throws RejectedExecutionException, IOException {
        if (documents == null || documents.size() == 0) {
            return Optional.empty();
        }

        List<BulkOperation> batch = null;
        for (final Object document : documents) {
            batch = makeBatch(document, DELETE, batch);
        }

        return add(batch, false);
    }

    /**
     * Delete specified document from Elastic index.
     *
     * @param document
     *            document to be deleted
     * @return an optional future for retrieving bulk responses associated with this request
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> delete(final Object document)
            throws RejectedExecutionException, IOException {
        return delete(document, false);
    }

    /**
     * Delete specified document from Elastic index.
     *
     * @param document
     *            document to be deleted
     * @param forceFlush
     *            true to force an immediate flush of data to Elastic
     * @return an optional future for retrieving bulk responses associated with this request
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> delete(
            final Object document,
            final boolean forceFlush) throws RejectedExecutionException, IOException {

        return add(makeBatch(document, DELETE, null), forceFlush);
    }

    /**
     * Adds a list of documents to the Elastic index without immediate index refresh and optional
     * flush.
     *
     * @param documents
     *            list of documents to index
     * @return an optional Future for retrieving bulk responses associated with this request.
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> index(final Collection<?> documents)
            throws RejectedExecutionException, IOException {
        if (documents == null || documents.size() == 0) {
            return Optional.empty();
        }

        List<BulkOperation> batch = null;
        for (final Object document : documents) {
            batch = makeBatch(document, INDEX, batch);
        }

        return add(batch, false);
    }

    /**
     * Adds a document to the Elastic index.
     *
     * @param document
     *            document to be indexed
     * @return an optional Future for retrieving bulk responses associated with this request.
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> index(final Object document)
            throws RejectedExecutionException, IOException {
        return index(document, false);
    }

    /**
     * Adds a document to the Elastic index.
     *
     * @param document
     *            document to be indexed
     * @param forceFlush
     *            true to force an immediate flush of data to Elastic
     * @return an optional Future for retrieving bulk responses associated with this request.
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Optional<ListenableFuture<List<BulkResponse>>> index(
            final Object document,
            final boolean forceFlush) throws RejectedExecutionException, IOException {

        return add(makeBatch(document, INDEX, null), forceFlush);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .omitNullValues() //
                .toString();
    }

    private Optional<ListenableFuture<List<BulkResponse>>> add(
            final List<BulkOperation> batch,
            final boolean forceFlush) {
        return bulkIndexer.add(batch, forceFlush);
    }

    /**
     * Adds a bulk operation to the queue, using the given document and specified action.
     *
     * @param document
     *            document
     * @param action
     *            action to be performed on document
     * @param forceFlush
     *            true to force an immediate flush of data to Elastic
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    private List<BulkOperation> makeBatch(
            final Object document,
            final Action action,
            List<BulkOperation> batch) throws RejectedExecutionException, IOException {
        if (document == null) {
            return batch;
        }

        // a document may be indexed to multiple places
        if (!factory.supports(document)) {
            throw new IOException("Unsupported document: " + document);
        }

        final List<BulkOperation> ops = factory.createBulkOperations(document, action);
        if (batch == null) {
            batch = Lists.newArrayList(ops);
        } else {
            batch.addAll(ops);
        }
        return batch;
    }
}
