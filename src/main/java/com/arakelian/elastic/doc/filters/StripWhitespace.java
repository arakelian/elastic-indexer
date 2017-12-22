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

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(singleton = true)
@JsonSerialize(as = ImmutableStripWhitespace.class)
@JsonDeserialize(builder = ImmutableStripWhitespace.Builder.class)
@JsonTypeName(TokenFilter.STRIP_WHITESPACE)
public abstract class StripWhitespace extends AbstractCharFilter {
    @Override
    public String apply(final String val) {
        if (val == null || val.length() == 0) {
            return val;
        }

        final int length = val.length();

        // do we have whitespace?
        int run = 0;
        for (int i = 0; i < length; i++) {
            final char ch = val.charAt(i);
            if (Character.isWhitespace(ch)) {
                if (run++ == 0) {
                    break;
                }
            } else {
                run = 0;
            }
        }

        if (run == 0) {
            // no whitespace inside string
            return val;
        }

        final StringBuilder buf = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            final char ch = val.charAt(i);
            if (!Character.isWhitespace(ch)) {
                buf.append(ch);
            }
        }

        // return reduction
        return buf != null ? buf.toString() : StringUtils.EMPTY;
    }
}
