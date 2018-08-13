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
import java.util.function.Consumer;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableLengthFilter.class)
@JsonDeserialize(builder = ImmutableLengthFilter.Builder.class)
@JsonTypeName(TokenFilter.LENGTH_FILTER)
public abstract class LengthFilter implements TokenFilter, Serializable {
    @Override
    public <T extends Consumer<String>> T accept(final String value, final T output) {
        if (value == null) {
            // always pass nulls through to signal end of tokens
            output.accept(null);
        } else {
            final int length = value.length();
            if (length >= getMinimum() && length <= getMaximum()) {
                output.accept(value);
            }
        }
        return output;
    }

    @Value.Default
    public int getMaximum() {
        return Integer.MAX_VALUE;
    }

    @Value.Default
    public int getMinimum() {
        return 0;
    }

    @Value.Check
    public LengthFilter normalizeRange() {
        final int min = getMinimum();
        final int max = getMaximum();
        Preconditions.checkState(min <= max, "minimum value must be less than or equal to maximum");

        final int newMin = Math.max(min, 0);
        final int newMax = max > 0 ? max : Integer.MAX_VALUE;
        if (min != newMin || max != newMax) {
            return ImmutableLengthFilter.builder() //
                    .minimum(newMin) //
                    .maximum(newMax) //
                    .build();
        }
        return this;
    }
}
