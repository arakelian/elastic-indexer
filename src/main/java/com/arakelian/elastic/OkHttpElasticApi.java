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

import com.arakelian.elastic.model.About;
import com.arakelian.elastic.model.BulkResponse;
import com.arakelian.elastic.model.ClusterHealth;
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
import com.arakelian.elastic.model.enums.SearchType;
import com.arakelian.elastic.model.search.SearchResponse;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OkHttpElasticApi {
    /** JSON media type **/
    public static final MediaType JSON = MediaType.parse("application/json; charset=UTF-8");

    @GET("/")
    Call<About> about();

    @POST("/_bulk")
    @Headers("Content-Type: application/x-ndjson")
    Call<BulkResponse> bulk(@Body String operations, @Query("pretty") Boolean pretty);

    @GET("/_cluster/health")
    Call<ClusterHealth> clusterHealth();

    @GET("/_cluster/health")
    Call<ClusterHealth> clusterHealth(
            @Query("wait_for_status") ClusterHealth.Status waitForStatus,
            @Query("timeout") String timeout);

    @GET("/_cluster/health/{names}")
    Call<ClusterHealth> clusterHealthForIndex(
            @Path("names") String names,
            @Query("wait_for_status") ClusterHealth.Status waitForStatus,
            @Query("timeout") String timeout);

    /**
     * Create an index in Elastic.
     *
     * Elastic will return a response acknowledging the request, however it make take a little time
     * for the index to actually be deleted.
     *
     * @param name
     *            index name
     * @param index
     *            index configuration
     * @return an OkHttp3 call
     */
    @PUT("/{name}")
    Call<IndexCreated> createIndex(@Path("name") String name, @Body Index index);

    /**
     * Delete all indexes from Elastic.
     *
     * Elastic will return a response acknowledging the request, however it make take a little time
     * for the indexes to actually be deleted.
     *
     * @return response from Elastic acknowledging the request
     */
    @DELETE("/*")
    Call<IndexDeleted> deleteAllIndexes();

    @DELETE("/{name}/{type}/{id}")
    Call<DeletedDocument> deleteDocument(
            @Path("name") String name,
            @Path("type") String type,
            @Path("id") String id);

    @DELETE("/{name}/{type}/{id}?version_type=external")
    Call<DeletedDocument> deleteDocument(
            @Path("name") String name,
            @Path("type") String type,
            @Path("id") String id,
            @Query("version") long epochMillisUtc);

    /**
     * Deletes a comma separated list of index names from Elastic.
     *
     * Elastic will return a response acknowledging the request, however it make take a little time
     * for the indexes to actually be deleted.
     *
     * @param names
     *            comma separated list of index names
     * @return response from Elastic acknowledging the request
     */
    @DELETE("/{names}")
    Call<IndexDeleted> deleteIndex(@Path("names") String names);

    @GET("/{name}/{type}/{id}")
    Call<Document> getDocument(
            @Path("name") String name,
            @Path("type") String type,
            @Path("id") String id,
            @Query("_source") String sourceFields);

    @POST("/_mget")
    Call<Documents> getDocuments(@Body Mget mget);

    /**
     * Indexes a document using default versioning scheme, which simply increments the document
     * version number.
     *
     * @param name
     *            index name
     * @param type
     *            mapping type
     * @param id
     *            document id
     * @param document
     *            document that is being indexed
     * @return response from Elastic indicating that document was indexed
     *
     * @see <a href=
     *      "https://www.elastic.co/blog/elasticsearch-versioning-support">https://www.elastic.co/blog/elasticsearch-versioning-support</a>
     */
    @PUT("/{name}/{type}/{id}")
    @Headers("Content-Type: application/json; charset=UTF-8")
    Call<IndexedDocument> indexDocument(
            @Path("name") String name,
            @Path("type") String type,
            @Path("id") String id,
            @Body String document);

    /**
     * Indexes a document using an external versioning scheme, based upon milliseconds since epoch
     * (UTC timezone).
     *
     * With version_type set to external, Elasticsearch will store the version number as given and
     * will not increment it. Also, instead of checking for an exact match, Elasticsearch will only
     * return a version collision error if the version currently stored is greater or equal to the
     * one in the indexing command.
     *
     * IMPORTANT: When using external versioning, make sure you always add the current version to
     * any index, update or delete calls. If you forget, Elasticsearch will use it's internal system
     * to process that request, which will cause the version to be incremented erroneously.
     *
     * @param name
     *            index name
     * @param type
     *            mapping type
     * @param id
     *            document id
     * @param document
     *            document that is being indexed
     * @param epochMillisUtc
     *            Number of milliseconds since epoch (normalized to UTC time)
     * @return response from Elastic indicating that document was indexed
     *
     * @see <a href=
     *      "https://www.elastic.co/blog/elasticsearch-versioning-support">https://www.elastic.co/blog/elasticsearch-versioning-support</a>
     */
    @PUT("/{name}/{type}/{id}?version_type=external")
    @Headers("Content-Type: application/json; charset=UTF-8")
    Call<IndexedDocument> indexDocument(
            @Path("name") String name,
            @Path("type") String type,
            @Path("id") String id,
            @Body String document,
            @Query("version") long epochMillisUtc);

    /**
     * Checks to see if specified index exists
     *
     * Elastic will return a success code (HTTP 200) if the index exists, otherwise it returns an
     * HTTP 404 (not found).
     *
     * @param name
     *            index name
     * @return HTTP 200 if index exists
     */
    @HEAD("/{name}")
    Call<Void> indexExists(@Path("name") String name);

    @GET("/_nodes/http")
    Call<Nodes> nodes();

    @POST("/_refresh")
    Call<Refresh> refreshAllIndexes();

    @POST("/{names}/_refresh")
    Call<Refresh> refreshIndex(@Path("names") String names);

    @POST("/{name}/_search")
    @Headers("Content-Type: application/json; charset=UTF-8")
    Call<SearchResponse> search(
            @Path("name") String name,
            @Query("preference") String preference,
            @Query("scroll") String scroll,
            @Query("search_type") SearchType searchType,
            @Query("request_cache") Boolean requestCache,
            @Body String query);
}
