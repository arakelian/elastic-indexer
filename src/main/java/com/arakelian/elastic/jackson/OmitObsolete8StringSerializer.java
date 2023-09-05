package com.arakelian.elastic.jackson;

import com.arakelian.elastic.Views.Elastic.Version8;

public class OmitObsolete8StringSerializer extends OmitObsoleteJsonSerializer<String> {
    public OmitObsolete8StringSerializer() {
        super(String.class, Version8.class);
    }
}
