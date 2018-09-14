package com.arakelian.elastic.model.search;

import java.io.Serializable;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableIndexedShape.class)
@JsonDeserialize(builder = ImmutableIndexedShape.Builder.class)
@JsonPropertyOrder({ "index", "type", "id", "path" })
public interface IndexedShape extends Serializable {
    /**
     * Returns the ID of the document that containing the pre-indexed shape.
     *
     * @return ID of the document that containing the pre-indexed shape.
     */
    @Nullable
    @JsonProperty("id")
    public String getId();

    /**
     * Returns name of the index where the pre-indexed shape is. Defaults to "shapes".
     *
     * @return name of the index where the pre-indexed shape is
     */
    @JsonProperty("index")
    @Value.Default
    public default String getIndex() {
        return "shapes";
    }

    /**
     * Returns the field specified as path containing the pre-indexed shape. Defaults to
     * "shape".
     *
     * @return the field specified as path containing the pre-indexed shape
     */
    @Nullable
    @JsonProperty("path")
    public String getPath();

    /**
     * Returns index type where the pre-indexed shape is.
     *
     * @return index type where the pre-indexed shape is.
     */
    @JsonProperty("type")
    @Value.Default
    public default String getType() {
        return "_doc";
    }
}