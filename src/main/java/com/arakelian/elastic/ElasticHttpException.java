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
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.arakelian.jackson.utils.JacksonUtils;

import retrofit2.Response;

public class ElasticHttpException extends ElasticException {
    private static String message(final Response response) {
        final int statusCode = response.code();
        final String statusMessage = response.message();

        String error;
        try {
            final Map map = JacksonUtils.getObjectMapper()
                    .readValue(response.errorBody().string(), Map.class);
            error = Objects.toString(map.get("error"), null);
        } catch (final IOException e) {
            // fall through
            error = null;
        }

        final StringBuilder buf = new StringBuilder();
        buf.append("HTTP ");
        buf.append(statusCode);
        if (!StringUtils.isEmpty(buf)) {
            buf.append(" (");
            buf.append(statusMessage);
            buf.append(")");
        }
        if (!StringUtils.isEmpty(error)) {
            buf.append(": ");
            buf.append(error);
            buf.append(")");
        }
        return buf.toString();
    }

    private final Object body;

    private final String errorBody;

    private final int statusCode;

    private final String statusMessage;

    public ElasticHttpException(final Response response) {
        this(response, null);
    }

    public ElasticHttpException(final Response response, final Object body) {
        super(message(response));

        String errorBody;
        try {
            errorBody = response.errorBody().string();
        } catch (final IOException e) {
            errorBody = StringUtils.EMPTY;
        }

        this.body = body;
        this.errorBody = StringUtils.defaultString(errorBody);
        this.statusCode = response.code();
        this.statusMessage = response.message();
    }

    public <T> T getBody(final Class<T> clazz) {
        return clazz.isInstance(body) ? clazz.cast(body) : null;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
