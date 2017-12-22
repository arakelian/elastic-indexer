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
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public final class TokenCollector implements Consumer<String> {
    private final List<String> list = Lists.newArrayList();
    private final boolean omitEmpty;

    public TokenCollector() {
        this(true);
    }

    public TokenCollector(final boolean omitEmpty) {
        this.omitEmpty = omitEmpty;
    }

    @Override
    public void accept(final String value) {
        if (!omitEmpty || !StringUtils.isEmpty(value)) {
            list.add(value);
        }
    }

    public List<String> get() {
        return list;
    }

    public Set<String> getUnique() {
        return ImmutableSet.copyOf(get());
    }

    public void reset() {
        list.clear();
    }
}
