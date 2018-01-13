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

package com.arakelian.elastic.doc;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class ValueCollector<T> implements Consumer<T> {
    private final List<T> list = Lists.newArrayList();
    private final boolean omitEmpty;

    public ValueCollector() {
        this(true);
    }

    public ValueCollector(final boolean omitEmpty) {
        this.omitEmpty = omitEmpty;
    }

    @Override
    public final void accept(final T value) {
        if (value == null) {
            return;
        }
        if (omitEmpty && value instanceof CharSequence) {
            final CharSequence csq = (CharSequence) value;
            if (csq.length() == 0) {
                return;
            }
        }
        list.add(value);
    }

    public final List<T> get() {
        return list;
    }

    public final Set<T> getUnique() {
        return ImmutableSet.copyOf(get());
    }

    public final void reset() {
        list.clear();
    }

    @Override
    public String toString() {
        return "ValueCollector{" + Joiner.on(", ").join(list) + "}";
    }
}
