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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.elastic.doc.ElasticDocBuilderPlugin;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

@Value.Immutable
@JsonSerialize(as = ImmutableElasticDocBuilderConfig.class)
@JsonDeserialize(builder = ImmutableElasticDocBuilderConfig.Builder.class)
@JsonPropertyOrder({ "targets", "mapping" })
public abstract class ElasticDocBuilderConfig implements Serializable {
    public Collection<Field> getFieldsOf(final String sourcePath) {
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
    public Set<String> getSourcePaths() {
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
    @JsonDeserialize(as = LinkedHashMultimap.class)
    protected Multimap<String, Field> getSourcePathsMapping() {
        final Multimap<String, Field> result = LinkedHashMultimap.create();
        final Mapping mapping = getMapping();

        for (final String target : getTargets().keySet()) {
            final Field field = mapping.getField(target);
            if (field == null) {
                throw new IllegalStateException("Mapping does not contain field \"" + target + "\"");
            }
            for (final String path : getTargets().get(target)) {
                if (!StringUtils.isEmpty(path)) {
                    result.put(path, field);
                }
            }
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
    public Multimap<String, String> getTargets() {
        return LinkedHashMultimap.create();
    }

    @JsonIgnore
    @Value.Check
    protected ElasticDocBuilderConfig normalizeTargets() {
        final Set<String> identityFields = getIdentityFields();
        if (identityFields.size() == 0) {
            return this;
        }

        final LinkedHashMultimap<String, String> newTargets = LinkedHashMultimap.create();
        for (final String identity : identityFields) {
            if (getTargets().containsKey(identity)) {
                final Collection<String> target = getTargets().get(identity);
                Preconditions.checkState(
                        target.contains(identity), //
                        "Target \"%s\" is not an identity mapping",
                        identity);
            }

            // add target
            newTargets.put(identity, identity);
        }

        // rebuild with additional targets
        return ImmutableElasticDocBuilderConfig.builder() //
                .from(this) //
                .identityFields(ImmutableSet.of()) //
                .targets(newTargets) //
                .build();
    }
}
