package com.arakelian.elastic;

import static com.arakelian.elastic.model.Mapping.Dynamic.STRICT;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableMapping;

@RunWith(Parameterized.class)
public class FieldTypeTest extends AbstractElasticTest {

    /**
     * Returns the cross-product of all Elastic versions and field types
     * 
     * @return cross-product of all Elastic versions and field types
     */
    @Parameters(name = "elastic-{0} / {1}")
    public static Object[][] data() {
        Object[] versions = AbstractElasticTest.data();
        Type[] types = Type.values();
        int vlen = versions.length;
        int tlen = types.length;

        Object[][] data = new Object[vlen * tlen][];
        for (int i = 0; i < vlen; i++) {
            for (int j = 0; j < tlen; j++) {
                int k = i * tlen + j;
                data[k] = new Object[2];
                data[k][0] = versions[i];
                data[k][1] = types[j];
            }
        }
        return data;
    }

    private final Type type;

    public FieldTypeTest(String version, Type type) throws Exception {
        super(version);
        this.type = type;
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
                                .name(DEFAULT_TYPE) //
                                .type(type) //
                                .build())
                .build();
        withIndex(mapping, index -> {
            // pass if index created
        });
    }
}
