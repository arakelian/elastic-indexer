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
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.arakelian.elastic.doc.filters.TokenChain;
import com.arakelian.elastic.doc.filters.TokenFilter;
import com.arakelian.elastic.doc.plugin.ElasticDocBuilderPlugin;
import com.arakelian.elastic.model.ElasticDocConfig;
import com.arakelian.elastic.model.Field;
import com.arakelian.json.JsonWriter;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

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
        public Set<String> getFields() {
            return document.keySet();
        }

        @Override
        public boolean hasField(final String name) {
            return config.getMapping().hasField(name);
        }

        @Override
        public void traverse(Object value, Consumer<Object> consumer) {
            ElasticDocBuilder.this.traverse(value, consumer);
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
    }

    /** Optional entity extractor **/
    private final ElasticDocConfig config;

    /** The Elastic document we're building **/
    private final Multimap<String, Object> document;

    /** Scratch buffer **/
    private final StringBuilder buf = new StringBuilder();

    /** Token filters **/
    private final Map<Field, TokenFilter> tokenFilters;

    /** Can only build one document at a time **/
    private final Lock lock;

    /**
     * An instance of {@link JsonWriter} that we can use for producing JSON.
     *
     * Note that Elastic bulk api requires that each document be on a single line so standard JSON
     * formatting is disabled. We also prevent empty fields from being written.
     */
    private final JsonWriter<StringWriter> writer = new JsonWriter<>(new StringWriter()).withPretty(false)
            .withSkipEmpty(true);

    public ElasticDocBuilder(final ElasticDocConfig config) {
        this.lock = new ReentrantLock();
        this.config = Preconditions.checkNotNull(config);
        this.document = LinkedHashMultimap.create();
        this.tokenFilters = Maps.newLinkedHashMap();
    }

    public String build(final JsonNode root) throws ElasticDocBuilderException {
        lock.lock();
        try {
            final ElasticDocImpl doc = new ElasticDocImpl();
            final List<ElasticDocBuilderPlugin> plugins = config.getPlugins();
            try {
                // map document fields to one or more index fields
                for (final JsonPointer sourcePath : config.getSourcePaths()) {
                    final JsonNode node = root.at(sourcePath);

                    // we've arrived at path! put values into document
                    final Collection<Field> targets = config.getFieldsTargetedBy(sourcePath);
                    for (final Field field : targets) {
                        putNode(field, node);
                    }
                }

                // give plugins a chance to augment document
                for (final ElasticDocBuilderPlugin plugin : plugins) {
                    plugin.complete(doc);
                }

                final String json = writeDocument();
                return json;
            } catch (final IllegalArgumentException | IllegalStateException e) {
                throw new ElasticDocBuilderException("Unable to build document", e);
            } finally {
                document.clear();
                buf.setLength(0);
            }
        } finally {
            lock.unlock();
        }
    }

    public String build(final String json) throws ElasticDocBuilderException {
        JsonNode node;
        try {
            Preconditions.checkArgument(json != null, "json must be non-null");
            final ObjectMapper objectMapper = config.getObjectMapper();
            node = objectMapper.readTree(json);
        } catch (final IllegalArgumentException | IllegalStateException | IOException e) {
            throw new ElasticDocBuilderException("Unable to parse source document", e);
        }

        return build(node);
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

    private void traverse(final Object obj, final Consumer<Object> consumer) {
        Preconditions.checkArgument(consumer != null, "consumer must be non-null");
        if (obj == null) {
            return;
        }

        if (obj instanceof CharSequence) {
            consumer.accept(obj);
            return;
        }
        if (obj instanceof Number) {
            consumer.accept(obj);
            return;
        }
        if (obj instanceof Boolean) {
            consumer.accept(obj);
            return;
        }

        if (obj instanceof Map) {
            for (Object v : ((Map) obj).values()) {
                traverse(v, consumer);
            }
            return;
        }

        if (obj instanceof Collection) {
            for (Object v : ((Collection) obj)) {
                traverse(v, consumer);
            }
            return;
        }
        if (obj instanceof Object[]) {
            for (Object v : ((Object[]) obj)) {
                traverse(v, consumer);
            }
            return;
        }

        throw new IllegalStateException(
                "Expecting simple JSON data type, but received: " + obj.getClass().getName());
    }

    private void put(final Field field, final Object obj) {
        if (obj == null) {
            // we don't store null values
            return;
        }

        // serialize object
        final Object ser = config.getValueSerializer().serialize(field, obj);
        if (ser == null) {
            return;
        }

        // apply token filters
        if (ser instanceof CharSequence) {
            getTokenFilter(field).accept(ser.toString(), token -> {
                // we only store non-empty strings in document
                if (!StringUtils.isEmpty(token)) {
                    document.put(field.getName(), token);
                }
            });
            return;
        }

        // make sure object is simple JSON type
        traverse(ser, o -> {
        });
        document.put(field.getName(), obj);
    }

    /**
     * Adds a field/value pair to an Elasticsearch document.
     *
     * If the specified value is an empty string, the field is not added to the Elastic document.
     * Similarly, if the specified value has already been added to the field, it is not added a
     * second time.
     *
     * Note that in an Elasticsearch document a field may have more than value, so this method will
     * automatically build a list if needed.
     *
     * @param fieldName
     *            name of Elasticsearch field (must be part of Elastic index mapping properties)
     * @param value
     *            value
     */
    private void putNode(final Field field, final JsonNode node) {
        // pipeline: deserialize to object -> serialize object to string -> token filters
        config.getValueDeserializer().deserialize(field, node, obj -> {
            put(field, obj);
        });
    }

    private String writeDocument() throws ElasticDocBuilderException {
        try {
            // output fields
            writer.writeStartObject();

            for (final String field : document.keySet()) {
                final Collection<Object> values = document.get(field);
                writeField(writer, field, values);
            }
            writer.writeEndObject();

            final String json = writer.flush().getWriter().toString();

            // sanity check
            for (int i = 0, size = json.length(); i < size; i++) {
                final char ch = json.charAt(i);
                if (ch == '\n') {
                    throw new ElasticDocBuilderException("Elastic document cannot contain newline character");
                }
            }
            return json;
        } catch (final IOException e) {
            // this should not happen because we're writing to a StringWriter
            throw new ElasticDocBuilderException("Unable to create build Elastic document", e);
        } finally {
            writer.getWriter().getBuffer().setLength(0);
            writer.reset();
        }
    }

    private void writeField(
            final JsonWriter<StringWriter> writer,
            final String field,
            final Collection<Object> values) throws IOException {

        final int size = values.size();
        if (size == 1) {
            writer.writeKeyValue(field, values.iterator().next());
        } else if (size > 1) {
            writer.writeKey(field);
            writer.writeStartArray();
            for (final Object o : values) {
                writer.writeObject(o);
            }
            writer.writeEndArray();
        }
    }
}
