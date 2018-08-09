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

import com.arakelian.elastic.bulk.BulkOperation.Action;

public interface BulkOperationFactory {
    /**
     * Returns true if this factory supports the given document.
     *
     * @param document
     *            document to be indexed
     * @return true if this factory supports the given document
     */
    public boolean supports(final Object document);

    /**
     * Returns a list of bulk operations to perform, which may include operations that affect
     * different indexes.
     *
     * @param document
     *            document
     * @param action
     *            action to perform
     *
     * @return a list of bulk operations to perform
     * @throws IOException
     *             if there is an error creating bulk operations from the given document
     */
    public List<BulkOperation> createBulkOperations(final Object document, final Action action)
            throws IOException;
}
