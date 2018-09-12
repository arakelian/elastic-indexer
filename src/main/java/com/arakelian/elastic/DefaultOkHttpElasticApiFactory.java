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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DefaultOkHttpElasticApiFactory implements OkHttpElasticApiFactory {
    private final OkHttpClient client;

    public final static class EnumConverterFactory extends Converter.Factory {
        private static final class EnumConverter implements Converter<Enum, String> {
            static final EnumConverter INSTANCE = new EnumConverter();

            @Override
            public String convert(Enum value) {
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
        public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            if (type instanceof Class && ((Class<?>) type).isEnum()) {
                return EnumConverter.INSTANCE;
            }
            return null;
        }
    }

    public DefaultOkHttpElasticApiFactory(final OkHttpClient client) {
        this.client = Preconditions.checkNotNull(client);
    }

    @Override
    public OkHttpElasticApi create(final String elasticUrl, final ObjectMapper mapper) {
        final Retrofit retrofit = new Retrofit.Builder() //
                .client(client) //
                .baseUrl(elasticUrl) //
                .addConverterFactory(EnumConverterFactory.create()) //
                .addConverterFactory(ScalarsConverterFactory.create()) //
                .addConverterFactory(JacksonConverterFactory.create(mapper)) //
                .build();
        final OkHttpElasticApi api = retrofit.create(OkHttpElasticApi.class);
        return api;
    }
}
