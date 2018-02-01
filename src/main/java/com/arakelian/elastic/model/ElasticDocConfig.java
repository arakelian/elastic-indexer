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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.immutables.value.Value;

import com.arakelian.elastic.doc.DefaultValueProducer;
import com.arakelian.elastic.doc.ValueProducer;
import com.arakelian.elastic.doc.plugin.ElasticDocBuilderPlugin;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

@Value.Immutable
@JsonSerialize(as = ImmutableElasticDocConfig.class)
@JsonDeserialize(builder = ImmutableElasticDocConfig.Builder.class)
@JsonPropertyOrder({ "targets", "mapping" })
public abstract class ElasticDocConfig {
    public static class JsonPointerDeserializer extends JsonDeserializer<JsonPointer> {
        @Override
        public JsonPointer deserialize(final JsonParser p, final DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            return JsonPointer.compile(p.getText());
        }
    }

    public static class JsonPointerSerializer extends JsonSerializer<JsonPointer> {
        @Override
        public void serialize(
                final JsonPointer value,
                final JsonGenerator gen,
                final SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeString(value.toString());
        }
    }

    public Collection<Field> getFieldsTargetedBy(final JsonPointer sourcePath) {
        final Collection<Field> collection = getSourcePathsMapping().get(sourcePath);
        return collection != null ? collection : ImmutableList.of();
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
    public Set<JsonPointer> getSourcePaths() {
        // must copy otherwise not serializable
        return ImmutableSet.copyOf(getSourcePathsMapping().keySet());
    }

    /**
     * Returns a list of source paths that should be mapped to each Elastic field.
     *
     * @return list of source paths that should be mapped to each Elastic field.
     */
    @Value.Default
    @JsonDeserialize(as = LinkedHashMultimap.class, contentUsing = JsonPointerDeserializer.class)
    @JsonSerialize(contentUsing = JsonPointerSerializer.class)
    public Multimap<String, JsonPointer> getTargets() {
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

    /**
     * Returns a list of source paths and the {@link Field}s to which it is indexed.
     *
     * @return list of source paths and the {@link Field}s to which it is indexed.
     */
    @JsonIgnore
    @Value.Lazy
    protected Multimap<JsonPointer, Field> getSourcePathsMapping() {
        final Multimap<JsonPointer, Field> result = LinkedHashMultimap.create();
        final Mapping mapping = getMapping();

        final Multimap<String, JsonPointer> targets = getTargets();
        for (final String target : targets.keySet()) {
            final Field field = mapping.getField(target);
            if (field == null) {
                throw new IllegalStateException("Mapping does not contain field \"" + target + "\"");
            }
            for (final JsonPointer path : targets.get(target)) {
                result.put(path, field);
            }
        }

        return result;
    }

    @JsonIgnore
    @Value.Check
    protected ElasticDocConfig normalizeTargets() {
        final Set<String> identityFields = getIdentityFields();
        if (identityFields.size() == 0) {
            return this;
        }

        final LinkedHashMultimap<String, JsonPointer> newTargets = LinkedHashMultimap.create();
        for (final String identity : identityFields) {
            final JsonPointer target = JsonPointer.compile("/" + identity);
            if (getTargets().containsKey(identity)) {
                final Collection<JsonPointer> targets = getTargets().get(identity);
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
}
