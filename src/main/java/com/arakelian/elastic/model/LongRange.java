package com.arakelian.elastic.model;

import java.io.Serializable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableLongRange.class)
@JsonDeserialize(builder = ImmutableLongRange.Builder.class)
@JsonPropertyOrder({ "gte", "lte" })
public interface LongRange extends Serializable {
    public long getGte();

    public long getLte();
}
