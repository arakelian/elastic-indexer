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

import java.time.ZonedDateTime;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.elastic.model.Field;
import com.google.common.io.BaseEncoding;

public class DefaultValueSerializer implements ValueSerializer {
    @Override
    public String serialize(final Field field, final Object value) {
        if (value == null) {
            return null;
        }

        // most common
        if (value instanceof CharSequence) {
            return value.toString();
        }
        if (value instanceof Number) {
            return value.toString();
        }
        if (value instanceof ZonedDateTime) {
            return DateUtils.toStringIsoFormat((ZonedDateTime) value);
        }

        // less frequent
        if (value instanceof byte[]) {
            return BaseEncoding.base64().encode((byte[]) value);
        }

        // default conversion
        return value.toString();
    }
}
