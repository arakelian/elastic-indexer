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

import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

public class MockElasticClient implements ElasticClient {
    protected final BehaviorDelegate<ElasticClient> delegate;

    public final AtomicInteger refreshCount = new AtomicInteger();

    public MockElasticClient(final BehaviorDelegate<ElasticClient> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Call<About> about() {
        throw new UnsupportedOperationException();
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
    public Call<ClusterHealth> clusterHealthForIndex(final String names, final Status waitForStatus,
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
    public Call<DeletedDocument> deleteDocument(final String name, final String type, final String id,
            final long epochMillisUtc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<IndexDeleted> deleteIndex(final String names) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<Document> getDocument(final String name, final String type, final String id,
            final String sourceFields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<Documents> getDocuments(final Mget mget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<IndexedDocument> indexDocument(final String name, final String type, final String id,
            final String document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<IndexedDocument> indexDocument(final String name, final String type, final String id,
            final String document, final long epochMillisUtc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Call<Void> indexExists(final String name) {
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

}
