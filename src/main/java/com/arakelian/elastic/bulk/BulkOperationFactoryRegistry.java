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
