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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.arakelian.faker.model.Person;
import com.arakelian.faker.service.RandomPerson;

public class BulkIndexerTest extends AbstractBulkIndexerTest {
    public BulkIndexerTest(final String version) throws Exception {
        super(version);
    }

    @Test
    public void testAddBatch() throws IOException {
        withPersonIndex(index -> {
            final List<Person> people = RandomPerson.get().listOf(10);

            final BulkIndexer tmp;
            try (final BulkIndexer indexer = tmp = createPersonIndexer()) {
                final BulkIngester ingester = createPersonIngester(index, indexer);
                assertTrue(indexer.isIdle());
                ingester.index(people);
            }
            assertTrue(tmp.isIdle());

            // verify we can find documents
            for (final Person person : people) {
                assertGetDocument(index, person, null);
            }
        });
    }

    @Test
    public void testAddIndividually() throws IOException {
        withPersonIndex(index -> {
            final List<Person> people = RandomPerson.get().listOf(10);

            final BulkIndexer tmp;
            try (final BulkIndexer indexer = tmp = createPersonIndexer()) {
                final BulkIngester ingester = createPersonIngester(index, indexer);
                assertTrue(indexer.isIdle());
                for (final Person person : people) {
                    ingester.index(person);
                }
            }
            assertTrue(tmp.isIdle());

            for (final Person person : people) {
                assertGetDocument(index, person, null);
            }
        });
    }

    @Test
    public void testDeleteBatch() throws IOException {
        withPersonIndex(index -> {
            final List<Person> people = RandomPerson.get().listOf(10);

            final BulkIndexer tmp;
            try (final BulkIndexer indexer = tmp = createPersonIndexer()) {
                // should be idle since we haven't indexed anything yet
                final BulkIngester ingester = createPersonIngester(index, indexer);
                assertTrue(indexer.isIdle());

                // when indexing, the Elastic documents will receive a version timestamp that
                // corresponds to the update date of the person
                ingester.index(people);

                // when deleting the documents, we will use a version timestamp that is equal to the
                // current time
                ingester.delete(people);
            }
            assertTrue(tmp.isIdle());
        });
    }

    @Test
    public void testDeleteIndividually() throws IOException {
        withPersonIndex(index -> {
            final Iterator<Person> people = RandomPerson.get().iteratorOf(10);

            final BulkIndexer tmp;
            try (final BulkIndexer indexer = tmp = createPersonIndexer()) {
                final BulkIngester ingester = createPersonIngester(index, indexer);
                assertTrue(indexer.isIdle());
                ingester.index(RandomPerson.get().listOf(10));
                while (people.hasNext()) {
                    ingester.delete(people.next());
                }
            }
            assertTrue(tmp.isIdle());
        });
    }
}
