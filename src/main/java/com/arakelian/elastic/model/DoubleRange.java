package com.arakelian.elastic.model;

import java.io.Serializable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableDoubleRange.class)
@JsonDeserialize(builder = ImmutableDoubleRange.Builder.class)
@JsonPropertyOrder({ "gte", "lte" })
public interface DoubleRange extends Serializable {
    public double getGte();

    public double getLte();
}
