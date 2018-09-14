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

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.model.enums.SearchType;
import com.arakelian.elastic.model.search.ImmutableSearch;
import com.arakelian.elastic.model.search.SearchResponse;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.jackson.utils.JacksonUtils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DefaultOkHttpElasticApiFactoryTest {
    @Test
    public void testEnumsAreConvertedToLowercase() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder() //
                .connectTimeout(2, TimeUnit.SECONDS) //
                .addInterceptor(chain -> {
                    final String url = chain.call().request().url().toString();

                    String body = "{}";
                    if (!"http://localhost:8080/index/_search?search_type=query_then_fetch".equals(url)) {
                        // search_type should have been converted to lowercase
                        body = "{\"error\":{\"reason\":\"Not working\"}}";
                    }

                    return new Response.Builder() //
                            .code(200) //
                            .message("OK") //
                            .protocol(Protocol.HTTP_1_1) //
                            .request(chain.request()) //
                            .body(ResponseBody.create(MediaType.parse("application/json"), body)) //
                            .build();
                }) //
                .build();

        final ElasticClient elasticClient = ElasticClientUtils.createElasticClient(
                "http://localhost:8080",
                okHttpClient,
                JacksonUtils.getObjectMapper(),
                VersionComponents.of("6.1"));

        final SearchResponse response = elasticClient.search(
                "index",
                ImmutableSearch.builder() //
                        .searchType(SearchType.QUERY_THEN_FETCH) //
                        .build());
        Assert.assertEquals(false, response.hasError());
    }
}
