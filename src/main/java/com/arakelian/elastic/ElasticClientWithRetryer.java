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

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import com.arakelian.elastic.api.About;
import com.arakelian.elastic.api.BulkResponse;
import com.arakelian.elastic.api.ClusterHealth;
import com.arakelian.elastic.api.ClusterHealth.Status;
import com.arakelian.elastic.api.DeletedDocument;
import com.arakelian.elastic.api.Document;
import com.arakelian.elastic.api.Documents;
import com.arakelian.elastic.api.Index;
import com.arakelian.elastic.api.IndexCreated;
import com.arakelian.elastic.api.IndexDeleted;
import com.arakelian.elastic.api.IndexedDocument;
import com.arakelian.elastic.api.Mget;
import com.arakelian.elastic.api.Refresh;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;

import jersey.repackaged.com.google.common.base.Preconditions;
import retrofit2.Response;

public class ElasticClientWithRetryer {
    private final ElasticClient delegate;
    private final Retryer<Response<?>> retryer;

    public ElasticClientWithRetryer(final ElasticClient delegate) {
        this(delegate, ElasticClientUtils.createElasticRetryer2());
    }

    public ElasticClientWithRetryer(ElasticClient delegate, Retryer<Response<?>> retryer) {
        this.delegate = Preconditions.checkNotNull(delegate);
        this.retryer = Preconditions.checkNotNull(retryer);
    }

    public Response<About> about() throws IOException {
        return call(() -> {
            return delegate.about().execute();
        });
    }

    public Response<BulkResponse> bulk(final String operations, final Boolean pretty) throws IOException {
        return call(() -> {
            return delegate.bulk(operations, pretty).execute();
        });
    }

    protected <T> Response<T> call(final Callable<Response<T>> callable) throws IOException {
        try {
            @SuppressWarnings("unchecked")
            final Response<T> response = (Response<T>) retryer.call(() -> {
                return callable.call();
            });
            return response;
        } catch (final ExecutionException e) {
            throw new IOException("Unable to index " + this, e.getCause());
        } catch (final RetryException e) {
            throw new IOException("Unable to index " + this, e);
        }
    }

    public Response<ClusterHealth> clusterHealth() throws IOException {
        return call(() -> {
            return delegate.clusterHealth().execute();
        });
    }

    public Response<ClusterHealth> clusterHealth(final Status waitForStatus, final String timeout)
            throws IOException {
        return call(() -> {
            return delegate.clusterHealth(waitForStatus, timeout).execute();
        });
    }

    public Response<ClusterHealth> clusterHealthForIndex(
            final String names,
            final Status waitForStatus,
            final String timeout) throws IOException {
        return call(() -> {
            return delegate.clusterHealthForIndex(names, waitForStatus, timeout).execute();
        });
    }

    public Response<IndexCreated> createIndex(final String name, final Index index) throws IOException {
        return call(() -> {
            return delegate.createIndex(name, index).execute();
        });
    }

    public Response<IndexDeleted> deleteAllIndexes() throws IOException {
        return call(() -> {
            return delegate.deleteAllIndexes().execute();
        });
    }

    public Response<DeletedDocument> deleteDocument(final String name, final String type, final String id)
            throws IOException {
        return call(() -> {
            return delegate.deleteDocument(name, type, id).execute();
        });
    }

    public Response<DeletedDocument> deleteDocument(
            final String name,
            final String type,
            final String id,
            final long epochMillisUtc) throws IOException {
        return call(() -> {
            return delegate.deleteDocument(name, type, id, epochMillisUtc).execute();
        });
    }

    public Response<IndexDeleted> deleteIndex(final String names) throws IOException {
        return call(() -> {
            return delegate.deleteIndex(names).execute();
        });
    }

    public Response<Document> getDocument(
            final String name,
            final String type,
            final String id,
            final String sourceFields) throws IOException {
        return call(() -> {
            return delegate.getDocument(name, type, id, sourceFields).execute();
        });
    }

    public Response<Documents> getDocuments(final Mget mget) throws IOException {
        return call(() -> {
            return delegate.getDocuments(mget).execute();
        });
    }

    public Response<IndexedDocument> indexDocument(
            final String name,
            final String type,
            final String id,
            final String document) throws IOException {
        return call(() -> {
            return delegate.indexDocument(name, type, id, document).execute();
        });
    }

    public Response<IndexedDocument> indexDocument(
            final String name,
            final String type,
            final String id,
            final String document,
            final long epochMillisUtc) throws IOException {
        return call(() -> {
            return delegate.indexDocument(name, type, id, document, epochMillisUtc).execute();
        });
    }

    public Response<Void> indexExists(final String name) throws IOException {
        return call(() -> {
            return delegate.indexExists(name).execute();
        });
    }

    public Response<Refresh> refreshAllIndexes() throws IOException {
        return call(() -> {
            return delegate.refreshAllIndexes().execute();
        });
    }

    public Response<Refresh> refreshIndex(final String names) throws IOException {
        return call(() -> {
            return delegate.refreshIndex(names).execute();
        });
    }
}
