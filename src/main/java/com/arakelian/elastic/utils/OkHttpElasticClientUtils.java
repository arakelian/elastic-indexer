package com.arakelian.elastic.utils;

import com.arakelian.elastic.ElasticClient;
import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.okhttp.DefaultOkHttpElasticApiFactory;
import com.arakelian.elastic.okhttp.OkHttpElasticClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;

public class OkHttpElasticClientUtils {

    public static ElasticClient createElasticClient(
            final String elasticUrl,
            final OkHttpClient client,
            final ObjectMapper objectMapper,
            final VersionComponents version) {
    
        return new OkHttpElasticClient(elasticUrl, new DefaultOkHttpElasticApiFactory(client), objectMapper,
                version);
    }

}
