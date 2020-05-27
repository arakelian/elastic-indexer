package com.arakelian.elastic.bulk;

import org.junit.Assert;
import org.junit.Test;

import com.arakelian.elastic.bulk.BulkOperation.Action;
import com.arakelian.elastic.model.IndexTest;
import com.arakelian.elastic.model.VersionComponents;

public class BulkOperationTest {
    @Test
    public void testSimple() {
        BulkOperation op = ImmutableBulkOperation.builder() //
                .action(Action.CREATE) //
                .elasticVersion(VersionComponents.of(7, 0)) //
                .id("id") //
                .source("{\n\t\"name\":\"Greg\tArakelian\",\n\t\"gender\":\"male\"\n}") //
                .index(IndexTest.MINIMAL) //
                .build();
        Assert.assertEquals(
                "{\"CREATE\":{\"_index\":\"index_name\",\"_id\":\"id\"}}\n"
                        + "{\"name\":\"Greg\\tArakelian\",\"gender\":\"male\"}\n",
                op.getOperation().toString());
    }

    @Test
    public void testEscaping() {
        BulkOperation op = ImmutableBulkOperation.builder() //
                .action(Action.CREATE) //
                .elasticVersion(VersionComponents.of(7, 0)) //
                .id("id\nline\\2\nline\\3") //
                .source("{\n\t\"name\":\"Greg\tArakelian\",\n\t\"gender\":\"male\"\n}") //
                .index(IndexTest.MINIMAL) //
                .build();
        Assert.assertEquals(
                "{\"CREATE\":{\"_index\":\"index_name\",\"_id\":\"id\\nline\\\\2\\nline\\\\3\"}}\n"
                        + "{\"name\":\"Greg\\tArakelian\",\"gender\":\"male\"}\n",
                op.getOperation().toString());
    }
}
