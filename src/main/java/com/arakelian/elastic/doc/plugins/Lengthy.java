package com.arakelian.elastic.doc.plugins;

import java.util.Collection;

import org.immutables.value.Value;

import com.arakelian.elastic.doc.ElasticDoc;
import com.arakelian.elastic.doc.ElasticDocException;
import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.Mapping;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Preconditions;

public class Lengthy implements ElasticDocBuilderPlugin {
    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableLengthyConfig.class)
    @JsonDeserialize(builder = ImmutableLengthyConfig.Builder.class)
    public interface LengthyConfig {
        /**
         * Returns the name of the indicator field in the Elastic index.
         *
         * @return the name of the indicator field in the Elastic index.
         */
        public String getIndicator();

        @Value.Default
        public default String getIndicatorPrefix() {
            return "HAS_LENGTHY";
        }

        /**
         * Returns the length of a text value considered 'lengthy', e.g. beyond which we trigger an
         * indicator value for the field.
         *
         * @return the length of a text value considered 'lengthy'
         */
        @Value.Default
        public default int getLengthyLength() {
            return 50;
        }
    }

    /**
     * Name of the plugin
     */
    private final String name;

    /**
     * Plugin configuration
     */
    private final LengthyConfig config;

    public Lengthy(final LengthyConfig config) {
        this(config, "lengthy");
    }

    public Lengthy(final LengthyConfig config, final String name) {
        this.config = Preconditions.checkNotNull(config, "config must be non-null");
        this.name = Preconditions.checkNotNull(name, "name must be non-null");
    }

    @Override
    public void after(final JsonNode raw, final ElasticDoc doc) throws ElasticDocException {
        final Mapping mapping = doc.getConfig().getMapping();
        final Field indicatorField = mapping.getField(config.getIndicator());

        boolean hasLengthies = false;

        for (final String name : doc.getFields()) {
            final Field field = mapping.getField(name);

            final Collection<Object> values = doc.get(name);
            for (final Object o : values) {
                final boolean lengthy = isLengthy(o);

                if (lengthy && hasDocValues(field)) {
                    doc.put(indicatorField, config.getIndicatorPrefix() + "_" + name.toUpperCase());
                    hasLengthies = true;
                }
            }
        }

        if (hasLengthies) {
            doc.put(indicatorField, config.getIndicatorPrefix());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    private boolean hasDocValues(final Field field) {
        final Boolean docValues = field.isDocValues();
        if (docValues != null && docValues.booleanValue()) {
            return true;
        }
        for (final Field subfield : field.getFields().values()) {
            if (hasDocValues(subfield)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLengthy(final Object o) {
        if (o instanceof CharSequence) {
            final CharSequence csq = (CharSequence) o;
            if (csq.length() > config.getLengthyLength()) {
                return true;
            }
        }
        return false;
    }
}
