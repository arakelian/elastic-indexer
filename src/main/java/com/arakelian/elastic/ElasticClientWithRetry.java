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

package com.arakelian.elastic;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.arakelian.elastic.model.About;
import com.arakelian.elastic.model.BulkResponse;
import com.arakelian.elastic.model.ClusterHealth;
import com.arakelian.elastic.model.ClusterHealth.Status;
import com.arakelian.elastic.model.DeletedDocument;
import com.arakelian.elastic.model.Document;
import com.arakelian.elastic.model.Documents;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.IndexCreated;
import com.arakelian.elastic.model.IndexDeleted;
import com.arakelian.elastic.model.IndexedDocument;
import com.arakelian.elastic.model.Mget;
import com.arakelian.elastic.model.Nodes;
import com.arakelian.elastic.model.Refresh;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.google.common.base.Preconditions;

import retrofit2.Response;

public class ElasticClientWithRetry implements ElasticClient {
    private final ElasticClient delegate;
    private final Retryer<Response<?>> retryer;

    public ElasticClientWithRetry(final ElasticClient delegate) {
        this(delegate, ElasticClientUtils.createElasticRetryer2());
    }

    public ElasticClientWithRetry(final ElasticClient delegate, final Retryer<Response<?>> retryer) {
        this.delegate = Preconditions.checkNotNull(delegate);
        this.retryer = Preconditions.checkNotNull(retryer);
    }

    @Override
    public Response<About> about() throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.about();
        });
    }

    @Override
    public Response<BulkResponse> bulk(final String operations, final Boolean pretty)
            throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.bulk(operations, pretty);
        });
    }

    @Override
    public Response<ClusterHealth> clusterHealth() throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.clusterHealth();
        });
    }

    @Override
    public Response<ClusterHealth> clusterHealth(final Status waitForStatus, final String timeout)
            throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.clusterHealth(waitForStatus, timeout);
        });
    }

    @Override
    public Response<ClusterHealth> clusterHealthForIndex(
            final String names,
            final Status waitForStatus,
            final String timeout) throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.clusterHealthForIndex(names, waitForStatus, timeout);
        });
    }

    @Override
    public Response<IndexCreated> createIndex(final String name, final Index index) throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.createIndex(name, index);
        });
    }

    @Override
    public Response<IndexDeleted> deleteAllIndexes() throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.deleteAllIndexes();
        });
    }

    @Override
    public Response<DeletedDocument> deleteDocument(final String name, final String type, final String id)
            throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.deleteDocument(name, type, id);
        });
    }

    @Override
    public Response<DeletedDocument> deleteDocument(
            final String name,
            final String type,
            final String id,
            final long epochMillisUtc) throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.deleteDocument(name, type, id, epochMillisUtc);
        });
    }

    @Override
    public Response<IndexDeleted> deleteIndex(final String names) throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.deleteIndex(names);
        });
    }

    protected <T> Response<T> executeWithRetry(final Callable<Response<T>> callable) throws ElasticException {
        try {
            @SuppressWarnings("unchecked")
            final Response<T> response = (Response<T>) retryer.call(() -> {
                return callable.call();
            });
            return response;
        } catch (final ExecutionException e) {
            throw new ElasticException("Unable to index " + this, e.getCause());
        } catch (final RetryException e) {
            throw new ElasticException("Unable to index " + this, e);
        }
    }

    @Override
    public Response<Document> getDocument(
            final String name,
            final String type,
            final String id,
            final String sourceFields) throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.getDocument(name, type, id, sourceFields);
        });
    }

    @Override
    public Response<Documents> getDocuments(final Mget mget) throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.getDocuments(mget);
        });
    }

    @Override
    public Response<IndexedDocument> indexDocument(
            final String name,
            final String type,
            final String id,
            final String document) throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.indexDocument(name, type, id, document);
        });
    }

    @Override
    public Response<IndexedDocument> indexDocument(
            final String name,
            final String type,
            final String id,
            final String document,
            final long epochMillisUtc) throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.indexDocument(name, type, id, document, epochMillisUtc);
        });
    }

    @Override
    public Response<Void> indexExists(final String name) throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.indexExists(name);
        });
    }

    @Override
    public Response<Nodes> nodes() throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.nodes();
        });
    }

    @Override
    public Response<Refresh> refreshAllIndexes() throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.refreshAllIndexes();
        });
    }

    @Override
    public Response<Refresh> refreshIndex(final String names) throws ElasticException {
        return executeWithRetry(() -> {
            return delegate.refreshIndex(names);
        });
    }
}
