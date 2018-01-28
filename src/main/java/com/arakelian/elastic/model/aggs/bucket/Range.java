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

package com.arakelian.elastic.model.aggs.bucket;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

@Value.Immutable
@JsonSerialize(as = ImmutableRange.class)
@JsonDeserialize(builder = ImmutableRange.Builder.class)
@Value.Style(from = "using", get = { "is*", "get*" }, depluralize = true)
public interface Range {
    public String getKey();

    @Nullable
    public Object getFrom();

    @Nullable
    public Object getTo();

    /**
     * Returns a CIDR mask that defines a range.
     * 
     * @return a CIDR mask that defines a range.
     */
    @Nullable
    public String getMask();

    @Value.Check
    public default void checkRange() {
        final Object from = getFrom();
        final Object to = getTo();

        final String mask = getMask();
        if (mask != null) {
            Preconditions.checkState(
                    from == null && to == null,
                    "Cannot combine 'from' and 'to' with CIDR 'mask'");
            return;
        }

        if (from != null) {
            Preconditions.checkState(to == null || from.getClass().isInstance(to));
        } else if (to != null) {
            Preconditions.checkState(from == null || to.getClass().isInstance(from));
        } else {
            throw new IllegalStateException("Range must define a 'from' or 'to' value");
        }
    }
}
