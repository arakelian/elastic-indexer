package com.arakelian.elastic.jackson;

import com.arakelian.elastic.Views.Elastic.Version8;

public class OmitObsolete8EnumSerializer extends OmitObsoleteJsonSerializer<Enum> {
    public OmitObsolete8EnumSerializer() {
        super(Enum.class, Version8.class);
    }
}
