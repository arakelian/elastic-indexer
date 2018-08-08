package com.arakelian.elastic.bulk;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public class DefaultBulkOperationFactory implements BulkOperationFactory {
    private final Multimap<Class, BulkOperationFactory> mappings = LinkedListMultimap.create();

    @Override
    public List<BulkOperation> getBulkOperations(final Action action, final Object document)
            throws IOException {
        ImmutableList.Builder<BulkOperation> list = null;

        for (final Class clazz : mappings.keySet()) {
            if (!clazz.isInstance(document)) {
                continue;
            }

            final Collection<BulkOperationFactory> factories = mappings.get(clazz);
            for (final BulkOperationFactory factory : factories) {
                final List<BulkOperation> ops = factory.getBulkOperations(action, document);
                if (ops != null && ops.size() != 0) {
                    if (list == null) {
                        list = ImmutableList.<BulkOperation> builder();
                    }
                    list.addAll(ops);
                }
            }
        }

        if (list == null) {
            return ImmutableList.of();
        }
        return list.build();
    }

    public void put(final Class clazz, final BulkOperationFactory factory) {
        Preconditions.checkNotNull(clazz, "clazz must be non-null");
        Preconditions.checkNotNull(factory, "factory must be non-null");
        mappings.put(clazz, factory);
    }
}
