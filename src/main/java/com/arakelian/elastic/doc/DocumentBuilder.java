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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.jackson.utils.JacksonUtils;
import com.arakelian.json.JsonWriter;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.BaseEncoding;

/**
 * Builds an Elasticsearch document. Elasticsearch documents are simple (flat) maps that have string
 * keys and either string or List&lt;String&gt; values.
 *
 * This class is not thread-safe.
 */
public class DocumentBuilder {
    /** Optional entity extractor **/
    private final DocumentBuilderConfig config;

    /** The Elastic document we're building **/
    private final Map<String, Object> document = Maps.newLinkedHashMap();

    /** Scratch buffer used to build the _all TEXT field equivalent **/
    private final StringBuilder _all = new StringBuilder();

    /**
     * An instance of {@link JsonWriter} that we can use for producing JSON.
     *
     * Note that Elastic bulk api requires that each document be on a single line so standard JSON
     * formatting is disabled. We also prevent empty fields from being written.
     */
    private final JsonWriter<StringWriter> writer = new JsonWriter<>(new StringWriter()).withPretty(false)
            .withSkipEmpty(true);

    public DocumentBuilder(final DocumentBuilderConfig config) {
        this.config = config;
    }

    /**
     * Appends a string to the _all field.
     *
     * @param value
     *            Elastic field value
     */
    private void appendAll(final Object value) {
        String sep = _all.length() != 0 ? " " : "";
        if (value instanceof List<?>) {
            // list of values
            @SuppressWarnings("unchecked")
            final List<String> valueList = (List<String>) value;
            for (int i = 0, size = valueList.size(); i < size; i++) {
                final String val = valueList.get(i);
                _all.append(sep).append(val);
                sep = " ";
            }
        } else if (value != null) {
            // single value
            _all.append(sep).append(value.toString());
        }
    }

    public synchronized String build(final DocumentMapping documentMapping, final String source)
            throws DocumentBuilderException {
        Preconditions.checkArgument(source != null, "root must be non-null");
        Preconditions.checkArgument(documentMapping != null, "documentMapping must be non-null");

        JsonNode root;
        try {
            root = JacksonUtils.getObjectMapper().readTree(source);
        } catch (final IOException e) {
            throw new DocumentBuilderException("Unable to parse source document", e);
        }

        try {
            // map document fields to one or more index fields
            final Multimap<String, Field> sourcePathsToField = documentMapping.getSourcePathsToField();
            final Set<String> paths = sourcePathsToField.keySet();
            for (final String sourcePath : paths) {
                descendSourcePath(root, sourcePath, 0, documentMapping);
            }

            final Mapping mapping = documentMapping.getMapping();

            final List<DocumentBuilderPlugin> plugins = mapping.getPlugins();
            final boolean requiresAll = plugins.stream() //
                    .filter(plugin -> plugin.requiresAll(config)) //
                    .findFirst() //
                    .isPresent();
            if (requiresAll) {
                // required prior to entity recognition
                buildAll(mapping);

                // perform entity extraction
                plugins.stream() //
                        .map(plugin -> plugin.execute(config, _all)) //
                        .forEach(map -> {
                            for (final Map.Entry<Field, String> e : map.entrySet()) {
                                final String standardizedText = e.getValue();
                                putDocument(e.getKey(), standardizedText);
                                appendAll(standardizedText);
                            }
                        });
            }
            final String json = writeDocument();
            return json;
        } finally {
            clearDocument();
            _all.setLength(0);
        }
    }

    private void buildAll(final Mapping mapping) {
        // concatenate all of the fields into a single buffer
        for (final Entry<String, Object> entry : document.entrySet()) {
            final String name = entry.getKey();
            final Field field = mapping.getField(name);
            if (field != null && field.isIncludeInAll() != null && field.isIncludeInAll().booleanValue()) {
                final Object value = entry.getValue();
                appendAll(value);
            }
        }
    }

    private void clearDocument() {
        for (final Iterator<Entry<String, Object>> it = document.entrySet().iterator(); it.hasNext();) {
            final Entry<String, Object> entry = it.next();
            final Object value = entry.getValue();
            if (value instanceof List<?>) {
                final List<?> list = (List<?>) value;
                list.clear();
            } else {
                it.remove();
            }
        }
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
                throw new MalformedValueException(field, trimmedValue);
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
                throw new MalformedValueException(field, trimmedValue);
            }
            return bool;

        case DATE:
            final ZonedDateTime date = DateUtils.toZonedDateTimeUtc(trimmedValue);
            if (date == null) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new MalformedValueException(field, trimmedValue);
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
                throw new MalformedValueException(field, trimmedValue, nfe);
            }

        case SHORT:
            try {
                return Short.parseShort(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new MalformedValueException(field, trimmedValue, nfe);
            }

        case INTEGER:
            try {
                return Integer.parseInt(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new MalformedValueException(field, trimmedValue, nfe);
            }

        case LONG:
            try {
                return Long.parseLong(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new MalformedValueException(field, trimmedValue, nfe);
            }

        case DOUBLE:
            try {
                Double.parseDouble(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new MalformedValueException(field, trimmedValue, nfe);
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
                throw new MalformedValueException(field, trimmedValue, nfe);
            }
            // we don't want to lose any precision so we return original value
            break;

        case TEXT:
        case KEYWORD:
            // use raw value
            break;

        default:
            throw new MalformedValueException("Unrecognized field type: " + field, field, trimmedValue);
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
    private void collectValues(final Field targetField, final JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode() || node.isBinary()) {
            return;
        }

        if (node.isArray()) {
            for (int i = 0, size = node.size(); i < size; i++) {
                final JsonNode item = node.get(i);
                collectValues(targetField, item);
            }
        } else if (node.isObject()) {
            final Iterator<JsonNode> children = node.elements();
            while (children.hasNext()) {
                collectValues(targetField, children.next());
            }
        } else if (!node.isPojo()) {
            // can't be NullNode
            final String text = node.asText();
            if (!StringUtils.isEmpty(text)) {
                putDocument(targetField, text);
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
    private void descendSourcePath(
            final JsonNode node,
            final String sourcePath,
            final int startPos,
            final DocumentMapping documentMapping) {
        if (node == null || node.isNull()) {
            return;
        }

        // only arrays will have a non-zero size
        for (int i = 0, size = node.size(); i < size; i++) {
            final JsonNode item = node.get(i);
            descendSourcePath(item, sourcePath, startPos, documentMapping);
        }

        final int endPos = sourcePath.indexOf('/', startPos);
        if (endPos != -1) {
            // descend into subfield
            final String subfield = sourcePath.substring(startPos, endPos);
            final JsonNode subfieldNode = node.get(subfield);
            descendSourcePath(subfieldNode, sourcePath, endPos + 1, documentMapping);
            return;
        }

        final String fieldName = sourcePath.substring(startPos);
        final JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            // the specified field was not found in the document
            return;
        }

        // we've arrived at path! put values into document
        final Multimap<String, Field> sourcePathsToField = documentMapping.getSourcePathsToField();
        final Collection<Field> fields = sourcePathsToField.get(sourcePath);
        for (final Field targetField : fields) {
            collectValues(targetField, fieldNode);
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
        Preconditions.checkArgument(field != null, "field must be non-null");
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

        final String name = field.getName();
        final Object currentVal = document.get(name);
        if (currentVal == null) {
            // only one value has been specified for Elastic field so far
            document.put(name, value);
            return;
        }

        if (currentVal instanceof List<?>) {
            // two or more values have already been specified for the Elastic field
            @SuppressWarnings("unchecked")
            final List<String> valueList = (List<String>) currentVal;
            if (!valueList.contains(value)) {
                valueList.add(value);
            }
            return;
        }

        // a second value has been specified for the Elastic field, so we need to
        // switch to a list
        final String currentString = currentVal.toString();
        if (!value.equals(currentString)) {
            final ArrayList<String> valueList = Lists.newArrayList(currentString, value);
            document.put(name, valueList);
        }
    }

    private String writeDocument() {
        try {
            // output fields
            writer.writeStartObject();
            for (final Entry<String, Object> entry : document.entrySet()) {
                writeField(writer, entry);
            }
            writer.writeEndObject();

            final String json = writer.flush().getWriter().toString();

            // sanity check
            for (int i = 0, size = json.length(); i < size; i++) {
                final char ch = json.charAt(i);
                if (ch == '\n') {
                    throw new DocumentBuilderException("Elastic document cannot contain newline character");
                }
            }
            return json;
        } catch (final IOException e) {
            // this should not happen because we're writing to a StringWriter
            throw new DocumentBuilderException("Unable to create build Elastic document", e);
        } finally {
            writer.getWriter().getBuffer().setLength(0);
            writer.reset();
        }
    }

    private void writeField(final JsonWriter<StringWriter> writer, final Entry<String, Object> entry)
            throws IOException {
        final String key = entry.getKey();
        final Object value = entry.getValue();
        if (value instanceof List<?>) {
            final List<?> valueList = (List<?>) value;
            final int size = valueList.size();
            if (size == 1) {
                writer.writeKeyUnescapedValue(key, valueList.get(0));
            } else if (size > 1) {
                writer.writeKey(key);
                writer.writeStartArray();
                for (int i = 0; i < size; i++) {
                    writer.writeObject(valueList.get(i));
                }
                writer.writeEndArray();
            }
        } else {
            // single value
            writer.writeKeyValue(key, value);
        }
    }
}
