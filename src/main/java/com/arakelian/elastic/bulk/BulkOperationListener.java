package com.arakelian.elastic.bulk;

@FunctionalInterface
public interface BulkOperationListener {
    public void accept(boolean success);
}
