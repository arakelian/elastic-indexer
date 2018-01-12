package com.arakelian.elastic.query;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.model.query.ImmutableBoolQuery;
import com.arakelian.elastic.model.query.ImmutableQueryStringQuery;
import com.arakelian.elastic.model.query.ImmutableTermsQuery;
import com.arakelian.elastic.model.query.QueryClause;
import com.arakelian.elastic.model.query.QueryStringQuery;
import com.arakelian.elastic.model.query.TermsQuery;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import net.javacrumbs.jsonunit.JsonAssert;

public class ElasticQueryDslVisitorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticQueryDslVisitorTest.class);

    private static final QueryStringQuery QUERY_STRING_QUERY = ImmutableQueryStringQuery.builder() //
            .addField("content", "name") //
            .queryString("this AND that") //
            .build();

    private static final TermsQuery TERMS_QUERY = ImmutableTermsQuery.builder() //
            .fieldName("field") //
            .addValue("the", "quick", "brown", "fox") //
            .build();

    private JsonFactory jsonFactory;

    @Before
    public void createFactory() {
        jsonFactory = JacksonUtils.getObjectMapper().getFactory();
    }

    @Test
    public void testTermsQuery() throws IOException {
        validateQueryDsl(
                ElasticQueryDslVisitorTest.TERMS_QUERY, //
                "" + //
                        "{\n" + //
                        "  \"query\" : {\n" + //
                        "    \"terms\" : {\n" + //
                        "      \"field\" : [ \"the\", \"quick\", \"brown\", \"fox\" ]\n" + //
                        "    }\n" + //
                        "  }\n" + //
                        "}");
    }

    @Test
    public void testQueryStringQuery() throws IOException {
        validateQueryDsl(ElasticQueryDslVisitorTest.QUERY_STRING_QUERY, "" + //
                "{\n" + //
                "  \"query\" : {\n" + //
                "    \"query_string\" : {\n" + //
                "      \"fields\" : [ \"content\", \"name\" ],\n" + //
                "      \"query\" : \"this AND that\"\n" + //
                "    }\n" + //
                "  }\n" + //
                "}");
    }

    @Test
    public void testBoolQuery() throws IOException {
        validateQueryDsl(
                ImmutableBoolQuery.builder() //
                        .addMustClause(TERMS_QUERY) //
                        .addMustClause(TERMS_QUERY) //
                        .addShouldClause(QUERY_STRING_QUERY) //
                        .build(),
                "" + //
                        "{\n" + //
                        "  \"query\" : {\n" + //
                        "    \"bool\" : {\n" + //
                        "      \"must\" : [ {\n" + //
                        "        \"terms\" : {\n" + //
                        "          \"field\" : [ \"the\", \"quick\", \"brown\", \"fox\" ]\n" + //
                        "        }\n" + //
                        "      }, {\n" + //
                        "        \"terms\" : {\n" + //
                        "          \"field\" : [ \"the\", \"quick\", \"brown\", \"fox\" ]\n" + //
                        "        }\n" + //
                        "      } ],\n" + //
                        "      \"should\" : {\n" + //
                        "        \"query_string\" : {\n" + //
                        "          \"fields\" : [ \"content\", \"name\" ],\n" + //
                        "          \"query\" : \"this AND that\"\n" + //
                        "        }\n" + //
                        "      }\n" + //
                        "    }\n" + //
                        "  }\n" + //
                        "}");
    }

    private void validateQueryDsl(QueryClause query, String expected) throws IOException {
        final StringWriter writer = new StringWriter();
        try (final JsonGenerator gen = jsonFactory.createGenerator(writer).useDefaultPrettyPrinter()) {
            gen.writeStartObject();
            query.accept(new ElasticQueryDslVisitor(gen));
            gen.writeEndObject();
        }
        final String actual = writer.getBuffer().toString();
        LOGGER.info("Query DSL: {}", actual);
        JsonAssert.assertJsonEquals(expected, actual);
    }
}
