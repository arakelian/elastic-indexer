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
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;

public class ValueException extends ElasticDocException {
    private final Field field;
    private final JsonNode node;

    public ValueException(final Field field, final JsonNode node) {
        this(field, node, null);
    }

    public ValueException(final Field field, final JsonNode node, final Throwable cause) {
        this("Malformed value for " + field + ": " + node, field, node, cause);
    }

    public ValueException(final String message, final Field field, final JsonNode node) {
        this(message, field, node, null);
    }

    public ValueException(
            final String message,
            final Field field,
            final JsonNode node,
            final Throwable cause) {
        super(message, cause);
        Preconditions.checkArgument(node != null, "node must be non-null");
        this.field = field;
        this.node = node;
    }

    public final Field getField() {
        return field;
    }

    public final JsonNode getNode() {
        return node;
    }
}
