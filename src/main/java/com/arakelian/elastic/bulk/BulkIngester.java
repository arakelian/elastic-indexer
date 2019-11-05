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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Function;

import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.arakelian.elastic.model.BulkResponse;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Indexes or deletes a group of documents from one or more Elastic indexes using the Elastic Bulk
 * Index API.
 */
public class BulkIngester {
    /** Name of default indexer **/
    private static final String DEFAULT_INDEXER = "default";

    /** Bulk indexers by name **/
    private final Map<String, BulkIndexer> bulkIndexers;

    /** Bulk operation factory **/
    private final BulkOperationFactory bulkOperationFactory;

    /** Function that takes an index name and returns the indexer name that should be used **/
    private final Function<String, String> indexToIndexer;

    public BulkIngester(final BulkOperationFactory factory, final BulkIndexer bulkIndexer) {
        this(factory, ImmutableMap.of(DEFAULT_INDEXER, bulkIndexer), (index) -> DEFAULT_INDEXER);
    }

    public BulkIngester(
            final BulkOperationFactory bulkOperationFactory,
            final Map<String, BulkIndexer> bulkIndexers,
            final Function<String, String> indexToIndexer) {
        this.bulkIndexers = Preconditions.checkNotNull(bulkIndexers, "bulkIndexers must be non-null");
        this.bulkOperationFactory = Preconditions
                .checkNotNull(bulkOperationFactory, "bulkOperationFactory must be non-null");
        this.indexToIndexer = Preconditions.checkNotNull(indexToIndexer, "indexToIndexer must be non-null");
        Preconditions.checkArgument(this.bulkIndexers.size() != 0, "Must have at least one bulkIndexer");
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
    public Map<String, Optional<ListenableFuture<List<BulkResponse>>>> delete(final Collection<?> documents)
            throws RejectedExecutionException, IOException {
        return delete(documents, false);
    }

    /**
     * Deletes a list of documents from their respective Elastic indexes, and optionally flushes
     * those deletes to Elastic immediately.
     *
     * @param documents
     *            list of documents to be removed
     * @param forceFlush
     *            true to flush associated index so that deletes are immediately processed
     * @return an optional future for retrieving bulk responses associated with this request
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Map<String, Optional<ListenableFuture<List<BulkResponse>>>> delete(
            final Collection<?> documents,
            final boolean forceFlush) throws RejectedExecutionException, IOException {

        if (documents == null || documents.size() == 0) {
            return ImmutableMap.of();
        }

        Multimap<String, BulkOperation> batches = null;
        for (final Object document : documents) {
            batches = makeBatch(document, DELETE, batches);
        }

        return dispatch(batches, forceFlush);
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
    public Map<String, Optional<ListenableFuture<List<BulkResponse>>>> delete(final Object document)
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
    public Map<String, Optional<ListenableFuture<List<BulkResponse>>>> delete(
            final Object document,
            final boolean forceFlush) throws RejectedExecutionException, IOException {

        return dispatch(makeBatch(document, DELETE, null), forceFlush);
    }

    public Map<String, BulkIndexer> getBulkIndexers() {
        return bulkIndexers;
    }

    public BulkOperationFactory getBulkOperationFactory() {
        return bulkOperationFactory;
    }

    /**
     * Adds a list of documents to the Elastic index without immediate flush.
     *
     * @param documents
     *            list of documents to index
     * @return an optional Future for retrieving bulk responses associated with this request.
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Map<String, Optional<ListenableFuture<List<BulkResponse>>>> index(final Collection<?> documents)
            throws RejectedExecutionException, IOException {
        return index(documents, false);
    }

    /**
     * Adds a list of documents to the Elastic index with optional flush.
     *
     * @param documents
     *            list of documents to index
     * @param forceFlush
     *            true to flush indexer after adding documents
     * @return an optional Future for retrieving bulk responses associated with this request.
     * @throws RejectedExecutionException
     *             if indexer is closed or background queue is full
     * @throws IOException
     *             if document could not be serialized
     */
    public Map<String, Optional<ListenableFuture<List<BulkResponse>>>> index(
            final Collection<?> documents,
            final boolean forceFlush) throws RejectedExecutionException, IOException {
        if (documents == null || documents.size() == 0) {
            return ImmutableMap.of();
        }

        Multimap<String, BulkOperation> batches = null;
        for (final Object document : documents) {
            batches = makeBatch(document, INDEX, batches);
        }

        return dispatch(batches, forceFlush);
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
    public Map<String, Optional<ListenableFuture<List<BulkResponse>>>> index(final Object document)
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
    public Map<String, Optional<ListenableFuture<List<BulkResponse>>>> index(
            final Object document,
            final boolean forceFlush) throws RejectedExecutionException, IOException {

        return dispatch(makeBatch(document, INDEX, null), forceFlush);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .omitNullValues() //
                .toString();
    }

    private Map<String, Optional<ListenableFuture<List<BulkResponse>>>> dispatch(
            final Multimap<String, BulkOperation> batches,
            final boolean forceFlush) {
        final ImmutableMap.Builder<String, Optional<ListenableFuture<List<BulkResponse>>>> map = ImmutableMap
                .builder();
        for (final String indexerName : batches.keySet()) {
            final BulkIndexer bulkIndexer = bulkIndexers.get(indexerName);
            final Collection<BulkOperation> batch = batches.get(indexerName);
            map.put(indexerName, bulkIndexer.add(ImmutableList.copyOf(batch), forceFlush));
        }
        return map.build();
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
    private Multimap<String, BulkOperation> makeBatch(
            final Object document,
            final Action action,
            Multimap<String, BulkOperation> batches) throws RejectedExecutionException, IOException {
        if (document == null) {
            return batches;
        }

        // a document may be indexed to multiple places
        if (!bulkOperationFactory.supports(document)) {
            throw new IOException("Unsupported document: " + document);
        }

        if (batches == null) {
            batches = LinkedListMultimap.create();
        }

        final List<BulkOperation> ops = bulkOperationFactory.createBulkOperations(document, action);
        for (final BulkOperation op : ops) {
            final String indexerName = indexToIndexer.apply(op.getIndex().getName());
            batches.put(indexerName, op);
        }
        return batches;
    }
}
