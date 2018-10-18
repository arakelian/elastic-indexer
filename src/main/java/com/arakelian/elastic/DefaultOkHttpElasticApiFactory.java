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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DefaultOkHttpElasticApiFactory implements OkHttpElasticApiFactory {
    public final static class CharSequenceConverterFactory extends Converter.Factory {
        private static final class CharSequenceConverter implements Converter<CharSequence, RequestBody> {
            static final CharSequenceConverter INSTANCE = new CharSequenceConverter();

            @Override
            public RequestBody convert(final CharSequence value) {
                return new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(final BufferedSink sink) throws IOException {
                        if (value == null) {
                            return;
                        }

                        try (OutputStreamWriter writer = new OutputStreamWriter(new OutputStream() {
                            @Override
                            public void write(int b) throws IOException {
                                sink.writeByte(b);
                            }
                        }, Charsets.UTF_8)) {
                            for (int i = 0; i < value.length(); i++) {
                                final char ch = value.charAt(i);
                                writer.write(ch);
                            }
                        }
                    }
                };
            }
        }

        public static CharSequenceConverterFactory create() {
            return new CharSequenceConverterFactory();
        }

        private CharSequenceConverterFactory() {
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(
                final Type type,
                final Annotation[] parameterAnnotations,
                final Annotation[] methodAnnotations,
                final Retrofit retrofit) {
            if (type instanceof Class && (Class<?>) type == CharSequence.class) {
                return CharSequenceConverter.INSTANCE;
            }
            return null;
        }
    }

    public final static class EnumConverterFactory extends Converter.Factory {
        private static final class EnumConverter implements Converter<Enum, String> {
            static final EnumConverter INSTANCE = new EnumConverter();

            @Override
            public String convert(final Enum value) {
                // Elastic often only recognizes lowercase enum values, e.g. query_then_fetch.
                return value.name().toLowerCase();
            }
        }

        public static EnumConverterFactory create() {
            return new EnumConverterFactory();
        }

        private EnumConverterFactory() {
        }

        @Override
        public Converter<?, String> stringConverter(
                final Type type,
                final Annotation[] annotations,
                final Retrofit retrofit) {
            if (type instanceof Class && ((Class<?>) type).isEnum()) {
                return EnumConverter.INSTANCE;
            }
            return null;
        }
    }

    private final OkHttpClient client;

    public DefaultOkHttpElasticApiFactory(final OkHttpClient client) {
        this.client = Preconditions.checkNotNull(client);
    }

    @Override
    public OkHttpElasticApi create(final String elasticUrl, final ObjectMapper mapper) {
        final Retrofit retrofit = new Retrofit.Builder() //
                .client(client) //
                .baseUrl(elasticUrl) //
                .addConverterFactory(EnumConverterFactory.create()) //
                .addConverterFactory(CharSequenceConverterFactory.create()) //
                .addConverterFactory(ScalarsConverterFactory.create()) //
                .addConverterFactory(JacksonConverterFactory.create(mapper)) //
                .build();
        final OkHttpElasticApi api = retrofit.create(OkHttpElasticApi.class);
        return api;
    }
}
