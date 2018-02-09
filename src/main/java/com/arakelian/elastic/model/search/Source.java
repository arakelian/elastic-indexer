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

package com.arakelian.elastic.model.search;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.immutables.value.Value;

import com.arakelian.jackson.MapPath;
import com.arakelian.jackson.model.GeoPoint;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

@Value.Immutable(copy=false)
@JsonSerialize(as = ImmutableSource.class)
@JsonDeserialize(builder = ImmutableSource.Builder.class)
public abstract class Source implements Serializable {
    /** ObjectMapper that should be used for deserialization. **/
    @SuppressWarnings("immutables")
    private transient ObjectMapper mapper;

    public <R> R find(final String path, final Function<Object, R> function, final R defaultValue) {
        return getMapPath().find(path, function, defaultValue);
    }

    public <T> T get(final String path, final Class<T> clazz) {
        return getMapPath().get(path, clazz);
    }

    public <T> T get(final String path, final Class<T> clazz, final T defaultValue) {
        return getMapPath().get(path, clazz, defaultValue);
    }

    public Double getDouble(final String path) {
        return getMapPath().getDouble(path);
    }

    public Float getFloat(final String path) {
        return getMapPath().getFloat(path);
    }

    public GeoPoint getGeoPoint(final String path) {
        return getMapPath().getGeoPoint(path);
    }

    public Integer getInt(final String path) {
        return getMapPath().getInt(path);
    }

    public List getList(final String path) {
        return getMapPath().getList(path);
    }

    public Long getLong(final String path) {
        return getMapPath().getLong(path);
    }

    public Map getMap(final String path) {
        return getMapPath().getMap(path);
    }

    public MapPath getMapPath(final String path) {
        return getMapPath().getMapPath(path);
    }

    public Object getObject(final String path) {
        return getMapPath().getObject(path);
    }

    @JsonIgnore
    @Value.Lazy
    public ObjectMapper getObjectMapper() {
        if (mapper == null) {
            mapper = JacksonUtils.getObjectMapper();
        }
        return mapper;
    }

    @JsonAnyGetter
    @Value.Default
    public Map<String, Object> getProperties() {
        return ImmutableMap.of();
    }

    public String getString(final String path) {
        return getMapPath().getString(path);
    }

    public ZonedDateTime getZonedDateTime(final String path) {
        return getMapPath().getZonedDateTime(path);
    }

    public boolean hasProperty(final String path) {
        return getMapPath().hasProperty(path);
    }

    public void setObjectMapper(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @JsonIgnore
    @Value.Lazy
    MapPath getMapPath() {
        return MapPath.of(getProperties(), getObjectMapper());
    }
}
