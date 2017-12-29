package com.arakelian.elastic.model;

import java.io.Serializable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableFloatRange.class)
@JsonDeserialize(builder = ImmutableFloatRange.Builder.class)
@JsonPropertyOrder({ "gte", "lte" })
public interface FloatRange extends Serializable {
    public float getGte();

    public float getLte();
}
