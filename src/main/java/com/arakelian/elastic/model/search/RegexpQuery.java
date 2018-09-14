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

package com.arakelian.elastic.model.search;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.enums.RegexpFlag;
import com.arakelian.elastic.model.enums.Rewrite;
import com.arakelian.elastic.search.QueryVisitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableSet;

/**
 * @see <a href=
 *      "https://github.com/elastic/elasticsearch/blob/99f88f15c5febbca2d13b5b5fda27b844153bf1a/server/src/main/java/org/elasticsearch/index/query/RegexpQueryBuilder.java">Regexp
 *      Query</a>
 */
@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableRegexpQuery.class)
@JsonDeserialize(builder = ImmutableRegexpQuery.Builder.class)
@JsonTypeName(Query.REGEXP_QUERY)
public interface RegexpQuery extends StandardQuery {
    /**
     * Returns a literal pattern <code>String</code> for the specified <code>String</code>.
     *
     * @param value
     *            The string to be literalized
     * @return A literal string replacement
     *
     * @see <a href=
     *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-regexp-query.html#regexp-syntax">Elastic
     *      documentation</a>
     */
    public static String quote(final CharSequence value) {
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }

        final StringBuilder buf = new StringBuilder();
        for (int i = 0, length = value.length(); i < length; i++) {
            final char ch = value.charAt(length);
            switch (ch) {
            case '.':
            case '?':
            case '+':
            case '*':
            case '|':
            case '{':
            case '}':
            case '[':
            case ']':
            case '(':
            case ')':
            case '"':
            case '\\':
            case '#':
            case '@':
            case '&':
            case '<':
            case '>':
            case '~':
                buf.append('\\');
                // fall through
            default:
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    @JsonProperty("field")
    public String getFieldName();

    /**
     * Returns the set of regular expression flags to be applied.
     *
     * @return the set of regular expression flags to be applied.
     * @see <a href=
     *      "http://lucene.apache.org/core/4_9_0/core/org/apache/lucene/util/automaton/RegExp.html">Lucene
     *      documentation</a>
     */
    @Value.Default
    public default Set<RegexpFlag> getFlags() {
        return ImmutableSet.of();
    }

    @Nullable
    public Integer getMaxDeterminizedStates();

    @Nullable
    public Rewrite getRewrite();

    /**
     * Returns the regular expression that will be matched.
     *
     * @return the regular expression that will be matched.
     * @see <a href=
     *      "http://lucene.apache.org/core/4_9_0/core/org/apache/lucene/util/automaton/RegExp.html">Lucene
     *      documentation</a>
     * @see <a href=
     *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-regexp-query.html#regexp-syntax">Elastic
     *      documentation</a>
     */
    public String getValue();

    @Override
    default void accept(final QueryVisitor visitor) {
        if (!visitor.enter(this)) {
            return;
        }
        try {
            if (visitor.enterRegexpQuery(this)) {
                visitor.leaveRegexpQuery(this);
            }
        } finally {
            visitor.leave(this);
        }
    }

    @Override
    default boolean isEmpty() {
        return StringUtils.isEmpty(getValue());
    }
}
