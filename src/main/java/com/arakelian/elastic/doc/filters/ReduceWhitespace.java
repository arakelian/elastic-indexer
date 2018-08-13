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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableReduceWhitespace.class)
@JsonDeserialize(builder = ImmutableReduceWhitespace.Builder.class)
@JsonTypeName(TokenFilter.REDUCE_WHITESPACE)
public abstract class ReduceWhitespace extends AbstractCharFilter {
    @Override
    public String apply(final String value) {
        if (value == null || value.length() == 0) {
            // nulls return null, otherwise always non-null
            return value;
        }

        final int length = value.length();
        int end = length;
        int start = 0;
        while (start < end && Character.isWhitespace(value.charAt(start))) {
            start++;
        }
        while (start < end && Character.isWhitespace(value.charAt(end - 1))) {
            end--;
        }

        // do we have a run of whitespace?
        int run = 0;
        for (int i = start; i < end; i++) {
            final char ch = value.charAt(i);
            if (Character.isWhitespace(ch)) {
                if (run++ == 1) {
                    break;
                }
            } else {
                run = 0;
            }
        }

        if (run <= 1) {
            // no runs of whitespace inside string
            return start > 0 || end < length ? value.substring(start, end) : value;
        }

        final StringBuilder buf = new StringBuilder(end - start);

        run = 0;
        for (int i = start; i < end; i++) {
            final char ch = value.charAt(i);
            if (Character.isWhitespace(ch)) {
                run++;
            } else {
                if (run > 0) {
                    buf.append(' ');
                    run = 0;
                }
                buf.append(ch);
            }
        }

        // return reduction
        return buf.toString();
    }
}
