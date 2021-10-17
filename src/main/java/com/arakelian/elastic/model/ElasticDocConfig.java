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

package com.arakelian.elastic.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.doc.DefaultValueProducer;
import com.arakelian.elastic.doc.JsonNodeFunction;
import com.arakelian.elastic.doc.ValueProducer;
import com.arakelian.elastic.doc.plugins.ElasticDocBuilderPlugin;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableElasticDocConfig.class)
@JsonDeserialize(builder = ImmutableElasticDocConfig.Builder.class)
@JsonPropertyOrder({ "targets", "mapping" })
public abstract class ElasticDocConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticDocConfig.class);

    private void buildSourcePathMapping(
            final Multimap<JsonSelector, Field> result,
            final Collection<JsonSelector> paths,
            final Set<String> visited,
            final String target) {
        // make sure we don't visit more than once
        if (!visited.add(target)) {
            return;
        }

        // check if field exists
        if (isIgnoreMissingFields() && !getMapping().hasField(target)) {
            return;
        }

        // get target field
        final Field field = getMapping().getField(target);
        if (field == null) {
            throw new IllegalStateException("Mapping does not contain field \"" + target + "\"");
        }

        // map source paths to target field
        final Set<JsonSelector> skip = selectorsCopiedToField(field);
        for (final JsonSelector path : paths) {
            if (!skip.contains(path)) {
                result.put(path, field);
            } else {
                LOGGER.trace(
                        "SKIPPING target '{}' selector '{}' since another field has it along with copy_to:'{}'",
                        field.getName(),
                        path.getSelector(),
                        field.getName());
            }
        }
    }

    public Collection<Field> getFieldsTargetedBy(final JsonSelector sourcePath) {
        final Collection<Field> collection = getSourcePathsMapping().get(sourcePath);
        return collection != null ? collection : ImmutableList.of();
    }

    @JsonIgnore
    @Value.Default
    @Value.Auxiliary
    public Map<String, JsonNodeFunction> getFunctions() {
        return ImmutableMap.of();
    }

    /**
     * Returns a list of field names that are mapped to identical source paths.
     *
     * @return list of field names that are mapped to identical source paths.
     */
    @JsonIgnore
    @Value.Default
    public Set<String> getIdentityFields() {
        return ImmutableSet.of();
    }

    @Value.Auxiliary
    public abstract Mapping getMapping();

    /**
     * Returns the {@link ObjectMapper} used for parsing JSON documents.
     *
     * @return the {@link ObjectMapper} used for parsing JSON documents.
     */
    @JsonIgnore
    @Value.Default
    @Value.Auxiliary
    public ObjectMapper getObjectMapper() {
        return JacksonUtils.getObjectMapper();
    }

    /**
     * Returns a list of plugins configured for this mapping.
     *
     * @return list of plugins configured for this mapping.
     */
    @Value.Default
    @Value.Auxiliary
    @JsonIgnore
    public List<ElasticDocBuilderPlugin> getPlugins() {
        return ImmutableList.of();
    }

    @JsonIgnore
    @Value.Derived
    public Set<JsonSelector> getSourcePaths() {
        // must copy otherwise not serializable
        return ImmutableSet.copyOf(getSourcePathsMapping().keySet());
    }

    /**
     * Returns a list of source paths and the {@link Field}s to which it is indexed.
     *
     * @return list of source paths and the {@link Field}s to which it is indexed.
     */
    @JsonIgnore
    @Value.Lazy
    protected Multimap<JsonSelector, Field> getSourcePathsMapping() {
        final Multimap<JsonSelector, Field> result = LinkedHashMultimap.create();
        final Set<String> visited = Sets.newHashSet();

        final Multimap<String, JsonSelector> targets = getTargets();
        for (final String target : targets.keySet()) {
            final Collection<JsonSelector> paths = targets.get(target);
            buildSourcePathMapping(result, paths, visited, target);
        }

        return result;
    }

    /**
     * Returns a list of source paths that should be mapped to each Elastic field.
     *
     * @return list of source paths that should be mapped to each Elastic field.
     */
    @Value.Default
    @JsonDeserialize(as = LinkedHashMultimap.class)
    public Multimap<String, JsonSelector> getTargets() {
        return LinkedHashMultimap.create();
    }

    @JsonIgnore
    @Value.Default
    @Value.Auxiliary
    public ValueProducer getValueProducer() {
        return new DefaultValueProducer(getObjectMapper());
    }

    @Value.Default
    @Value.Auxiliary
    public boolean isCompact() {
        return true;
    }

    @Value.Default
    @Value.Auxiliary
    public boolean isIgnoreMissingAdditionalTargets() {
        return false;
    }

    @Value.Default
    @Value.Auxiliary
    public boolean isIgnoreMissingFields() {
        return false;
    }

    @JsonIgnore
    @Value.Check
    protected ElasticDocConfig normalizeTargets() {
        final Set<String> identityFields = getIdentityFields();
        if (identityFields.size() == 0) {
            return this;
        }

        final LinkedHashMultimap<String, JsonSelector> newTargets = LinkedHashMultimap.create();
        for (final String identity : identityFields) {
            final JsonSelector target = JsonSelector.of(identity);
            if (getTargets().containsKey(identity)) {
                final Collection<JsonSelector> targets = getTargets().get(identity);
                Preconditions.checkState(
                        targets.contains(target), //
                        "Target \"%s\" is not an identity mapping",
                        identity);
            }

            // add target
            newTargets.put(identity, target);
        }

        // rebuild with additional targets
        return ImmutableElasticDocConfig.builder() //
                .from(this) //
                .identityFields(ImmutableSet.of()) //
                .putAllTargets(newTargets) //
                .build();
    }

    /**
     * Returns a list of selectors from all fields that copy_to the given target field.
     *
     * @param targetField
     *            target field
     * @return a list of selectors from all fields that copy_to the given target field
     */
    private Set<JsonSelector> selectorsCopiedToField(final Field targetField) {
        Set<JsonSelector> selectors = null;

        for (final Field field : getMapping().getFields()) {
            if (!field.getCopyTo().contains(targetField.getName())) {
                continue;
            }

            final Multimap<String, JsonSelector> targets = getTargets();
            if (targets.containsKey(field.getName())) {
                if (selectors == null) {
                    selectors = Sets.newLinkedHashSet();
                }
                selectors.addAll(targets.get(field.getName()));
            }
        }

        return selectors != null ? selectors : ImmutableSet.of();
    }
}
