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

package com.arakelian.elastic.search;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.arakelian.elastic.model.VersionComponents;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Preconditions;

public class AbstractVisitor {
    protected final JsonGenerator writer;
    protected final VersionComponents version;

    public AbstractVisitor(final JsonGenerator writer, final VersionComponents version) {
        this.writer = Preconditions.checkNotNull(writer);
        this.version = Preconditions.checkNotNull(version);
    }

    public void writeArrayOf(final Collection<String> values) throws IOException {
        writer.writeStartArray();
        for (final String value : values) {
            writer.writeString(value);
        }
        writer.writeEndArray();
    }

    public void writeFieldValue(final String field, final Object value) throws IOException {
        if (value == null) {
            // omit null values
            return;
        }

        if (value instanceof Collection) {
            final Collection c = (Collection) value;
            if (c.size() == 0) {
                // omit empty collections
                return;
            }
            writer.writeFieldName(field);
            writer.writeStartArray();
            for (final Object o : c) {
                writer.writeObject(o);
            }
            writer.writeEndArray();
            return;
        }

        if (value instanceof CharSequence) {
            final CharSequence csq = (CharSequence) value;
            if (csq.length() == 0) {
                // omit empty strings
                return;
            }
        }

        if (value instanceof Enum) {
            // Elastic uses lowercase names
            writer.writeFieldName(field);
            writer.writeString(((Enum) value).name().toLowerCase());
            return;
        }

        // output value
        writer.writeFieldName(field);
        writer.writeObject(value);
    }

    public boolean writeFieldValue(final String field, final String value) throws IOException {
        if (!StringUtils.isEmpty(value)) {
            writer.writeFieldName(field);
            writer.writeString(value);
            return true;
        }
        return false;
    }

    public void writeFieldWithValues(final String field, final Collection<String> values) throws IOException {
        if (values != null && values.size() != 0) {
            writer.writeFieldName(field);
            writeArrayOf(values);
        }
    }
}
