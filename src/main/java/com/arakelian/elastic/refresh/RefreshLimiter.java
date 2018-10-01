package com.arakelian.elastic.refresh;

import java.io.Closeable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public interface RefreshLimiter extends Closeable {
    /**
     * Refreshes the given index asynchronously.
     *
     * @param name
     *            index name
     * @throws RejectedExecutionException
     *             if exceptions occurs while refreshing index
     */
    void enqueueRefresh(String name) throws RejectedExecutionException;

    boolean tryRefresh(String name);

    boolean tryRefresh(String name, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Returns true if Elastic refresh is required and occurred within the given time frame; also
     * returns true if refresh is not required.
     *
     * @param name
     *            index name
     * @param timeout
     *            Length of time in milliseconds to wait for a refresh to occur
     * @param unit
     *            timeout units
     * @return Returns true if there are no dirty indices, false otherwise after timeout period
     */
    boolean waitForRefresh(String name, long timeout, TimeUnit unit);

}
