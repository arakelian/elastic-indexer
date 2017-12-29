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

package com.arakelian.elastic.doc.plugin;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.doc.ElasticDoc;
import com.arakelian.elastic.doc.ElasticDocException;
import com.arakelian.elastic.doc.ValueException;
import com.arakelian.elastic.model.Field;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;

public class ComputeDigest implements ElasticDocBuilderPlugin {
    @Value.Immutable
    @JsonSerialize(as = ImmutableComputeDigestConfig.class)
    @JsonDeserialize(builder = ImmutableComputeDigestConfig.Builder.class)
    public interface ComputeDigestConfig {
        @Value.Default
        public default String getAlgorithm() {
            return "MD5";
        }

        @Value.Default
        public default Set<String> getExcludeFields() {
            return ImmutableSet.copyOf(Field.META_FIELDS);
        }

        public String getFieldName();

        @Value.Default
        public default Set<String> getIncludeFields() {
            return ImmutableSet.of();
        }

        @Value.Default
        @Value.Auxiliary
        public default Predicate<String> getPredicate() {
            return new Predicate<String>() {
                @Override
                public boolean test(final String field) {
                    // never include the digest field itself
                    if (StringUtils.equals(field, getFieldName())) {
                        return false;
                    }

                    final Set<String> excludes = getExcludeFields();
                    if (excludes.contains(field)) {
                        // explicit excludes always skipped
                        return false;
                    }

                    // if includes are specified we only include those
                    final Set<String> includes = getIncludeFields();
                    if (includes.size() != 0 && !includes.contains(field)) {
                        return false;
                    }

                    // everything included by default
                    return true;
                }
            };
        }

        @Nullable
        public String getProvider();
    }

    private final ComputeDigestConfig config;

    public ComputeDigest(final ComputeDigestConfig config) {
        this.config = Preconditions.checkNotNull(config);
    }

    @Override
    public void completed(final ElasticDoc doc) throws ElasticDocException {
        // we will compute digest on serialized Elastic document
        JsonNode root;
        try {
            final String json = doc.writeDocumentAsJson();
            root = doc.getConfig().getObjectMapper().readValue(json, JsonNode.class);
        } catch (final IOException e) {
            throw new ElasticDocException("Unable to parse Elastic document", e);
        }

        final MessageDigest digester;
        try {
            digester = getMessageDigest();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new ElasticDocException("Unable to create hash function with " + config.toString(), e);
        }

        // traverse JSON
        traverse(root, config.getPredicate(), node -> {
            // null nodes converted to "null" which is important so that digest changes between a
            // true empty string and a null value.
            final String text = node.asText();
            final byte[] bytes = text.getBytes(Charsets.UTF_8);
            digester.update(bytes);
        });

        // add digest field
        final Field field = doc.getConfig().getMapping().getField(config.getFieldName());
        final String digest = BaseEncoding.base64().omitPadding().encode(digester.digest());
        doc.put(field, digest);
    }

    protected MessageDigest getMessageDigest() throws NoSuchAlgorithmException, NoSuchProviderException {
        final String provider = config.getProvider();
        if (StringUtils.isEmpty(provider)) {
            return MessageDigest.getInstance(config.getAlgorithm());
        } else {
            return MessageDigest.getInstance(config.getAlgorithm(), provider);
        }
    }

    protected void traverse(
            final JsonNode node,
            final Predicate<String> fieldPredicate,
            final Consumer<JsonNode> consumer) throws ValueException {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return;
        }

        if (node.isArray()) {
            // note: we do sort arrays because ordering is significant (example: geopoints)
            for (int i = 0, size = node.size(); i < size; i++) {
                final JsonNode item = node.get(i);
                traverse(item, null, consumer);
            }
            return;
        }

        if (node.isObject()) {
            // we sort all the field names, to ensure that digest doesn't change just because fields
            // appear in different orders
            final ObjectNode obj = (ObjectNode) node;
            final List<String> names = Lists.newArrayList(obj.fieldNames());
            Collections.sort(names);

            for (final String name : names) {
                // optionally narrow the list of fields included in digest
                if (fieldPredicate == null || fieldPredicate.test(name)) {
                    final JsonNode child = obj.get(name);
                    traverse(child, null, consumer);
                }
            }
            return;
        }

        if (!node.isPojo()) {
            consumer.accept(node);
        }
    }
}
