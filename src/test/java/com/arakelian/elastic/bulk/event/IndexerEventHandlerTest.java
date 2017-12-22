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

import static com.arakelian.elastic.bulk.BulkOperation.Action.INDEX;
import static com.arakelian.elastic.bulk.event.IndexerEvent.Status.SUCCEEDED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.elastic.bulk.AbstractBulkIndexerTest;
import com.arakelian.elastic.bulk.BulkIndexer;
import com.arakelian.elastic.bulk.BulkOperation.VersionType;
import com.arakelian.fake.model.Person;
import com.arakelian.fake.model.RandomPerson;
import com.google.common.collect.Maps;

public class IndexerEventHandlerTest extends AbstractBulkIndexerTest {
    private static class CaptureIndexerEventHandler extends AbstractIndexerEventHandler {
        public final List<IndexerEvent> events = new ArrayList<>();
        public final Map<String, IndexerEvent> eventsById = Maps.newLinkedHashMap();

        @Override
        protected void handle(final IndexerEvent event, final long sequence, final boolean endOfBatch)
                throws Exception {
            final String id = event.getId();
            final IndexerEvent copy = new IndexerEvent();
            copy.setAction(event.getAction());
            copy.setId(id);
            copy.setIndex(event.getIndex());
            copy.setType(event.getType());
            copy.setVersion(event.getVersion());
            copy.setVersionType(event.getVersionType());
            copy.setStatus(event.getStatus());
            events.add(copy);
            eventsById.put(id, copy);
        }
    }

    public IndexerEventHandlerTest(final String version) throws Exception {
        super(version);
    }

    @Test
    public void testPublish() throws IOException {
        final CaptureIndexerEventHandler handler = new CaptureIndexerEventHandler();

        try (final IndexerEventPublisher listener = new IndexerEventPublisher(64, handler)) {
            final List<Person> people = RandomPerson.listOf(10);

            withPersonIndex(index -> {
                try (final BulkIndexer<Person> indexer = createIndexer(index, listener)) {
                    indexer.index(people);
                }

                // verify we can find documents
                for (final Person person : people) {
                    assertGetDocument(index, person, null);
                }

                // make sure listener has processed all events
                listener.close();

                // verify that we received the events we expected
                Assert.assertEquals(people.size(), handler.events.size());
                for (final Person person : people) {
                    final IndexerEvent event = handler.eventsById.get(person.getId());
                    assertNotNull(event);
                    assertNotNull(event.getVersion());
                    assertEquals(INDEX, event.getAction());
                    assertEquals(index, event.getIndex());
                    assertEquals(
                            DateUtils.toEpochMillisUtc(person.getUpdated()),
                            event.getVersion().longValue());
                    assertEquals(VersionType.EXTERNAL, event.getVersionType());
                    assertEquals(SUCCEEDED, event.getStatus());
                }
            });
        }
    }
}
