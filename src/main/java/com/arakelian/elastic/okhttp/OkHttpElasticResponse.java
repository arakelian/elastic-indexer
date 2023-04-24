package com.arakelian.elastic.okhttp;

import java.io.IOException;

import com.arakelian.elastic.ElasticResponse;

import retrofit2.Response;

public class OkHttpElasticResponse implements ElasticResponse {
    private final Response response;

    public OkHttpElasticResponse(Response response) {
        this.response = response;
    }

    @Override
    public int code() {
        return response.code();
    }

    @Override
    public String message() {
        return response.message();
    }

    @Override
    public String errorBody() throws IOException {
        return response.errorBody().string();
    }
}
