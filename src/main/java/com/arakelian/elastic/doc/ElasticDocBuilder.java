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
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.doc.filters.TokenChain;
import com.arakelian.elastic.doc.filters.TokenFilter;
import com.arakelian.elastic.model.ElasticDocBuilderConfig;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.JsonWriter;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.BaseEncoding;

/**
 * Builds an Elasticsearch document. Elasticsearch documents are simple (flat) maps that have string
 * keys and either string or List&lt;String&gt; values.
 *
 * This class is not thread-safe.
 */
public class ElasticDocBuilder {
    private final class ElasticDocImpl implements ElasticDoc {
        @Override
        public CharSequence concatenate(final Predicate<Field> predicate) {
            String sep = buf.length() != 0 ? " " : "";

            // concatenate fields that match predicate
            for (final String name : document.keySet()) {
                final Field field = config.getMapping().getField(name);
                if (predicate == null || predicate.test(field)) {
                    for (final Object val : document.get(name)) {
                        final String s = Objects.toString(val);
                        buf.append(sep).append(s);
                        sep = " ";
                    }
                }
            }

            return buf;
        }

        @Override
        public ElasticDocBuilderConfig getConfig() {
            return config;
        }

        @Override
        public Set<String> getFields() {
            return document.keySet();
        }

        @Override
        public Collection<Object> getValues(final String field) {
            Preconditions.checkArgument(
                    config.getMapping().getField(field) != null,
                    "Field \"%s\" is not part of mapping",
                    field);
            return document.get(field);
        }
    }

    /** Optional entity extractor **/
    private final ElasticDocBuilderConfig config;

    /** The Elastic document we're building **/
    private final Multimap<String, Object> document = LinkedHashMultimap.create();

    /** Scratch buffer **/
    private final StringBuilder buf = new StringBuilder();

    /** Token filters **/
    private final Map<Field, TokenFilter> tokenFilters = Maps.newLinkedHashMap();

    /** Can only build one document at a time **/
    private final Lock lock = new ReentrantLock();

    /**
     * An instance of {@link JsonWriter} that we can use for producing JSON.
     *
     * Note that Elastic bulk api requires that each document be on a single line so standard JSON
     * formatting is disabled. We also prevent empty fields from being written.
     */
    private final JsonWriter<StringWriter> writer = new JsonWriter<>(new StringWriter()).withPretty(false)
            .withSkipEmpty(true);

    public ElasticDocBuilder(final ElasticDocBuilderConfig config) {
        this.config = Preconditions.checkNotNull(config);
    }

    public String build(final JsonNode root) throws ElasticDocBuilderException {
        lock.lock();
        try {
            final List<ElasticDocBuilderPlugin> plugins = config.getPlugins();
            final boolean havePlugins = plugins.size() != 0;
            try {
                final ElasticDocImpl elasticDoc;
                if (havePlugins) {
                    elasticDoc = new ElasticDocImpl();
                    for (final ElasticDocBuilderPlugin plugin : plugins) {
                        plugin.before(elasticDoc);
                    }
                } else {
                    elasticDoc = null;
                }

                // map document fields to one or more index fields
                for (final String sourcePath : config.getSourcePaths()) {
                    descendSourcePath(root, sourcePath, 0);
                }

                if (havePlugins) {
                    for (final ElasticDocBuilderPlugin plugin : plugins) {
                        plugin.after(elasticDoc);
                    }
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

    public String build(final String source) throws ElasticDocBuilderException {
        JsonNode root;
        try {
            Preconditions.checkArgument(source != null, "source must be non-null");
            root = JacksonUtils.getObjectMapper().readTree(source);
        } catch (final IllegalArgumentException | IllegalStateException | IOException e) {
            throw new ElasticDocBuilderException("Unable to parse source document", e);
        }

        return build(root);
    }

    protected Object coerceValue(final Field field, final String rawValue) {
        Preconditions.checkArgument(field != null, "field must be non-null");

        // whitespace is not indexed and is therefore removed
        final String trimmedValue = MoreStringUtils.trimWhitespace(rawValue);
        if (trimmedValue == null || trimmedValue.length() == 0) {
            return null;
        }

        final Type type = field.getType();
        if (type == null) {
            // cannot check value if type is unknown
            return rawValue;
        }

        final boolean ignoreMalformed = field.isIgnoreMalformed() != null && field.isIgnoreMalformed();
        switch (type) {
        case BINARY: {
            // note: base64 encoding is not the same thing as hex and can include all the letters of
            // the alphabet
            if (!BaseEncoding.base64().canDecode(trimmedValue)) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue);
            }
            // we return original value
            break;
        }

        case BOOLEAN:
            final Boolean bool = BooleanUtils.toBooleanObject(trimmedValue);
            if (bool == null) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue);
            }
            return bool;

        case DATE:
            final ZonedDateTime date = DateUtils.toZonedDateTimeUtc(trimmedValue);
            if (date == null) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue);
            }
            // standardize the date in ISO format
            return DateUtils.toStringIsoFormat(date);

        case BYTE:
            try {
                return Byte.parseByte(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }

        case SHORT:
            try {
                return Short.parseShort(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }

        case INTEGER:
            try {
                return Integer.parseInt(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }

        case LONG:
            try {
                return Long.parseLong(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }

        case DOUBLE:
            try {
                Double.parseDouble(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }
            // we don't want to lose any precision so we return original value
            break;

        case FLOAT:
            try {
                Float.parseFloat(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }
            // we don't want to lose any precision so we return original value
            break;

        case TEXT:
        case KEYWORD:
            // use raw value
            break;

        default:
            throw new TypeConverterException("Unrecognized field type: " + field, field, trimmedValue);
        }

        // use raw value but stripped all leading and trailing whitespace, which includes line
        // delimiters
        return trimmedValue;
    }

    /**
     * Collects all of the values from the given source node and pushes them into the Elastic
     * document field. If the source node is an array or object, all of the descendant values are
     * pushes into the target field.
     *
     * @param targetField
     *            target field in Elastic document
     * @param node
     *            source node
     */
    private void collectValues(final Field field, final JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode() || node.isBinary()) {
            return;
        }

        if (node.isArray()) {
            for (int i = 0, size = node.size(); i < size; i++) {
                final JsonNode item = node.get(i);
                collectValues(field, item);
            }
        } else if (node.isObject()) {
            final Iterator<JsonNode> children = node.elements();
            while (children.hasNext()) {
                collectValues(field, children.next());
            }
        } else if (!node.isPojo()) {
            // can't be NullNode
            final String text = node.asText();
            if (!StringUtils.isEmpty(text)) {
                putDocument(field, text);
            }
        }
    }

    /**
     * Recursively descend a source tree until we reach the source path, at which point we push all
     * of the values into the target fields.
     *
     * @param node
     *            our current location in the JSON document
     * @param sourcePath
     *            the path that we are traversing
     * @param startPos
     *            location that we are in the document
     * @param mapping
     *            index at we are putting data into
     * @throws IOException
     */
    private void descendSourcePath(final JsonNode node, final String sourcePath, final int startPos) {
        if (node == null || node.isNull()) {
            return;
        }

        // only arrays will have a non-zero size
        for (int i = 0, size = node.size(); i < size; i++) {
            final JsonNode item = node.get(i);
            descendSourcePath(item, sourcePath, startPos);
        }

        final int endPos = sourcePath.indexOf('/', startPos);
        if (endPos != -1) {
            // descend into subfield
            final String subfield = sourcePath.substring(startPos, endPos);
            final JsonNode subfieldNode = node.get(subfield);
            descendSourcePath(subfieldNode, sourcePath, endPos + 1);
            return;
        }

        final String fieldName = sourcePath.substring(startPos);
        final JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            // the specified field was not found in the document
            return;
        }

        // we've arrived at path! put values into document
        final Collection<Field> targets = config.getFieldsOf(sourcePath);

        for (final Field field : targets) {
            collectValues(field, fieldNode);
        }
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
    private void putDocument(final Field field, final String rawValue) {
        Preconditions.checkArgument(field != null, "target must be non-null");
        if (StringUtils.isEmpty(rawValue)) {
            // we don't add empty strings to Elastic document
            return;
        }

        // make sure raw value is accepted for field and that it is coerced into standardized format
        // (particularly for dates)
        final String value = Objects.toString(coerceValue(field, rawValue), null);
        if (value == null) {
            return;
        }

        final TokenFilter filter;
        if (tokenFilters.containsKey(field)) {
            filter = tokenFilters.get(field);
        } else {
            filter = TokenChain.link(field.getTokenFilters());
            tokenFilters.put(field, filter);
        }

        filter.accept(value, token -> {
            if (!StringUtils.isEmpty(token)) {
                document.put(field.getName(), token);
            }
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
