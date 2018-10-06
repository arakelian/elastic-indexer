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
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.elastic.model.search.SearchResponse;

/**
 * Elastic API
 *
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/tree/77d2dd203eac1bbf529fb1c5740d54c20ec197d7/rest-api-spec/src/main/resources/rest-api-spec/api">Elastic
 *      Rest API specification</a>
 */
public interface ElasticClient {
    About about() throws ElasticException;

    BulkResponse bulk(CharSequence operations, Boolean pretty) throws ElasticException;

    ClusterHealth clusterHealth() throws ElasticException;

    ClusterHealth clusterHealth(ClusterHealth.Status waitForStatus, String timeout) throws ElasticException;

    ClusterHealth clusterHealthForIndex(String names, ClusterHealth.Status waitForStatus, String timeout)
            throws ElasticException;

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
     * @throws ElasticException
     *             if there an exception making HTTP request
     */
    IndexCreated createIndex(String name, Index index) throws ElasticException;

    /**
     * Delete all indexes from Elastic.
     *
     * Elastic will return a response acknowledging the request, however it make take a little time
     * for the indexes to actually be deleted.
     *
     * @return response from Elastic acknowledging the request
     * @throws ElasticException
     *             if there an exception making HTTP request
     */
    IndexDeleted deleteAllIndexes() throws ElasticException;

    DeletedDocument deleteDocument(String name, String type, String id) throws ElasticException;

    DeletedDocument deleteDocument(String name, String type, String id, long epochMillisUtc)
            throws ElasticException;

    /**
     * Deletes a comma separated list of index names from Elastic.
     *
     * Elastic will return a response acknowledging the request, however it make take a little time
     * for the indexes to actually be deleted.
     *
     * @param names
     *            comma separated list of index names
     * @return response from Elastic acknowledging the request
     * @throws ElasticException
     *             if there an exception making HTTP request
     */
    IndexDeleted deleteIndex(String names) throws ElasticException;

    Document getDocument(String name, String type, String id, String sourceFields) throws ElasticException;

    Documents getDocuments(Mget mget) throws ElasticException;

    VersionComponents getVersion();

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
     * @throws ElasticException
     *             if there an exception making HTTP request
     */
    IndexedDocument indexDocument(String name, String type, String id, CharSequence document)
            throws ElasticException;

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
     * @throws ElasticException
     *             if there an exception making HTTP request
     */
    IndexedDocument indexDocument(String name, String type, String id, CharSequence document, long epochMillisUtc)
            throws ElasticException;

    /**
     * Checks to see if specified index exists
     *
     * Elastic will return a success code (HTTP 200) if the index exists, otherwise it returns an
     * HTTP 404 (not found).
     *
     * @param name
     *            index name
     * @return HTTP 200 if index exists
     * @throws ElasticException
     *             if there an exception making HTTP request
     */
    boolean indexExists(String name) throws ElasticException;

    Nodes nodes() throws ElasticException;

    Refresh refreshAllIndexes() throws ElasticException;

    Refresh refreshIndex(String names) throws ElasticException;

    SearchResponse search(String name, Search search);
}
