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
