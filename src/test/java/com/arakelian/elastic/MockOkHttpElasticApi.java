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

import java.util.concurrent.atomic.AtomicInteger;

import com.arakelian.elastic.model.About;
import com.arakelian.elastic.model.BulkResponse;
import com.arakelian.elastic.model.ClusterHealth;
import com.arakelian.elastic.model.ClusterHealth.Status;
import com.arakelian.elastic.model.DeletedDocument;
import com.arakelian.elastic.model.Document;
import com.arakelian.elastic.model.Documents;
import com.arakelian.elastic.model.ImmutableAbout;
import com.arakelian.elastic.model.ImmutableVersion;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.IndexCreated;
import com.arakelian.elastic.model.IndexDeleted;
import com.arakelian.elastic.model.IndexedDocument;
import com.arakelian.elastic.model.Mget;
import com.arakelian.elastic.model.Nodes;
import com.arakelian.elastic.model.Refresh;
import com.arakelian.elastic.model.search.SearchResponse;
import com.arakelian.elastic.model.search.SearchType;

import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.Calls;

public class MockOkHttpElasticApi implements OkHttpElasticApi {
    protected final BehaviorDelegate<OkHttpElasticApi> delegate;

    public final AtomicInteger refreshCount = new AtomicInteger();

    public MockOkHttpElasticApi(final BehaviorDelegate<OkHttpElasticApi> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<About> about() {
        final About about = ImmutableAbout.builder() //
                .name("name") //
                .tagline("You Know, for Search") //
                .clusterName("cluster_name") //
                .clusterUuid("sVkTY1B_SqS5bVBNJ6TFpQ") //
                .version(
                        ImmutableVersion.builder() //
                                .number("6.0.1") //
                                .buildHash("601be4a") //
                                .buildDate("2017-12-04T09:29:09.525Z") //
                                .buildSnapshot(false) //
                                .luceneVersion("7.0.1") //
                                .minimumWireCompatibilityVersion("5.6.0") //
                                .minimumIndexCompatibilityVersion("5.0.0") //
                                .build())
                .build();
        return delegate.returning(Calls.response(about)).about();
    }

    @Override
    public Call<BulkResponse> bulk(final String operations, final Boolean pretty) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<ClusterHealth> clusterHealth() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<ClusterHealth> clusterHealth(final Status waitForStatus, final String timeout) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<ClusterHealth> clusterHealthForIndex(
            final String names,
            final Status waitForStatus,
            final String timeout) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<IndexCreated> createIndex(final String name, final Index index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<IndexDeleted> deleteAllIndexes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<DeletedDocument> deleteDocument(final String name, final String type, final String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<DeletedDocument> deleteDocument(
            final String name,
            final String type,
            final String id,
            final long epochMillisUtc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<IndexDeleted> deleteIndex(final String names) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<Document> getDocument(
            final String name,
            final String type,
            final String id,
            final String sourceFields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<Documents> getDocuments(final Mget mget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<IndexedDocument> indexDocument(
            final String name,
            final String type,
            final String id,
            final String document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<IndexedDocument> indexDocument(
            final String name,
            final String type,
            final String id,
            final String document,
            final long epochMillisUtc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<Void> indexExists(final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<Nodes> nodes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<Refresh> refreshAllIndexes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<Refresh> refreshIndex(final String names) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<SearchResponse> search(
            final String name,
            final String preference,
            final String scroll,
            final SearchType searchType,
            final Boolean requestCache,
            final String query) {
        throw new UnsupportedOperationException();
    }
}
