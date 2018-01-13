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

package com.arakelian.elastic.doc;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.arakelian.elastic.model.ElasticDocConfig;
import com.arakelian.elastic.model.Field;

public interface ElasticDoc {
    /**
     * Returns the value(s) associated with a field.
     *
     * @param field
     *            field values
     * @return the value(s) associated with a field.
     */
    public Collection<Object> get(final String field);

    public ElasticDocConfig getConfig();

    /**
     * Returns a copy of the document as a <code>Map</code>.
     *
     * @return a copy of the document as a <code>Map</code>.
     */
    public Map<String, Object> getDocumentAsMap();

    /**
     * Returns a list of field names in the Elastic document.
     *
     * Not all Elastic documents will contain all fields that are in the mapping (see
     * {@link ElasticDocConfig#getMapping()}).
     *
     * @return a list of field names in the current Elastic document.
     */
    public Set<String> getFields();

    public boolean hasField(final String name);

    /**
     * Adds a field / value combination to the Elastic document. Fields can have multiple values,
     * and repeated calls to this method for the same field will accumulate field values.
     *
     * @param field
     *            field (must be in {@link ElasticDocConfig#getMapping()}
     * @param value
     *            value (any object serializable by Jackson)
     */
    public void put(final Field field, final Object value);

    /**
     * Returns a rendering of the current Elastic document in JSON format.
     *
     * @return a rendering of the current Elastic document in JSON format.
     */
    public String writeDocumentAsJson();
}
