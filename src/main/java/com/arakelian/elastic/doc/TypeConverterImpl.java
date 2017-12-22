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

import org.apache.commons.lang3.BooleanUtils;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.Field.Type;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;

public class TypeConverterImpl implements TypeConverter {
    @Override
    public Object coerceValue(final Field field, final String rawValue) {
        Preconditions.checkArgument(field != null, "field must be non-null");

        // whitespace is not indexed and is therefore removed
        final String trimmedValue = MoreStringUtils.trimWhitespace(rawValue);
        if (trimmedValue == null || trimmedValue.length() == 0) {
            return null;
        }

        final Type type = field.getType();
        if (type == null) {
            // cannot check value if type is unknown
            return rawValue;
        }

        final boolean ignoreMalformed = field.isIgnoreMalformed() != null && field.isIgnoreMalformed();
        switch (type) {
        case BINARY: {
            // note: base64 encoding is not the same thing as hex and can include all the letters of
            // the alphabet
            if (!BaseEncoding.base64().canDecode(trimmedValue)) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue);
            }
            // we return original value
            break;
        }

        case BOOLEAN:
            final Boolean bool = BooleanUtils.toBooleanObject(trimmedValue);
            if (bool == null) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue);
            }
            return bool;

        case DATE:
            final ZonedDateTime date = DateUtils.toZonedDateTimeUtc(trimmedValue);
            if (date == null) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue);
            }
            // standardize the date in ISO format
            return DateUtils.toStringIsoFormat(date);

        case BYTE:
            try {
                return Byte.parseByte(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }

        case SHORT:
            try {
                return Short.parseShort(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }

        case INTEGER:
            try {
                return Integer.parseInt(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }

        case LONG:
            try {
                return Long.parseLong(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }

        case DOUBLE:
            try {
                Double.parseDouble(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }
            // we don't want to lose any precision so we return original value
            break;

        case FLOAT:
            try {
                Float.parseFloat(trimmedValue);
            } catch (final NumberFormatException nfe) {
                if (ignoreMalformed) {
                    return null;
                }
                throw new TypeConverterException(field, trimmedValue, nfe);
            }
            // we don't want to lose any precision so we return original value
            break;

        case TEXT:
        case KEYWORD:
            // use raw value
            break;

        default:
            throw new TypeConverterException("Unrecognized field type: " + field, field, trimmedValue);
        }

        // use raw value but stripped all leading and trailing whitespace, which includes line
        // delimiters
        return trimmedValue;
    }
}
