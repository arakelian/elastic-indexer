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

package com.arakelian.elastic.bulk.event;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

import com.arakelian.core.utils.ExecutorUtils;
import com.arakelian.elastic.bulk.BulkOperation;
import com.arakelian.elastic.bulk.event.IndexerEvent.Status;
import com.arakelian.elastic.model.BulkIndexerStats;
import com.arakelian.elastic.model.BulkResponse;
import com.arakelian.elastic.model.BulkResponse.BulkOperationResponse;
import com.google.common.base.Preconditions;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class IndexerEventPublisher implements IndexerListener, Closeable {
    /** Optional disruptor we create, start and shutdown **/
    private final Disruptor<IndexerEvent> disruptor;

    /** Ring buffer we publish to **/
    private final RingBuffer<IndexerEvent> ringBuffer;

    /** We can only be closed once **/
    private final AtomicBoolean closed = new AtomicBoolean();

    @SafeVarargs
    public IndexerEventPublisher(final int ringBufferSize, final EventHandler<IndexerEvent>... handlers) {
        // start disruptor that receives DAO events and forwards to consumers
        this.disruptor = new Disruptor<>( //
                new IndexerEventFactory(), //
                ringBufferSize, //
                ExecutorUtils.newThreadFactory(IndexerEventPublisher.class, false), //
                ProducerType.SINGLE, //
                new BlockingWaitStrategy());
        disruptor.handleEventsWith(handlers);
        disruptor.start();

        // get ring buffer we publish to
        this.ringBuffer = disruptor.getRingBuffer();
    }

    public IndexerEventPublisher(final RingBuffer<IndexerEvent> ringBuffer) {
        this.disruptor = null;
        this.ringBuffer = ringBuffer;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            if (disruptor != null) {
                this.disruptor.shutdown();
            }
        }
    }

    @Override
    public void closed(final BulkIndexerStats stats) {
    }

    private void failed(final BulkOperation op) {
        Preconditions.checkArgument(op != null, "op must be non-null");
        final long sequence = ringBuffer.next();
        try {
            final IndexerEvent event = ringBuffer.get(sequence);
            initialize(event, op, Status.FAILED);
        } finally {
            ringBuffer.publish(sequence);
        }
    }

    private void initialize(final IndexerEvent event, final BulkOperation op, final Status status) {
        // status will get set by caller
        event.reset();
        event.setStatus(status);
        event.setAction(op.getAction());
        event.setId(op.getId());
        event.setIndex(op.getIndex());
        event.setType(op.getType());
        event.setVersion(op.getVersion());
        event.setVersionType(op.getVersionType());
    }

    @Override
    public void onFailure(final BulkOperation op, final BulkOperationResponse response) {
        failed(op);
    }

    @Override
    public void onFailure(final BulkOperation op, final BulkResponse result) {
        failed(op);
    }

    @Override
    public void onFailure(final BulkOperation op, final Throwable t) {
        failed(op);
    }

    @Override
    public void onSuccess(final BulkOperation op) {
        Preconditions.checkArgument(op != null, "op must be non-null");
        final long sequence = ringBuffer.next();
        try {
            final IndexerEvent event = ringBuffer.get(sequence);
            initialize(event, op, Status.SUCCEEDED);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}
