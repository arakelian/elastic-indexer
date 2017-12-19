package com.arakelian.elastic;

import java.util.concurrent.Callable;

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
import com.arakelian.elastic.model.Refresh;
import com.google.common.base.Preconditions;

import retrofit2.Call;
import retrofit2.Response;

public class OkHttpElasticClient implements ElasticClient {
    protected final About about;
    protected final OkHttpElasticApi api;

    public OkHttpElasticClient(final OkHttpElasticApi api, final About about) {
        this.api = Preconditions.checkNotNull(api);
        this.about = about;
    }

    @Override
    public Response<About> about() throws ElasticException {
        return executeWithRetry(() -> {
            return api.about();
        });
    }

    @Override
    public Response<BulkResponse> bulk(final String operations, final Boolean pretty)
            throws ElasticException {
        return executeWithRetry(() -> {
            return api.bulk(operations, pretty);
        });
    }

    @Override
    public Response<ClusterHealth> clusterHealth() throws ElasticException {
        return executeWithRetry(() -> {
            return api.clusterHealth();
        });
    }

    @Override
    public Response<ClusterHealth> clusterHealth(final Status waitForStatus, final String timeout)
            throws ElasticException {
        return executeWithRetry(() -> {
            return api.clusterHealth(waitForStatus, timeout);
        });
    }

    @Override
    public Response<ClusterHealth> clusterHealthForIndex(
            final String names,
            final Status waitForStatus,
            final String timeout) throws ElasticException {
        return executeWithRetry(() -> {
            return api.clusterHealthForIndex(names, waitForStatus, timeout);
        });
    }

    @Override
    public Response<IndexCreated> createIndex(final String name, final Index index) throws ElasticException {
        return executeWithRetry(() -> {
            return api.createIndex(name, index);
        });
    }

    @Override
    public Response<IndexDeleted> deleteAllIndexes() throws ElasticException {
        return executeWithRetry(() -> {
            return api.deleteAllIndexes();
        });
    }

    @Override
    public Response<DeletedDocument> deleteDocument(final String name, final String type, final String id)
            throws ElasticException {
        return executeWithRetry(() -> {
            return api.deleteDocument(name, type, id);
        });
    }

    @Override
    public Response<DeletedDocument> deleteDocument(
            final String name,
            final String type,
            final String id,
            final long epochMillisUtc) throws ElasticException {
        return executeWithRetry(() -> {
            return api.deleteDocument(name, type, id, epochMillisUtc);
        });
    }

    @Override
    public Response<IndexDeleted> deleteIndex(final String names) throws ElasticException {
        return executeWithRetry(() -> {
            return api.deleteIndex(names);
        });
    }

    protected <T> Response<T> executeWithRetry(final Callable<Call<T>> callable) throws ElasticException {
        try {
            final Call<T> call = callable.call();
            final Response<T> response = call.execute();
            return response;
        } catch (final Exception e) {
            throw new ElasticException(e.getMessage(), e);
        }
    }

    @Override
    public Response<Document> getDocument(
            final String name,
            final String type,
            final String id,
            final String sourceFields) throws ElasticException {
        return executeWithRetry(() -> {
            return api.getDocument(name, type, id, sourceFields);
        });
    }

    @Override
    public Response<Documents> getDocuments(final Mget mget) throws ElasticException {
        return executeWithRetry(() -> {
            return api.getDocuments(mget);
        });
    }

    @Override
    public Response<IndexedDocument> indexDocument(
            final String name,
            final String type,
            final String id,
            final String document) throws ElasticException {
        return executeWithRetry(() -> {
            return api.indexDocument(name, type, id, document);
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
            return api.indexDocument(name, type, id, document, epochMillisUtc);
        });
    }

    @Override
    public Response<Void> indexExists(final String name) throws ElasticException {
        return executeWithRetry(() -> {
            return api.indexExists(name);
        });
    }

    @Override
    public Response<Refresh> refreshAllIndexes() throws ElasticException {
        return executeWithRetry(() -> {
            return api.refreshAllIndexes();
        });
    }

    @Override
    public Response<Refresh> refreshIndex(final String names) throws ElasticException {
        return executeWithRetry(() -> {
            return api.refreshIndex(names);
        });
    }
}
