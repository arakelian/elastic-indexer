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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.input.CharSequenceReader;
import org.apache.commons.lang3.StringUtils;

import com.arakelian.elastic.doc.filters.TokenFilter;
import com.arakelian.elastic.doc.plugins.ElasticDocBuilderPlugin;
import com.arakelian.elastic.model.ElasticDocConfig;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.JsonSelector;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.elastic.utils.JsonNodeUtils;
import com.arakelian.json.JsonFilter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.jayway.jsonpath.Configuration;
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
            return Collections.unmodifiableCollection(document.get(field));
        }

        @Override
        public Set<Object> getAttribute(final String name) {
            return attributes.get(name);
        }

        @Override
        public ElasticDocConfig getConfig() {
            return config;
        }

        @Override
        public Map<String, Object> getDocumentAsMap() {
            // modification should be via 'put'
            return Collections.unmodifiableMap(ElasticDocBuilder.this.getDocumentAsMap());
        }

        @Override
        public Set<String> getFields() {
            // we make a copy so that if client loops over it, and make modifications to document,
            // that we don't get a ConcurrentModificationException
            return ImmutableSet.copyOf(document.keySet());
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
            ElasticDocBuilder.this.put(this, field, value);
        }

        @Override
        public void putAttribute(final String name, final Object value) {
            attributes.put(name, value);
        }

        @Override
        public Set<Object> removeAttribute(final String name) {
            return attributes.removeAll(name);
        }

        @Override
        public CharSequence writeDocumentAsJson() {
            return ElasticDocBuilder.this.writeDocumentAsJson(false);
        }
    }

    /**
     * Used to build a canonical path, with clean separators
     */
    private static final Joiner SPACE_JOINER = Joiner.on(" ").skipNulls();

    /** Elastic document configuration **/
    protected final ElasticDocConfig config;

    /** The Elastic document we're building. Duplicate values are not stored. **/
    protected final LinkedHashMultimap<String, Object> document;

    /** Attributes used by plugins **/
    protected final LinkedHashMultimap<String, Object> attributes;

    /** Can only build one document at a time **/
    private final Lock lock;

    /** Object writer for document serialization **/
    protected final ObjectMapper mapper;

    /** JsonPath configuration **/
    private Configuration jsonPathConfig;

    public ElasticDocBuilder(final ElasticDocConfig config) {
        this.lock = new ReentrantLock();
        this.config = Preconditions.checkNotNull(config);
        this.document = LinkedHashMultimap.create();
        this.attributes = LinkedHashMultimap.create();
        this.mapper = config.getObjectMapper();
    }

    public CharSequence build(final CharSequence json) throws ElasticDocException {
        final JsonNode node = readValue(json);
        return build(node);
    }

    public CharSequence build(final JsonNode root) throws ElasticDocException {
        lock.lock();
        try {
            final ElasticDocImpl doc = new ElasticDocImpl();
            final List<ElasticDocBuilderPlugin> plugins = config.getPlugins();
            try {
                // give plugins a chance to modify raw JSON, or initialize document
                for (final ElasticDocBuilderPlugin plugin : plugins) {
                    plugin.before(root, doc);
                }

                // map document fields to one or more index fields
                for (final JsonSelector sourcePath : config.getSourcePaths()) {
                    final JsonNode node = read(sourcePath, root);

                    // we've arrived at path! put values into document
                    final Collection<Field> targets = config.getFieldsTargetedBy(sourcePath);
                    for (final Field field : targets) {
                        putNode(doc, field, node);
                    }
                }

                // give plugins a chance to augment document
                for (final ElasticDocBuilderPlugin plugin : plugins) {
                    plugin.after(root, doc);
                }

                final CharSequence json = writeDocumentAsJson(config.isCompact());
                return json;
            } catch (final IllegalArgumentException | IllegalStateException e) {
                throw new ElasticDocException("Unable to build document", e);
            } finally {
                document.clear();
                attributes.clear();
            }
        } finally {
            lock.unlock();
        }
    }

    private void buildDocumentMap(final String fieldName, final Map<String, Object> map) {
        final Object values = getFieldValues(fieldName);
        if (values != null) {
            map.put(fieldName, values);
        }
    }

    private JsonNode concat(final JsonSelector selector, final JsonNode node) {
        Preconditions.checkArgument(selector != null, "selector must be non-null");
        Preconditions.checkArgument(node != null, "node must be non-null");

        final JsonNode[] args = getArguments(selector, node);
        return TextNode.valueOf(SPACE_JOINER.join(args));
    }

    private JsonNode function(final JsonSelector selector, final JsonNode node) {
        Preconditions.checkArgument(selector != null, "selector must be non-null");
        Preconditions.checkArgument(node != null, "node must be non-null");

        // lookup function
        final String name = selector.getFunctionName();
        final JsonNodeFunction function = config.getFunctions().get(name);
        Preconditions.checkState(function != null, "Undefined function: " + name);

        // apply function
        final JsonNode[] args = getArguments(selector, node);
        return function.apply(args);
    }

    private JsonNode[] getArguments(final JsonSelector selector, final JsonNode node) {
        final Map<String, List<String>> arguments = selector.getArguments();

        int arg = 0;
        final JsonNode[] args = new JsonNode[arguments.size()];
        for (final List<String> path : arguments.values()) {
            args[arg++] = JsonNodeUtils.read(node, path);
        }
        return args;
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
        final Map<String, Field> properties = config.getMapping().getProperties();
        final Set<String> mappingFields = properties.keySet();
        for (final String fieldName : mappingFields) {
            if (document.containsKey(fieldName)) {
                buildDocumentMap(fieldName, map);
            }
        }

        // add fields that do not appear in mapping
        for (final String fieldName : document.keys()) {
            if (!mappingFields.contains(fieldName)) {
                buildDocumentMap(fieldName, map);
            }
        }

        return map;
    }

    private Object getFieldValues(final String fieldName) {
        final Collection<Object> values = document.get(fieldName);
        if (values.size() == 0) {
            // don't output empty values
            return null;
        }

        if (values.size() == 1) {
            // single values
            return values.iterator().next();
        }

        final Mapping mapping = config.getMapping();
        if (config.isIgnoreMissingFields() && !mapping.hasField(fieldName)) {
            return null;
        }

        final Field field = mapping.getField(fieldName);
        final Boolean sortTokens = field.isSortTokens();
        if (sortTokens == null || !sortTokens.booleanValue()) {
            // no sort; just return insertion order
            return values;
        }

        // check if we have any comparables
        List<Comparable> comparables = null;
        Class<?> comparablesClass = null;
        for (final Object o : values) {
            if (o instanceof Comparable) {
                if (comparables == null) {
                    comparables = Lists.newArrayList();
                    comparablesClass = o.getClass();
                } else if (!comparablesClass.isInstance(o)) {
                    continue;
                }
                comparables.add((Comparable) o);
            }
        }

        if (comparables == null) {
            // sorting is not possible
            return values;
        }

        // sort!
        Collections.sort(comparables, Ordering.natural());
        final boolean finished = comparables.size() == values.size();

        // optimization: remove analyzed strings which are subsets of another string
        // - "1234 MAIN STREET"
        // - "1234 MAIN STREET APT 12345"
        // - "1234 MAIN STREET APT 12345 RESTON VA 20191"
        if (field.getType() == Type.TEXT && CharSequence.class.isAssignableFrom(comparablesClass)) {
            CharSequence last = null;
            for (int i = 0; i < comparables.size(); i++) {
                final CharSequence csq = (CharSequence) comparables.get(i);
                if (i != 0) {
                    if (StringUtils.startsWith(csq, last) //
                            && csq.length() > last.length()
                            && Character.isWhitespace(csq.charAt(last.length()))) {
                        comparables.remove(--i);
                    }
                }
                last = csq;
            }
        }
        if (finished) {
            return comparables;
        }

        // mixture of comparables without non-comparables
        final List<Object> sorted = Lists.newArrayList(comparables);
        for (final Object o : values) {
            if (!comparablesClass.isInstance(o)) {
                sorted.add(o);
            }
        }

        return sorted;
    }

    private JsonNode jsonPath(final JsonSelector selector, final JsonNode node) {
        if (jsonPathConfig == null) {
            jsonPathConfig = Configuration.builder() //
                    .jsonProvider(new JacksonJsonNodeJsonProvider(mapper)) //
                    .mappingProvider(new JacksonMappingProvider(mapper)) //
                    .build();
        }

        // traverse node using JsonPath and return value
        return selector.getJsonPath().read(node, jsonPathConfig);
    }

    protected void put(final ElasticDoc doc, final Field field, final Object obj) {
        if (obj == null) {
            // we don't store null values
            return;
        }

        final Set<Field> visited;
        if (field.getAdditionalTargets().size() != 0) {
            visited = Sets.newHashSet();
        } else {
            visited = null;
        }

        put(doc, field, obj, visited, field);
    }

    protected void put(
            final ElasticDoc doc,
            final Field field,
            final Object val,
            final Set<Field> visited,
            final Field originalField) {
        if (visited != null) {
            if (visited.contains(field)) {
                return;
            }
            visited.add(field);
        }

        final Object value;
        if (field == originalField) {
            // give plugins a chance to mutate value
            Object v = val;
            for (final ElasticDocBuilderPlugin plugin : config.getPlugins()) {
                v = plugin.beforePut(doc, field, v);
            }
            value = v;
        } else {
            value = val;
        }

        final Mapping mapping = config.getMapping();
        if (value instanceof CharSequence) {
            // apply token filters
            final CharSequence csq = (CharSequence) value;
            final TokenFilter tokenFilter = mapping.getFieldTokenFilter(field.getName());
            tokenFilter.execute(csq, token -> {
                document.put(field.getName(), token);
                for (final ElasticDocBuilderPlugin plugin : config.getPlugins()) {
                    plugin.put(doc, field, token, originalField, value);
                }
            });
        } else {
            // store object
            document.put(field.getName(), value);
            for (final ElasticDocBuilderPlugin plugin : config.getPlugins()) {
                plugin.put(doc, field, value, originalField, value);
            }
        }

        // copy to additional fields?
        final List<String> additionalTargets = field.getAdditionalTargets();
        if (additionalTargets.size() == 0) {
            return;
        }

        for (final String additionalTarget : additionalTargets) {
            if (config.isIgnoreMissingAdditionalTargets() && !mapping.hasField(additionalTarget)) {
                continue;
            }
            // recursive copy
            final Field additionalField = mapping.getField(additionalTarget);
            put(doc, additionalField, value, visited, originalField);
        }
    }

    /**
     * Adds a field/value pair to an Elasticsearch document.
     *
     * @param field
     *            field
     * @param node
     *            value
     */
    protected void putNode(final ElasticDoc doc, final Field field, final JsonNode node) {
        // pipeline: deserialize to object -> token filters for textual data
        config.getValueProducer().traverse(field, node, obj -> {
            put(doc, field, obj);
        });
    }

    private JsonNode read(final JsonSelector selector, final JsonNode node) {
        switch (selector.getType()) {
        case PATH:
            return selector.read(node);
        case JSON_PATH:
            return jsonPath(selector, node);
        case CONCAT:
            return concat(selector, node);
        case FUNCTION:
            return function(selector, node);
        default:
            throw new IllegalStateException("Unsupported selector: " + selector.toString());
        }
    }

    public JsonNode readValue(final CharSequence json) {
        Preconditions.checkArgument(json != null, "json must be non-null");

        JsonNode node;
        try {
            node = mapper.readTree(new CharSequenceReader(json));
        } catch (final IllegalArgumentException | IllegalStateException | IOException e) {
            throw new ElasticDocException("Unable to parse source document", e);
        }
        return node;
    }

    protected CharSequence writeDocumentAsJson(final boolean compact) throws ElasticDocException {
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
