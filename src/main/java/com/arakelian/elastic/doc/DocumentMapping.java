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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.elastic.api.Field;
import com.arakelian.elastic.api.Index;
import com.arakelian.elastic.api.Mapping;
import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

@Value.Immutable(copy = false)
public abstract class DocumentMapping {
    /**
     * Returns a list of source paths that should be mapped to each Elastic field.
     *
     * @return list of source paths that should be mapped to each Elastic field.
     */
    public abstract Multimap<String, String> getFieldToSourcePaths();

    /**
     * Returns a list of field names that are mapped to identical source paths.
     *
     * @return list of field names that are mapped to identical source paths.
     */
    public abstract Set<String> getIdentityFields();

    /**
     * Returns index that we're mapping to.
     *
     * @return index that we're mapping to.
     */
    public abstract Index getIndex();

    @Value.Derived
    public Mapping getMapping() {
        final String type = getType();
        final Index index = getIndex();

        final Map<String, Mapping> mappings = index.getMappings();
        if (mappings.containsKey(type)) {
            return mappings.get(type);
        }

        // use _default_ mapping if type does not have custom mapping
        final Mapping mapping = mappings.get(Mapping._DEFAULT_);
        Preconditions.checkState(mapping != null,
                "Index \"" + index + "\" does not contain mapping \"" + type + "\" or _default_ mapping");
        return mapping;
    }

    /**
     * Returns a list of source paths and the {@link Field}s to which it is indexed.
     * 
     * @return list of source paths and the {@link Field}s to which it is indexed.
     */
    @Value.Derived
    public Multimap<String, Field> getSourcePathsToField() {
        final Multimap<String, Field> pathsToField = LinkedHashMultimap.create();

        final Mapping mapping = getMapping();
        final Multimap<String, String> fields = getFieldToSourcePaths();
        for (final String name : fields.keys()) {
            final Field field = mapping.getField(name);
            if (field == null) {
                throw new IllegalStateException("Index \"" + getIndex().getName()
                        + "\" does not contain field \"" + name + "\" in mapping \"" + getType() + "\"");
            }

            final Collection<String> paths = fields.get(name);
            for (final String path : paths) {
                if (!StringUtils.isEmpty(path)) {
                    pathsToField.put(path, field);
                }
            }
        }

        final Set<String> identityFields = getIdentityFields();
        for (final String name : identityFields) {
            final Field field = mapping.getField(name);
            if (field == null) {
                throw new IllegalStateException("Index \"" + getIndex().getName()
                        + "\" does not contain field \"" + name + "\" in mapping \"" + getType() + "\"");
            }
            pathsToField.put(name, field);
        }

        return pathsToField;
    }

    @Value.Default
    public String getType() {
        return Mapping._DEFAULT_;
    }
}
