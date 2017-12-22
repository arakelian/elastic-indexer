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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.ImmutableVersionComponents.Builder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableVersionComponents.class)
@JsonDeserialize(builder = ImmutableVersionComponents.Builder.class)
public interface VersionComponents extends Serializable {
    public static Pattern MAJOR_MINOR_BUILD = Pattern.compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(.*|\\d+))?$");

    public static VersionComponents of(final String number) {
        final Builder builder = ImmutableVersionComponents.builder() //
                .number(number);

        if (number != null) {
            final Matcher matcher = MAJOR_MINOR_BUILD.matcher(number);
            if (matcher.matches()) {
                final String major = matcher.group(1);
                if (NumberUtils.isDigits(major)) {
                    builder.major(Integer.parseInt(major));
                }
                final String minor = matcher.group(2);
                if (NumberUtils.isDigits(minor)) {
                    builder.minor(Integer.parseInt(minor));
                }
                final String build = matcher.group(3);
                if (NumberUtils.isDigits(build)) {
                    builder.build(Integer.parseInt(build));
                }
            }
        }

        return builder.build();
    }

    @Value.Default
    public default int getBuild() {
        return 0;
    }

    @Value.Default
    public default int getMajor() {
        return 0;
    }

    @Value.Default
    public default int getMinor() {
        return 0;
    }

    @Nullable
    public String getNumber();
}
