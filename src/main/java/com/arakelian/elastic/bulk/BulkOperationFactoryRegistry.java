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

package com.arakelian.elastic.bulk;

import java.io.IOException;
import java.util.List;

import org.immutables.value.Value;

import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.google.common.collect.ImmutableList;

@Value.Immutable
public abstract class BulkOperationFactoryRegistry implements BulkOperationFactory {
    @Override
    public List<BulkOperation> createBulkOperations(final Object document, final Action action)
            throws IOException {
        ImmutableList.Builder<BulkOperation> list = null;

        for (final BulkOperationFactory factory : getFactories()) {
            if (!factory.supports(document)) {
                continue;
            }

            final List<BulkOperation> ops = factory.createBulkOperations(document, action);
            if (ops != null && ops.size() != 0) {
                if (list == null) {
                    list = ImmutableList.<BulkOperation> builder();
                }
                list.addAll(ops);
            }
        }

        if (list == null) {
            return ImmutableList.of();
        }
        return list.build();
    }

    @Value.Default
    public List<BulkOperationFactory> getFactories() {
        return ImmutableList.of();
    }

    @Override
    public boolean supports(final Object document) {
        for (final BulkOperationFactory factory : getFactories()) {
            if (factory.supports(document)) {
                return true;
            }
        }
        return false;
    }
}
