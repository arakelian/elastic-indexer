package com.arakelian.elastic.refresh;

import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class NullRefreshLimiter implements RefreshLimiter {
    /** Singleton instance **/
    public static final NullRefreshLimiter INSTANCE = new NullRefreshLimiter();

    private NullRefreshLimiter() {
        // singleton
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }

    @Override
    public void enqueueRefresh(final String name) throws RejectedExecutionException {
        // do nothing
    }

    @Override
    public boolean tryRefresh(final String name) {
        // do nothing
        return true;
    }

    @Override
    public boolean tryRefresh(final String name, final long timeout, final TimeUnit unit)
            throws InterruptedException {
        // do nothing
        return true;
    }

    @Override
    public boolean waitForRefresh(final String name, final long timeout, final TimeUnit unit) {
        // do nothing
        return true;
    }
}
