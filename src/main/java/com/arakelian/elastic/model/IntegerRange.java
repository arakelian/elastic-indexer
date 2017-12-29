package com.arakelian.elastic.model;

import java.io.Serializable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableIntegerRange.class)
@JsonDeserialize(builder = ImmutableIntegerRange.Builder.class)
@JsonPropertyOrder({ "gte", "lte" })
public interface IntegerRange extends Serializable {
    public int getGte();

    public int getLte();
}
