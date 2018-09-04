package com.arakelian.elastic.bulk;

import static com.google.common.util.concurrent.Uninterruptibles.getUninterruptibly;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.immutables.value.Value;

import com.arakelian.elastic.model.BulkResponse;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;

@Value.Immutable
abstract class BulkFuturesSuccessful implements Callable<Boolean> {
    @Override
    public Boolean call() throws Exception {
        final List<ListenableFuture<BulkResponse>> futures = getFutures();
        for (final Future<?> future : futures) {
            try {
                getUninterruptibly(future);
            } catch (final ExecutionException e) {
                return false;
            } catch (final RuntimeException e) {
                return false;
            } catch (final Error e) {
                return false;
            }
        }
        return true;
    }

    @Value.Default
    public List<ListenableFuture<BulkResponse>> getFutures() {
        return ImmutableList.of();
    }
}
