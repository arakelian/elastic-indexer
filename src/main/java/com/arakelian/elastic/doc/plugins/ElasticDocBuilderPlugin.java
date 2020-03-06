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

package com.arakelian.elastic.doc.plugins;

import com.arakelian.elastic.doc.ElasticDoc;
import com.arakelian.elastic.doc.ElasticDocException;
import com.arakelian.elastic.model.Field;
import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings("unused")
public interface ElasticDocBuilderPlugin {
    /**
     * Invoked after {@link ElasticDoc} has been built.
     *
     * @param jsonDoc
     *            reference to root node of document
     * @param doc
     *            reference to {@link ElasticDoc} being built.
     * @throws ElasticDocException
     *             if error occurs while processing
     */
    public default void after(final JsonNode jsonDoc, final ElasticDoc doc) throws ElasticDocException {
    }

    /**
     * Invoked before {@link ElasticDoc} has any data pushed into it.
     *
     * @param jsonDoc
     *            reference to root node of document
     * @param doc
     *            reference to {@link ElasticDoc} being built.
     * @throws ElasticDocException
     *             if error occurs while processing
     */
    public default void before(final JsonNode jsonDoc, final ElasticDoc doc) {
    }

    public String getName();

    /**
     * Invoke after value is record into {@link ElasticDoc} being built.
     *
     * @param doc
     *            elastic document
     * @param field
     *            field
     * @param value
     *            value being stored in field
     * @param original
     *            original field that value was stored to (if field is different reference than
     *            original, it is an additionalTarget)
     */
    public default void put(
            final ElasticDoc doc,
            final Field field,
            final Object value,
            final Field original) {
    }
}
