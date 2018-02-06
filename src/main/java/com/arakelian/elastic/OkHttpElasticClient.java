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
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

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
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.elastic.model.search.SearchResponse;
import com.arakelian.elastic.search.WriteSearchVisitor;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Preconditions;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OkHttpElasticClient implements ElasticClient {
    private class DelegatingCall<T> implements Call<T> {
        private final Class<T> clazz;
        private final Call<T> delegate;

        public DelegatingCall(final Class<T> clazz, final Call<T> delegate) {
            this.delegate = Preconditions.checkNotNull(delegate);
            this.clazz = Preconditions.checkNotNull(clazz);
        }

        @Override
        public void cancel() {
            delegate.cancel();
        }

        @Override
        public Call<T> clone() {
            return delegate.clone();
        }

        @Override
        public void enqueue(final Callback<T> callback) {
            delegate.enqueue(callback);
        }

        @Override
        public Response<T> execute() throws IOException {
            return delegate.execute();
        }

        public T failure(final Response response) throws ElasticHttpException {
            if (response.code() == 404) {
                try {
                    // retrofit treats 404 like error but they are not as far as Elastic is
                    // concerned; often it will return a body that matches expected type
                    final ResponseBody errorBody = response.errorBody();
                    final T result = toResponse(errorBody != null ? errorBody.string() : null);
                    throw new ElasticNotFoundException(response, result);
                } catch (final IOException e) {
                    throw new ElasticNotFoundException(response);
                }
            }

            throw new ElasticHttpException(response);
        }

        @Override
        public boolean isCanceled() {
            return delegate.isCanceled();
        }

        @Override
        public boolean isExecuted() {
            return delegate.isExecuted();
        }

        @Override
        public Request request() {
            return delegate.request();
        }

        private T toResponse(final String body) throws IOException {
            final T result = !StringUtils.isEmpty(body) ? mapper.readValue(body, clazz) : null;
            return result;
        }
    }

    protected final OkHttpElasticApiFactory elasticApiFactory;
    protected final ObjectMapper mapper;
    protected final String elasticUrl;

    /** Synchronization mechanism for version-specific resources **/
    private final Lock versionLock = new ReentrantLock();

    /** Cache of Retrofit API wrappers **/
    private final LoadingCache<String, OkHttpElasticApi> versionedApi;

    /** Elastic version, if known **/
    private VersionComponents version;

    /** Version-specific <code>ObjectMapper</code> **/
    private ObjectMapper versionMapper;

    public OkHttpElasticClient(
            final String elasticUrl,
            final OkHttpElasticApiFactory elasticApiFactory,
            final ObjectMapper mapper) {
        // version will be determined dynamically
        this(elasticUrl, elasticApiFactory, mapper, null);
    }

    public OkHttpElasticClient(
            final String elasticUrl,
            final OkHttpElasticApiFactory elasticApiFactory,
            final ObjectMapper mapper,
            final VersionComponents version) {
        this.elasticUrl = Preconditions.checkNotNull(elasticUrl);
        this.elasticApiFactory = Preconditions.checkNotNull(elasticApiFactory);
        this.mapper = Preconditions.checkNotNull(mapper);

        // if version is null, it will be computed dynamically
        this.version = version;

        versionedApi = Caffeine.newBuilder() //
                .maximumSize(1_000) //
                .expireAfterWrite(5, TimeUnit.MINUTES) //
                .refreshAfterWrite(1, TimeUnit.MINUTES) //
                .build(url -> elasticApiFactory.create(url, getVersionedObjectMapper()));
    }

    @Override
    public About about() throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(About.class, getApi().about());
        });
    }

    @Override
    public BulkResponse bulk(final String operations, final Boolean pretty) throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(BulkResponse.class, getVersionedApi().bulk(operations, pretty));
        });
    }

    @Override
    public ClusterHealth clusterHealth() throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(ClusterHealth.class, getVersionedApi().clusterHealth());
        });
    }

    @Override
    public ClusterHealth clusterHealth(final Status waitForStatus, final String timeout)
            throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(ClusterHealth.class,
                    getVersionedApi().clusterHealth(waitForStatus, timeout));
        });
    }

    @Override
    public ClusterHealth clusterHealthForIndex(
            final String names,
            final Status waitForStatus,
            final String timeout) throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(ClusterHealth.class,
                    getVersionedApi().clusterHealthForIndex(names, waitForStatus, timeout));
        });
    }

    @Override
    public IndexCreated createIndex(final String name, final Index index) throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(IndexCreated.class, getVersionedApi().createIndex(name, index));
        });
    }

    @Override
    public IndexDeleted deleteAllIndexes() throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(IndexDeleted.class, getVersionedApi().deleteAllIndexes());
        });
    }

    @Override
    public DeletedDocument deleteDocument(final String name, final String type, final String id)
            throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(DeletedDocument.class,
                    getVersionedApi().deleteDocument(name, type, id));
        });
    }

    @Override
    public DeletedDocument deleteDocument(
            final String name,
            final String type,
            final String id,
            final long epochMillisUtc) throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(DeletedDocument.class,
                    getVersionedApi().deleteDocument(name, type, id, epochMillisUtc));
        });
    }

    @Override
    public IndexDeleted deleteIndex(final String names) throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(IndexDeleted.class, getVersionedApi().deleteIndex(names));
        });
    }

    @Override
    public Document getDocument(
            final String name,
            final String type,
            final String id,
            final String sourceFields) throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(Document.class,
                    getVersionedApi().getDocument(name, type, id, sourceFields));
        });
    }

    @Override
    public Documents getDocuments(final Mget mget) throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(Documents.class, getVersionedApi().getDocuments(mget));
        });
    }

    @Override
    public VersionComponents getVersion() {
        versionLock.lock();
        try {
            if (version == null) {
                this.version = about().getVersion().getComponents();
            }
            return version;
        } finally {
            versionLock.unlock();
        }
    }

    @Override
    public IndexedDocument indexDocument(
            final String name,
            final String type,
            final String id,
            final String document) throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(IndexedDocument.class,
                    getVersionedApi().indexDocument(name, type, id, document));
        });
    }

    @Override
    public IndexedDocument indexDocument(
            final String name,
            final String type,
            final String id,
            final String document,
            final long epochMillisUtc) throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(IndexedDocument.class,
                    getVersionedApi().indexDocument(name, type, id, document, epochMillisUtc));
        });
    }

    @Override
    public boolean indexExists(final String name) throws ElasticException {
        try {
            execute(() -> {
                return new DelegatingCall<>(Void.class, getVersionedApi().indexExists(name));
            });
            return true;
        } catch (final ElasticNotFoundException e) {
            // Elastic returns 404 if index not found
            return false;
        }
    }

    @Override
    public Nodes nodes() throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(Nodes.class, getVersionedApi().nodes());
        });
    }

    @Override
    public Refresh refreshAllIndexes() throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(Refresh.class, getVersionedApi().refreshAllIndexes());
        });
    }

    @Override
    public Refresh refreshIndex(final String names) throws ElasticException {
        return execute(() -> {
            return new DelegatingCall<>(Refresh.class, getVersionedApi().refreshIndex(names));
        });
    }

    @Override
    public SearchResponse search(final String name, final Search search) {
        final ObjectMapper mapper = getVersionedObjectMapper();

        final String query = JacksonUtils.toString(writer -> {
            new WriteSearchVisitor(writer, version).writeSearch(search);
        }, mapper, true);

        final SearchResponse response = execute(() -> {
            return new DelegatingCall<>(SearchResponse.class,
                    getVersionedApi().search(
                            name,
                            search.getPreference(),
                            search.getScroll(),
                            search.getSearchType(),
                            search.isRequestCache(),
                            query));
        });

        response.getHits().setObjectMapper(mapper);
        return response;
    }

    private ObjectMapper getVersionedObjectMapper() {
        versionLock.lock();
        try {
            versionMapper = mapper.copy();
            ElasticClientUtils.configure(versionMapper, getVersion());
            ElasticClientUtils.configureIndexSerialization(versionMapper);
            return versionMapper;
        } finally {
            versionLock.unlock();
        }
    }

    protected <T> T execute(final Callable<DelegatingCall<T>> request) throws ElasticException {
        try {
            final DelegatingCall<T> call = request.call();
            final Response<T> response = call.execute();
            if (response.isSuccessful()) {
                final T body = response.body();
                return body;
            }
            return call.failure(response);
        } catch (final ElasticException e) {
            // pass through
            throw e;
        } catch (final Exception e) {
            // wrap exception
            throw new ElasticException(e.getMessage(), e);
        }
    }

    protected OkHttpElasticApi getApi() {
        final String url = nextElasticUrl();
        return elasticApiFactory.create(url, mapper);
    }

    protected OkHttpElasticApi getVersionedApi() {
        try {
            final String url = nextElasticUrl();
            return versionedApi.get(url);
        } catch (final CompletionException e) {
            throw new ElasticException("Unable to fetch API for: " + elasticUrl, e.getCause());
        }
    }

    protected String nextElasticUrl() {
        return elasticUrl;
    }
}
