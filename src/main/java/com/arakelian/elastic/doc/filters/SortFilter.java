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

package com.arakelian.elastic.doc.filters;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;

@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableSortFilter.class)
@JsonDeserialize(builder = ImmutableSortFilter.Builder.class)
@JsonTypeName(TokenFilter.SORT)
public abstract class SortFilter implements TokenFilter, Serializable {
    private final List<String> values = Lists.newArrayList();

    @Override
    public <T extends Consumer<String>> T accept(final String value, final T output) {
        if (value == null) {
            try {
                // flush sorted values
                Collections.sort(values, String.CASE_INSENSITIVE_ORDER);
                for (final String v : values) {
                    output.accept(v);
                }
            } finally {
                // make sure we always reset, no matter what
                values.clear();
            }
        } else {
            values.add(value);
        }

        return output;
    }
}
