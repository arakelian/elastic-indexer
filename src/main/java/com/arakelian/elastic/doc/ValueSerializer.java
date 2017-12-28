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

import com.arakelian.elastic.model.Field;

public interface ValueSerializer {
    /**
     * <p>
     * Converts a complex Java type to a simple type, suitable for rendering to JSON without any
     * further type conversion.
     * </p>
     * 
     * <p>
     * Supported Java types:
     * </p>
     * <ul>
     * <li><code>CharSequence</code> for JSON strings</li>
     * <li><code>Boolean</code> for JSON booleans</li>
     * <li><code>Number</code> for JSON numbers</li>
     * <li><code>Collection</code> or <code>Object[]</code> for JSON arrays</li>
     * <li><code>Map</code> for JSON objects</li>
     * </ul>
     * 
     * @param field
     *            Elastic field definition
     * @param value
     *            field value
     * @return a Java object that can be rendered to JSON without any further conversion.
     * @throws ValueException
     *             if value cannot be converted
     */
    public Object serialize(Field field, Object value) throws ValueException;
}
