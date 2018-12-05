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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableVersionComponents.class)
@JsonDeserialize(builder = ImmutableVersionComponents.Builder.class)
public abstract class VersionComponents implements Serializable {
    public static final Pattern MAJOR_MINOR_BUILD = Pattern
            .compile("^(\\d+)(?:\\.(\\d+))?(?:\\.(.*|\\d+))?$");

    private static final VersionComponents EMPTY = ImmutableVersionComponents.builder().build();

    public static VersionComponents of() {
        return EMPTY;
    }

    public static VersionComponents of(final int major, final int minor) {
        return of(major, minor, 0);
    }

    public static VersionComponents of(final int major, final int minor, final int build) {
        return ImmutableVersionComponents.builder() //
                .major(major) //
                .minor(minor) //
                .build(build) //
                .build();
    }

    public static VersionComponents of(final String number) {
        final ImmutableVersionComponents.Builder builder = ImmutableVersionComponents.builder() //
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

    public boolean atLeast(final int major, final int minor, final int build) {
        switch (Integer.compare(getMajor(), major)) {
        case -1:
            return false;
        case +1:
            return true;
        default:
            break;
        }
        switch (Integer.compare(getMinor(), minor)) {
        case -1:
            return false;
        case +1:
            return true;
        default:
            break;
        }
        switch (Integer.compare(getBuild(), build)) {
        case -1:
            return false;
        case +1:
            return true;
        default:
            return true;
        }
    }

    @Value.Default
    @Value.Auxiliary
    public int getBuild() {
        return 0;
    }

    @Value.Default
    @Value.Auxiliary
    public int getMajor() {
        return 0;
    }

    @Value.Default
    @Value.Auxiliary
    public int getMinor() {
        return 0;
    }

    @Nullable
    public abstract String getNumber();

    @JsonIgnore
    @Value.Derived
    @Value.Auxiliary
    public boolean isEmpty() {
        return StringUtils.isEmpty(getNumber());
    }
}
