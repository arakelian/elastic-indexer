package com.arakelian.elastic;

import java.io.IOException;

public interface ElasticResponse {
    public int code();

    public String message();

    public String errorBody() throws IOException;
}
