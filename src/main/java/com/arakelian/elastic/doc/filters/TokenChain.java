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

import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;

public class TokenChain implements TokenFilter {
    public static TokenFilter link(final List<TokenFilter> filters) {
        if (filters == null || filters.size() == 0) {
            return ImmutableNullFilter.of();
        }

        final int length = filters.size();
        TokenFilter next = filters.get(length - 1);

        for (int i = length - 2; i >= 0; i--) {
            final TokenFilter filter = filters.get(i);
            next = new TokenChain(filter, next);
        }
        return next;
    }

    private final TokenFilter filter;
    private final TokenFilter next;

    public TokenChain(final TokenFilter filter, final TokenFilter next) {
        this.filter = Preconditions.checkNotNull(filter);
        this.next = next;
    }

    @Override
    public <T extends Consumer<String>> T accept(final String value, final T output) {
        filter.accept(value, out -> {
            if (next != null) {
                next.accept(out, output);
            } else {
                output.accept(out);
            }
        });
        return output;
    }
}
