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

import org.immutables.value.Value;

import com.arakelian.core.utils.MoreStringUtils;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableTrimWhitespace.class)
@JsonDeserialize(builder = ImmutableTrimWhitespace.Builder.class)
@JsonTypeName(TokenFilter.TRIM_WHITESPACE)
public abstract class TrimWhitespace extends AbstractCharFilter {
    @Override
    public String apply(final String value) {
        // nulls return null, otherwise always non-null
        return MoreStringUtils.trimWhitespace(value);
    }
}
