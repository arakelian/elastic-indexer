package com.arakelian.elastic.model.search;

import java.io.Serializable;
import java.util.List;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.arakelian.elastic.model.enums.GeoShapeType;
import com.arakelian.jackson.model.Coordinate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableShape.class)
@JsonDeserialize(builder = ImmutableShape.Builder.class)
@JsonPropertyOrder({ "type" })
public interface Shape extends Serializable {
    @Nullable
    @JsonProperty("type")
    public GeoShapeType getType();

    /**
     * Returns the ID of the document that containing the pre-indexed shape.
     *
     * @return ID of the document that containing the pre-indexed shape.
     */
    @JsonProperty("coordinates")
    public List<Coordinate> getCoordinates();
}
