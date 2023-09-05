package com.arakelian.elastic.jackson;

import java.io.IOException;
import java.util.Set;

import com.arakelian.elastic.Views.Elastic;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Sets;

public class OmitObsoleteJsonSerializer<T> extends JsonSerializer<T> {
    private final Class<T> clazz;
    private final Set<Class<? extends Elastic>> obsolete;

    @SafeVarargs
    public OmitObsoleteJsonSerializer(final Class<T> clazz, final Class<? extends Elastic>... obsolete) {
        this.clazz = clazz;
        this.obsolete = Sets.newHashSet(obsolete);
    }

    @Override
    public boolean isEmpty(final SerializerProvider provider, final T value) {
        final Class<?> activeView = provider.getActiveView();
        if (isObsolete(activeView)) {
            return true;
        }
        return false;
    }

    protected boolean isObsolete(final Class<?> activeView) {
        return obsolete.contains(activeView);
    }

    @Override
    public void serialize(final T value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {
        final Class<?> activeView = serializers.getActiveView();
        if (isObsolete(activeView)) {
            return;
        }

        // pass through to default serializer
        final JsonSerializer<Object> defaultSerializer = serializers
                .findTypedValueSerializer(clazz, true, null);
        defaultSerializer.serialize(value, gen, serializers);
    }
}
