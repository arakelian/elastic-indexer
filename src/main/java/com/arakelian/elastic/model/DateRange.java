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

package com.arakelian.elastic.model;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.immutables.value.Value;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.jackson.ZonedDateTimeDeserializer;
import com.arakelian.jackson.ZonedDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableDateRange.class)
@JsonDeserialize(builder = ImmutableDateRange.Builder.class)
@JsonPropertyOrder({ "gte", "lte" })
public interface DateRange extends Serializable {
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    public ZonedDateTime getGte();

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    public ZonedDateTime getLte();

    @Value.Check
    public default DateRange normalizeDates() {
        final ZonedDateTime gte = getGte();
        final ZonedDateTime lte = getLte();
        if (!DateUtils.isUtc(gte) || !DateUtils.isUtc(lte)) {
            return ImmutableDateRange.builder().gte(DateUtils.toUtc(gte)).lte(DateUtils.toUtc(lte)).build();
        }
        return this;
    }
}
