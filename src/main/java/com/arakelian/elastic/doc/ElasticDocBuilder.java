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

package com.arakelian.elastic.doc;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

import com.arakelian.elastic.doc.filters.TokenChain;
import com.arakelian.elastic.doc.filters.TokenFilter;
import com.arakelian.elastic.doc.plugin.ElasticDocBuilderPlugin;
import com.arakelian.elastic.model.ElasticDocConfig;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.JsonSelector;
import com.arakelian.json.JsonFilter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

/**
 * Builds an Elasticsearch document. Elasticsearch documents are simple (flat) maps that have string
 * keys and either string or List&lt;String&gt; values.
 *
 * This class is not thread-safe.
 */
public class ElasticDocBuilder {
    private final class ElasticDocImpl implements ElasticDoc {
        @Override
        public Collection<Object> get(final String field) {
            Preconditions.checkArgument(
                    config.getMapping().hasField(field),
                    "Field \"%s\" is not part of mapping",
                    field);
            return document.get(field);
        }

        @Override
        public ElasticDocConfig getConfig() {
            return config;
        }

        @Override
        public Map<String, Object> getDocumentAsMap() {
            return ElasticDocBuilder.this.getDocumentAsMap();
        }

        @Override
        public Set<String> getFields() {
            return document.keySet();
        }

        @Override
        public boolean hasField(final String name) {
            return config.getMapping().hasField(name);
        }

        @Override
        public void put(final Field field, final Object value) {
            Preconditions.checkArgument(field != null, "field must be non-null");
            Preconditions.checkArgument(
                    config.getMapping().hasField(field),
                    "Field \"%s\" is not part of mapping",
                    field.getName());
            ElasticDocBuilder.this.put(field, value);
        }

        @Override
        public String writeDocumentAsJson() {
            return ElasticDocBuilder.this.writeDocumentAsJson(false);
        }
    }

    /** Elastic document configuration **/
    protected final ElasticDocConfig config;

    /** The Elastic document we're building **/
    protected final Multimap<String, Object> document;

    /** Token filters **/
    private final Map<Field, TokenFilter> tokenFilters;

    /** Can only build one document at a time **/
    private final Lock lock;

    /** Object writer for document serialization **/
    protected final ObjectMapper mapper;

    /** JsonPath configuration **/
    private final Configuration jsonPathConfig;

    public ElasticDocBuilder(final ElasticDocConfig config) {
        this.lock = new ReentrantLock();
        this.config = Preconditions.checkNotNull(config);
        this.document = LinkedHashMultimap.create();
        this.tokenFilters = Maps.newLinkedHashMap();
        this.mapper = config.getObjectMapper();
        this.jsonPathConfig = Configuration.builder() //
                .jsonProvider(new JacksonJsonNodeJsonProvider(mapper)) //
                .mappingProvider(new JacksonMappingProvider(mapper)) //
                .build();
    }

    public String build(final JsonNode root) throws ElasticDocException {
        lock.lock();
        try {
            final ElasticDocImpl doc = new ElasticDocImpl();
            final List<ElasticDocBuilderPlugin> plugins = config.getPlugins();
            try {
                // map document fields to one or more index fields
                for (final JsonSelector sourcePath : config.getSourcePaths()) {
                    final JsonPath jsonPath = sourcePath.getJsonPath();
                    final JsonNode node = jsonPath.read(root, jsonPathConfig);

                    // we've arrived at path! put values into document
                    final Collection<Field> targets = config.getFieldsTargetedBy(sourcePath);
                    for (final Field field : targets) {
                        putNode(field, node);
                    }
                }

                // give plugins a chance to augment document
                for (final ElasticDocBuilderPlugin plugin : plugins) {
                    plugin.completed(doc);
                }

                final String json = writeDocumentAsJson(config.isCompact());
                return json;
            } catch (final IllegalArgumentException | IllegalStateException e) {
                throw new ElasticDocException("Unable to build document", e);
            } finally {
                document.clear();
            }
        } finally {
            lock.unlock();
        }
    }

    public String build(final String json) throws ElasticDocException {
        JsonNode node;
        try {
            Preconditions.checkArgument(json != null, "json must be non-null");
            node = mapper.readTree(json);
        } catch (final IllegalArgumentException | IllegalStateException | IOException e) {
            throw new ElasticDocException("Unable to parse source document", e);
        }

        return build(node);
    }

    /**
     * Returns the Elastic document as a simple map.
     *
     * Field names are will be ordered as they are in the mapping, and values are listed in the
     * order they were added to the document.
     *
     * @return the document as a simple map.
     */
    protected Map<String, Object> getDocumentAsMap() {
        final Map<String, Object> map = Maps.newLinkedHashMap();

        // add fields in the order that they appear in the mapping
        final Set<String> mappingFields = config.getMapping().getProperties().keySet();
        for (final String mappingField : mappingFields) {
            if (document.containsKey(mappingField)) {
                final Collection<Object> values = document.get(mappingField);
                if (values.size() == 1) {
                    map.put(mappingField, values.iterator().next());
                } else {
                    map.put(mappingField, values);
                }
            }
        }

        // add fields that do not appear in mapping
        for (final String field : document.keys()) {
            if (!mappingFields.contains(field)) {
                final Collection<Object> values = document.get(field);
                if (values.size() == 1) {
                    map.put(field, values.iterator().next());
                } else {
                    map.put(field, values);
                }
            }
        }

        return map;
    }

    protected TokenFilter getTokenFilter(final Field field) {
        final TokenFilter filter;
        if (tokenFilters.containsKey(field)) {
            filter = tokenFilters.get(field);
        } else {
            filter = TokenChain.link(field.getTokenFilters());
            tokenFilters.put(field, filter);
        }
        return filter;
    }

    protected void put(final Field field, final Object obj) {
        if (obj == null) {
            // we don't store null values
            return;
        }

        // apply token filters
        if (obj instanceof CharSequence) {
            getTokenFilter(field).accept(obj.toString(), token -> {
                // we only store non-empty strings in document
                if (!StringUtils.isEmpty(token)) {
                    document.put(field.getName(), token);
                }
            });
            return;
        }

        // store object
        document.put(field.getName(), obj);
    }

    /**
     * Adds a field/value pair to an Elasticsearch document.
     *
     * @param field
     *            field
     * @param node
     *            value
     */
    protected void putNode(final Field field, final JsonNode node) {
        // pipeline: deserialize to object -> token filters for textual data
        config.getValueProducer().traverse(field, node, obj -> {
            put(field, obj);
        });
    }

    protected String writeDocumentAsJson(final boolean compact) throws ElasticDocException {
        try {
            // note: we convert document to a "regular" map so that single-value fields are not
            // rendered as arrays; for cosmetic purposes, we also rearrange the map keys to align
            // with the ordering specified in the index mapping.
            final Map<String, Object> map = getDocumentAsMap();

            // return JSON
            final String json = mapper.writeValueAsString(map);
            if (compact) {
                return JsonFilter.compact(json);
            }
            return json;
        } catch (final IOException e) {
            throw new ElasticDocException("Unable to serialize Elastic document", e);
        }
    }
}
