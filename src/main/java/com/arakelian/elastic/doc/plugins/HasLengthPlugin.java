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

package com.arakelian.elastic.doc.plugins;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import org.immutables.value.Value;

import com.arakelian.elastic.doc.ElasticDoc;
import com.arakelian.elastic.doc.ElasticDocException;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.Mapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class HasLengthPlugin implements ElasticDocBuilderPlugin {
    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableHasLengthConfig.class)
    @JsonDeserialize(builder = ImmutableHasLengthConfig.Builder.class)
    public interface HasLengthConfig {
        /**
         * Returns a list of field names that should be excluded from the plugin.
         *
         * @return a list of field names that should be excluded from the plugin.
         */
        @Value.Default
        public default Set<String> getExcludeFields() {
            return ImmutableSet.copyOf(Field.META_FIELDS);
        }

        /**
         * Returns a list of field names that should be included by the plugin. Note that if
         * includes are specified, we only include those.
         *
         * @return a list of field names that should be included by the plugin.
         */
        @Value.Default
        public default Set<String> getIncludeFields() {
            return ImmutableSet.of();
        }

        /**
         * Returns the name of the indicator field in the Elastic index.
         *
         * @return the name of the indicator field in the Elastic index.
         */
        public String getIndicator();

        @Value.Default
        public default String getIndicatorPrefix() {
            return "HAS_LENGTH_" + getLength();
        }

        /**
         * Returns the length of a text value considered 'lengthy', e.g. beyond which we trigger an
         * indicator value for the field.
         *
         * @return the length of a text value considered 'lengthy'
         */
        @Value.Default
        public default int getLength() {
            return 50;
        }

        @Value.Derived
        @Value.Auxiliary
        public default Predicate<Field> getNamePredicate() {
            return new Predicate<>() {
                @Override
                public boolean test(final Field field) {
                    final String name = field.getName();

                    final Set<String> excludes = getExcludeFields();
                    if (excludes.contains(name)) {
                        // explicit excludes always skipped
                        return false;
                    }

                    // if includes are specified we only include those
                    final Set<String> includes = getIncludeFields();
                    if (includes.size() != 0 && !includes.contains(name)) {
                        return false;
                    }

                    // everything included by default
                    return true;
                }
            };
        }

        @Value.Default
        @Value.Auxiliary
        public default Predicate<Field> getPredicate() {
            return new Predicate<>() {
                @Override
                public boolean test(final Field t) {
                    return true;
                }
            };
        }

        @Value.Default
        public default String getSeparator() {
            return "__";
        }
    }

    /**
     * Name of the plugin
     */
    private final String name;

    /**
     * Plugin configuration
     */
    private final HasLengthConfig config;

    public HasLengthPlugin(final HasLengthConfig config) {
        this(config, "lengthy");
    }

    public HasLengthPlugin(final HasLengthConfig config, final String name) {
        this.config = Preconditions.checkNotNull(config, "config must be non-null");
        this.name = Preconditions.checkNotNull(name, "name must be non-null");
    }

    @Override
    public void after(final JsonNode raw, final ElasticDoc doc) throws ElasticDocException {
        final Mapping mapping = doc.getConfig().getMapping();
        final Field indicatorField = mapping.getField(config.getIndicator());

        boolean hasLengthy = false;

        for (final String name : doc.getFields()) {
            final Field field = mapping.getField(name);

            final Collection<Object> values = doc.get(name);
            for (final Object o : values) {
                if (test(field) && isLengthy(o)) {
                    final String indicator = config.getIndicatorPrefix() + config.getSeparator()
                            + name.toUpperCase();
                    doc.put(indicatorField, indicator);
                    hasLengthy = true;
                }
            }
        }

        if (hasLengthy) {
            doc.put(indicatorField, config.getIndicatorPrefix());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    private boolean isLengthy(final Object o) {
        if (o instanceof CharSequence) {
            final CharSequence csq = (CharSequence) o;
            if (csq.length() > config.getLength()) {
                return true;
            }
        }
        return false;
    }

    private boolean test(final Field field) {
        if (config.getNamePredicate().test(field) && config.getPredicate().test(field)) {
            return true;
        }
        for (final Field subfield : field.getFields().values()) {
            if (test(subfield)) {
                return true;
            }
        }
        return false;
    }
}
