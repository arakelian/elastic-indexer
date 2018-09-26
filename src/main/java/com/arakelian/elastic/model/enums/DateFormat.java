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

package com.arakelian.elastic.model.enums;

import java.util.Map;
import java.util.TreeMap;

import com.arakelian.elastic.model.search.Query;
import com.google.common.base.Preconditions;

/**
 * Regular expression flags that can be applied to {@link Query}.
 *
 * @see <a href=
 *      "http://lucene.apache.org/core/4_9_0/core/org/apache/lucene/util/automaton/RegExp.html">Lucene
 *      documentation</a>
 */
public enum DateFormat {
    /**
     * A formatter for the number of milliseconds since the epoch. Note, that this timestamp is
     * subject to the limits of a Java Long.MIN_VALUE and Long.MAX_VALUE.
     **/
    EPOCH_MILLIS,

    /**
     * A formatter for the number of seconds since the epoch. Note, that this timestamp is subject
     * to the limits of a Java Long.MIN_VALUE and Long. MAX_VALUE divided by 1000 (the number of
     * milliseconds in a second).
     **/
    EPOCH_SECOND,

    /**
     * A generic ISO datetime parser where the date is mandatory and the time is optional. Full
     * details here.
     **/
    DATE_OPTIONAL_TIME, STRICT_DATE_OPTIONAL_TIME,

    /**
     * A basic formatter for a full date as four digit year, two digit month of year, and two digit
     * day of month: yyyyMMdd.
     **/
    BASIC_DATE,

    /**
     * A basic formatter that combines a basic date and time, separated by a T:
     * yyyyMMdd'T'HHmmss.SSSZ.
     **/
    BASIC_DATE_TIME,

    /**
     * A basic formatter that combines a basic date and time without millis, separated by a T:
     * yyyyMMdd'T'HHmmssZ.
     **/
    BASIC_DATE_TIME_NO_MILLIS,

    /**
     * A formatter for a full ordinal date, using a four digit year and three digit dayOfYear:
     * yyyyDDD.
     **/
    BASIC_ORDINAL_DATE,

    /**
     * A formatter for a full ordinal date and time, using a four digit year and three digit
     * dayOfYear: yyyyDDD'T'HHmmss.SSSZ.
     **/
    BASIC_ORDINAL_DATE_TIME,

    /**
     * A formatter for a full ordinal date and time without millis, using a four digit year and
     * three digit dayOfYear: yyyyDDD'T'HHmmssZ.
     **/
    BASIC_ORDINAL_DATE_TIME_NO_MILLIS,

    /**
     * A basic formatter for a two digit hour of day, two digit minute of hour, two digit second of
     * minute, three digit millis, and time zone offset: HHmmss.SSSZ.
     **/
    BASIC_TIME,

    /**
     * A basic formatter for a two digit hour of day, two digit minute of hour, two digit second of
     * minute, and time zone offset: HHmmssZ.
     **/
    BASIC_TIME_NO_MILLIS,

    /**
     * A basic formatter for a two digit hour of day, two digit minute of hour, two digit second of
     * minute, three digit millis, and time zone off set prefixed by T: 'T'HHmmss.SSSZ.
     **/
    BASIC_T_TIME,

    /**
     * A basic formatter for a two digit hour of day, two digit minute of hour, two digit second of
     * minute, and time zone offset prefixed by T: 'T'HHmmssZ.
     **/
    BASIC_T_TIME_NO_MILLIS,

    /**
     * A basic formatter for a full date as four digit weekyear, two digit week of weekyear, and one
     * digit day of week: xxxx'W'wwe.
     **/
    BASIC_WEEK_DATE, STRICT_BASIC_WEEK_DATE,

    /**
     * A basic formatter that combines a basic weekyear date and time, separated by a T:
     * xxxx'W'wwe'T'HHmmss.SSSZ.
     **/
    BASIC_WEEK_DATE_TIME, STRICT_BASIC_WEEK_DATE_TIME,

    /**
     * A basic formatter that combines a basic weekyear date and time without millis, separated by a
     * T: xxxx'W'wwe'T'HHmmssZ.
     **/
    BASIC_WEEK_DATE_TIME_NO_MILLIS, STRICT_BASIC_WEEK_DATE_TIME_NO_MILLIS,

    /**
     * A formatter for a full date as four digit year, two digit month of year, and two digit day of
     * month: yyyy-MM-dd.
     **/
    DATE, STRICT_DATE,

    /** A formatter that combines a full date and two digit hour of day: yyyy-MM-dd'T'HH. **/
    DATE_HOUR, STRICT_DATE_HOUR,

    /**
     * A formatter that combines a full date, two digit hour of day, and two digit minute of hour:
     * yyyy-MM-dd'T'HH:mm.
     **/
    DATE_HOUR_MINUTE, STRICT_DATE_HOUR_MINUTE,

    /**
     * A formatter that combines a full date, two digit hour of day, two digit minute of hour, and
     * two digit second of minute: yyyy-MM-dd'T'HH:mm:ss.
     **/
    DATE_HOUR_MINUTE_SECOND, STRICT_DATE_HOUR_MINUTE_SECOND,

    /**
     * A formatter that combines a full date, two digit hour of day, two digit minute of hour, two
     * digit second of minute, and three digit fraction of second: yyyy-MM-dd'T'HH:mm:ss.SSS.
     **/
    DATE_HOUR_MINUTE_SECOND_FRACTION, STRICT_DATE_HOUR_MINUTE_SECOND_FRACTION,

    /**
     * A formatter that combines a full date, two digit hour of day, two digit minute of hour, two
     * digit second of minute, and three digit fraction of second: yyyy-MM-dd'T'HH:mm:ss.SSS.
     **/
    DATE_HOUR_MINUTE_SECOND_MILLIS, STRICT_DATE_HOUR_MINUTE_SECOND_MILLIS,

    /**
     * A formatter that combines a full date and time, separated by a T:
     * yyyy-MM-dd'T'HH:mm:ss.SSSZZ.
     **/
    DATE_TIME, STRICT_DATE_TIME,

    /**
     * A formatter that combines a full date and time without millis, separated by a T:
     * yyyy-MM-dd'T'HH:mm:ssZZ.
     **/
    DATE_TIME_NO_MILLIS, STRICT_DATE_TIME_NO_MILLIS,

    /** A formatter for a two digit hour of day: HH **/
    HOUR, STRICT_HOUR,

    /** A formatter for a two digit hour of day and two digit minute of hour: HH:mm. **/
    HOUR_MINUTE, STRICT_HOUR_MINUTE,

    /**
     * A formatter for a two digit hour of day, two digit minute of hour, and two digit second of
     * minute: HH:mm:ss.
     **/
    HOUR_MINUTE_SECOND, STRICT_HOUR_MINUTE_SECOND,

    /**
     * A formatter for a two digit hour of day, two digit minute of hour, two digit second of
     * minute, and three digit fraction of second: HH:mm:ss.SSS.
     **/
    HOUR_MINUTE_SECOND_FRACTION, STRICT_HOUR_MINUTE_SECOND_FRACTION,

    /**
     * A formatter for a two digit hour of day, two digit minute of hour, two digit second of
     * minute, and three digit fraction of second: HH:mm:ss.SSS.
     **/
    HOUR_MINUTE_SECOND_MILLIS, STRICT_HOUR_MINUTE_SECOND_MILLIS,

    /**
     * A formatter for a full ordinal date, using a four digit year and three digit dayOfYear:
     * yyyy-DDD.
     **/
    ORDINAL_DATE, STRICT_ORDINAL_DATE,

    /**
     * A formatter for a full ordinal date and time, using a four digit year and three digit
     * dayOfYear: yyyy-DDD'T'HH:mm:ss.SSSZZ.
     **/
    ORDINAL_DATE_TIME, STRICT_ORDINAL_DATE_TIME,

    /**
     * A formatter for a full ordinal date and time without millis, using a four digit year and
     * three digit dayOfYear: yyyy-DDD'T'HH:mm:ssZZ.
     **/
    ORDINAL_DATE_TIME_NO_MILLIS, STRICT_ORDINAL_DATE_TIME_NO_MILLIS,

    /**
     * A formatter for a two digit hour of day, two digit minute of hour, two digit second of
     * minute, three digit fraction of second, and time zone offset: HH:mm:ss.SSSZZ.
     **/
    TIME, STRICT_TIME,

    /**
     * A formatter for a two digit hour of day, two digit minute of hour, two digit second of
     * minute, and time zone offset: HH:mm:ssZZ.
     **/
    TIME_NO_MILLIS, STRICT_TIME_NO_MILLIS,

    /**
     * A formatter for a two digit hour of day, two digit minute of hour, two digit second of
     * minute, three digit fraction of second, and time zone offset prefixed by T:
     * 'T'HH:mm:ss.SSSZZ.
     **/
    T_TIME, STRICT_T_TIME,

    /**
     * A formatter for a two digit hour of day, two digit minute of hour, two digit second of
     * minute, and time zone offset prefixed by T: 'T'HH:mm:ssZZ.
     **/
    T_TIME_NO_MILLIS, STRICT_T_TIME_NO_MILLIS,

    /**
     * A formatter for a full date as four digit weekyear, two digit week of weekyear, and one digit
     * day of week: xxxx-'W'ww-e.
     **/
    WEEK_DATE, STRICT_WEEK_DATE,

    /**
     * A formatter that combines a full weekyear date and time, separated by a T:
     * xxxx-'W'ww-e'T'HH:mm:ss.SSSZZ.
     **/
    WEEK_DATE_TIME, STRICT_WEEK_DATE_TIME,

    /**
     * A formatter that combines a full weekyear date and time without millis, separated by a T:
     * xxxx-'W'ww-e'T'HH:mm:ssZZ.
     **/
    WEEK_DATE_TIME_NO_MILLIS, STRICT_WEEK_DATE_TIME_NO_MILLIS,

    /** A formatter for a four digit weekyear: xxxx. **/
    WEEKYEAR, STRICT_WEEKYEAR,

    /** A formatter for a four digit weekyear and two digit week of weekyear: xxxx-'W'ww. **/
    WEEKYEAR_WEEK, STRICT_WEEKYEAR_WEEK,

    /**
     * A formatter for a four digit weekyear, two digit week of weekyear, and one digit day of week:
     * xxxx-'W'ww-e.
     **/
    WEEKYEAR_WEEK_DAY, STRICT_WEEKYEAR_WEEK_DAY,

    /** A formatter for a four digit year: yyyy. **/
    YEAR, STRICT_YEAR,

    /** A formatter for a four digit year and two digit month of year: yyyy-MM. **/
    YEAR_MONTH, STRICT_YEAR_MONTH,

    /**
     * A formatter for a four digit year, two digit month of year, and two digit day of month:
     * yyyy-MM-dd.
     **/
    YEAR_MONTH_DAY, STRICT_YEAR_MONTH_DAY;

    private static final Map<String, DateFormat> NAMES;

    static {
        Map<String, DateFormat> names = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (DateFormat df : DateFormat.values()) {
            names.put(df.name(), df);
            names.put(df.name().replaceAll("_", ""), df);
        }
        NAMES = names;
    }

    public static DateFormat of(String value) {
        Preconditions.checkArgument(NAMES.containsKey(value), "No DateFormat with name %s", value);
        return NAMES.get(value);
    }
}
