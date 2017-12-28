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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.doc.ElasticDoc;
import com.arakelian.elastic.doc.ElasticDocBuilderException;
import com.arakelian.elastic.model.Field;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
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
    public void complete(final ElasticDoc doc) {
        final MessageDigest func;
        try {
            func = getMessageDigest();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new ElasticDocBuilderException("Unable to create hash function with " + config.toString(),
                    e);
        }

        for (final String field : doc.getFields()) {
            if (!config.getPredicate().test(field)) {
                continue;
            }

            final Collection<Object> values = doc.get(field);
            for (final Object val : values) {
                doc.traverse(val, o -> {
                    func.digest(o.toString().getBytes(Charsets.UTF_8));
                });
            }
        }

        final Field field = doc.getConfig().getMapping().getField(config.getFieldName());
        final String hash = BaseEncoding.base64().omitPadding().encode(func.digest());
        doc.put(field, hash);
    }

    protected MessageDigest getMessageDigest() throws NoSuchAlgorithmException, NoSuchProviderException {
        final String provider = config.getProvider();
        if (StringUtils.isEmpty(provider)) {
            return MessageDigest.getInstance(config.getAlgorithm());
        } else {
            return MessageDigest.getInstance(config.getAlgorithm(), provider);
        }
    }
}
