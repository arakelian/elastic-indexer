package com.arakelian.elastic;

import static com.arakelian.elastic.model.Mapping.Dynamic.STRICT;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.elastic.model.DateRangeTest;
import com.arakelian.elastic.model.DoubleRangeTest;
import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.FloatRangeTest;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableGeoPoint;
import com.arakelian.elastic.model.ImmutableMapping;
import com.arakelian.elastic.model.IndexedDocument;
import com.arakelian.elastic.model.IntegerRangeTest;
import com.arakelian.elastic.model.LongRangeTest;
import com.arakelian.jackson.utils.JacksonUtils;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

@RunWith(Parameterized.class)
public class FieldTypeTest extends AbstractElasticTest {

    /**
     * Returns the cross-product of all Elastic versions and field types
     *
     * @return cross-product of all Elastic versions and field types
     */
    @Parameters(name = "elastic-{0} / {1}")
    public static Object[][] data() {
        final Object[] versions = AbstractElasticTest.data();
        final Type[] types = Type.values();
        final int vlen = versions.length;
        final int tlen = types.length;

        final Object[][] data = new Object[vlen * tlen][];
        for (int i = 0; i < vlen; i++) {
            for (int j = 0; j < tlen; j++) {
                final int k = i * tlen + j;
                data[k] = new Object[2];
                data[k][0] = versions[i];
                data[k][1] = types[j];
            }
        }
        return data;
    }

    private final Type type;

    public FieldTypeTest(final String version, final Type type) throws Exception {
        super(version);
        this.type = type;
    }

    private Object getTestValue() {
        switch (type) {
        case BINARY:
            return "hello".getBytes(Charsets.UTF_8);
        case BOOLEAN:
            return Boolean.TRUE;
        case BYTE:
            return Byte.valueOf(Byte.MAX_VALUE);
        case DATE:
            return DateUtils.nowWithZoneUtc();
        case DATE_RANGE:
            return DateRangeTest.EXTREMA;
        case DOUBLE:
            return Double.valueOf(Math.PI);
        case DOUBLE_RANGE:
            return DoubleRangeTest.EXTREMA;
        case FLOAT:
        case HALF_FLOAT:
        case SCALED_FLOAT:
            return Float.valueOf((float) Math.PI);
        case FLOAT_RANGE:
            return FloatRangeTest.EXTREMA;
        case GEO_POINT:
            return ImmutableGeoPoint.builder().lat(38.8977d).lon(77.0365d).build();
        case INTEGER:
            return Integer.valueOf(Integer.MAX_VALUE);
        case INTEGER_RANGE:
            return IntegerRangeTest.EXTREMA;
        case IP:
            return ImmutableList.of("192.168.1.1", "::ffff:10.0.0.1");
        case LONG:
            return Long.valueOf(Long.MAX_VALUE);
        case LONG_RANGE:
            return LongRangeTest.EXTREMA;
        case SHORT:
            return Short.valueOf(Short.MAX_VALUE);
        case KEYWORD:
        case COMPLETION:
        case TEXT:
        case TOKEN_COUNT:
            return "Sample " + type.name().toLowerCase() + " value";
        default:
            throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    /**
     * Creates index mapping for field type.
     *
     * @throws IOException
     *             if index cannot be created
     */
    @Test
    public void testType() throws IOException {
        final ImmutableMapping mapping = ImmutableMapping.builder() //
                .dynamic(STRICT) //
                .addField(
                        ImmutableField.builder() //
                                .name("value") //
                                .type(type) //
                                .build())
                .build();

        withIndex(mapping, index -> {
            final String id = MoreStringUtils.shortUuid();

            final Map<String, Object> doc = Maps.newLinkedHashMap();
            doc.put("value", getTestValue());

            final IndexedDocument response = assertSuccessful( //
                    IndexedDocument.class, //
                    elasticClient.indexDocument(
                            index.getName(), //
                            DEFAULT_TYPE, //
                            id, //
                            JacksonUtils.toString(doc, false)));

            assertEquals(index.getName(), response.getIndex());
            assertEquals(DEFAULT_TYPE, response.getType());
            assertEquals(id, response.getId());
            assertEquals("created", response.getResult());
            assertEquals(Boolean.TRUE, response.isCreated());
        });
    }
}
