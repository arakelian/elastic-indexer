/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arakelian.elastic;

import static com.arakelian.elastic.model.Mapping.Dynamic.STRICT;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.arakelian.elastic.model.Field.Type;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableMapping;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.elastic.model.aggs.bucket.BucketOrder;
import com.arakelian.elastic.model.aggs.bucket.ImmutableTermsAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutableAvgAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutableCardinalityAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutableExtendedStatsAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutableGeoBoundsAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutableGeoCentroidAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutableMaxAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutableMinAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutablePercentileRanksAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutablePercentilesAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutableStatsAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutableSumAggregation;
import com.arakelian.elastic.model.aggs.metrics.ImmutableValueCountAggregation;
import com.arakelian.elastic.model.search.ImmutableMatchQuery;
import com.arakelian.elastic.model.search.ImmutableSearch;
import com.arakelian.elastic.model.search.SearchResponse;
import com.arakelian.faker.model.Gender;
import com.arakelian.faker.model.Person;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import net.javacrumbs.jsonunit.JsonAssert;

public class ElasticClientAggregationsTest extends AbstractElasticDockerTest {
    public ElasticClientAggregationsTest(final String version) throws Exception {
        super(version);
    }

    @Test
    public void testAvg() throws IOException {
        withPeople(10, (index, people) -> {
            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutableAvgAggregation.builder() //
                                    .name("age") //
                                    .field("age") //
                                    .build()) //
                    .build();

            // execute average aggregation
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(avg(people), json, "age.value");
        });
    }

    @Test
    public void testCardinality() throws IOException {
        withPeople(50, (index, people) -> {
            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutableCardinalityAggregation.builder() //
                                    .name("age") //
                                    .field("age") //
                                    .build()) //
                    .build();

            // check response
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(countDistinct(people), json, "age.value");
        });
    }

    @Test
    public void testExtendedStats() throws IOException {
        withPeople(10, (index, people) -> {
            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutableExtendedStatsAggregation.builder() //
                                    .name("age") //
                                    .field("age") //
                                    .build()) //
                    .build();

            // execute average aggregation
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(people.size(), json, "age.count");
            JsonAssert.assertJsonPartEquals(min(people), json, "age.min");
            JsonAssert.assertJsonPartEquals(max(people), json, "age.max");
            JsonAssert.assertJsonPartEquals(avg(people), json, "age.avg");
            JsonAssert.assertJsonPartEquals(sum(people), json, "age.sum");
            JsonAssert.assertJsonNodePresent(json, "age.sum_of_squares");
            JsonAssert.assertJsonNodePresent(json, "age.variance");
            JsonAssert.assertJsonNodePresent(json, "age.std_deviation");
            JsonAssert.assertJsonNodePresent(json, "age.std_deviation_bounds.upper");
            JsonAssert.assertJsonNodePresent(json, "age.std_deviation_bounds.lower");
        });
    }

    @Test
    public void testGeoBounds() throws IOException {
        final Mapping mapping = createGeoPointMapping();

        withIndex(mapping, index -> {
            indexGeoPointDocuments(index);

            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .query(
                            ImmutableMatchQuery.builder() //
                                    .fieldName("name") //
                                    .value("musée") //
                                    .build()) //
                    .addAggregation(
                            ImmutableGeoBoundsAggregation.builder() //
                                    .name("viewport") //
                                    .field("location") //
                                    .build()) //
                    .build();

            // execute aggregation
            final SearchResponse response = assertSearchFinds(index, search, 2);
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(48.86111099738628d, json, "viewport.bounds.top_left.lat");
            JsonAssert.assertJsonPartEquals(2.3269999679178d, json, "viewport.bounds.top_left.lon");
            JsonAssert.assertJsonPartEquals(48.85999997612089d, json, "viewport.bounds.bottom_right.lat");
            JsonAssert.assertJsonPartEquals(2.3363889567553997d, json, "viewport.bounds.bottom_right.lon");
        });
    }

    @Test
    public void testGeoCentroid() throws IOException {
        final Mapping mapping = createGeoPointMapping();

        withIndex(mapping, index -> {
            indexGeoPointDocuments(index);

            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutableGeoCentroidAggregation.builder() //
                                    .name("centroid") //
                                    .field("location") //
                                    .build()) //
                    .build();

            // execute aggregation
            final SearchResponse response = assertSearchFinds(index, search, 6);
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(51.00982963107526d, json, "centroid.location.lat");
            JsonAssert.assertJsonPartEquals(3.9662130922079086d, json, "centroid.location.lon");
            JsonAssert.assertJsonPartEquals(6, json, "centroid.count");
        });
    }

    @Test
    public void testMax() throws IOException {
        withPeople(10, (index, people) -> {
            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutableMaxAggregation.builder() //
                                    .name("age") //
                                    .field("age") //
                                    .build()) //
                    .build();

            // check response
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(max(people), json, "age.value");
        });
    }

    @Test
    public void testMin() throws IOException {
        withPeople(10, (index, people) -> {
            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutableMinAggregation.builder() //
                                    .name("age") //
                                    .field("age") //
                                    .build()) //
                    .build();

            // check response
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(min(people), json, "age.value");
        });
    }

    @Test
    public void testPercentiles() throws IOException {
        withPeople(50, (index, people) -> {
            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutablePercentilesAggregation.builder() //
                                    .name("age") //
                                    .field("age") //
                                    .addPercent(1, 5, 25, 50, 75, 95, 99) //
                                    .numberOfSignificantValueDigits(3) //
                                    .keyed(true) //
                                    .build()) //
                    .build();

            // check response
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonNodePresent(json, "age.values.1\\.0");
            JsonAssert.assertJsonNodePresent(json, "age.values.5\\.0");
            JsonAssert.assertJsonNodePresent(json, "age.values.25\\.0");
            JsonAssert.assertJsonNodePresent(json, "age.values.50\\.0");
            JsonAssert.assertJsonNodePresent(json, "age.values.75\\.0");
            JsonAssert.assertJsonNodePresent(json, "age.values.95\\.0");
            JsonAssert.assertJsonNodePresent(json, "age.values.99\\.0");

            final Map age = (Map) response.getAggregations().get("age");
            @SuppressWarnings("unchecked")
            final Map<String, Double> values = (Map<String, Double>) age.get("values");

            testPercentileRankAggregation(index, people, values.values());
        });
    }

    @Test
    public void testStats() throws IOException {
        withPeople(10, (index, people) -> {
            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutableStatsAggregation.builder() //
                                    .name("age") //
                                    .field("age") //
                                    .build()) //
                    .build();

            // execute average aggregation
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(people.size(), json, "age.count");
            JsonAssert.assertJsonPartEquals(min(people), json, "age.min");
            JsonAssert.assertJsonPartEquals(max(people), json, "age.max");
            JsonAssert.assertJsonPartEquals(avg(people), json, "age.avg");
            JsonAssert.assertJsonPartEquals(sum(people), json, "age.sum");
        });
    }

    @Test
    public void testSum() throws IOException {
        withPeople(10, (index, people) -> {
            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutableSumAggregation.builder() //
                                    .name("age") //
                                    .field("age") //
                                    .build()) //
                    .build();

            // check response
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(sum(people), json, "age.value");
        });
    }

    @Test
    public void testTerms() throws IOException {
        withPeople(20, (index, people) -> {
            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutableTermsAggregation.builder() //
                                    .name("genders") //
                                    .field("gender") //
                                    .minDocCount(1L) //
                                    .shardMinDocCount(1L) //
                                    .size(Integer.MAX_VALUE) //
                                    .addInclude(Gender.MALE.name(), Gender.FEMALE.name()) //
                                    .addOrder(BucketOrder.KEY_DESC) //
                                    .showTermDocCountError(true) //
                                    .build()) //
                    .build();

            // execute terms aggregation
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(0, json, "genders.doc_count_error_upper_bound");
            JsonAssert.assertJsonPartEquals(0, json, "genders.sum_other_doc_count");

            // terms should appear in this order, because we sorted terms in descending order
            JsonAssert.assertJsonPartEquals("MALE", json, "genders.buckets[0].key");
            JsonAssert.assertJsonPartEquals("FEMALE", json, "genders.buckets[1].key");

            // we don't know what counts will be, but they should exist
            JsonAssert.assertJsonNodePresent(json, "genders.buckets[0].doc_count");
            JsonAssert.assertJsonNodePresent(json, "genders.buckets[1].doc_count");

            // we shouldn't have any errors with single shard used for testing
            JsonAssert.assertJsonPartEquals(0, json, "genders.buckets[0].doc_count_error_upper_bound");
            JsonAssert.assertJsonPartEquals(0, json, "genders.buckets[1].doc_count_error_upper_bound");
        });
    }

    @Test
    public void testValueCount() throws IOException {
        withPeople(50, (index, people) -> {
            final ImmutableSearch search = ImmutableSearch.builder() //
                    .size(0) //
                    .addAggregation(
                            ImmutableValueCountAggregation.builder() //
                                    .name("age") //
                                    .field("age") //
                                    .build()) //
                    .build();

            // check response
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final String json = JacksonUtils.toString(response.getAggregations(), true);
            JsonAssert.assertJsonPartEquals(count(people), json, "age.value");
        });
    }

    private double avg(final List<Person> people) {
        return people.stream() //
                .filter(p -> p.getAge() != null) //
                .mapToDouble(p -> p.getAge()) //
                .average() //
                .getAsDouble();
    }

    private int count(final List<Person> people) {
        return (int) people.stream() //
                .filter(p -> p.getAge() != null) //
                .map(p -> p.getAge()) //
                .count();
    }

    private int countDistinct(final List<Person> people) {
        return (int) people.stream() //
                .filter(p -> p.getAge() != null) //
                .map(p -> p.getAge()) //
                .distinct() //
                .count();
    }

    private ImmutableMapping createGeoPointMapping() {
        return ImmutableMapping.builder() //
                .dynamic(STRICT) //
                .addField(
                        ImmutableField.builder() //
                                .name("location") //
                                .type(Type.GEO_POINT) //
                                .build()) //
                .addField(
                        ImmutableField.builder() //
                                .name("city") //
                                .type(Type.TEXT) //
                                .build()) //
                .addField(
                        ImmutableField.builder() //
                                .name("name") //
                                .type(Type.TEXT) //
                                .build()) //
                .build();
    }

    private void indexGeoPointDocuments(final Index index) {
        assertIndexDocuments(
                index,
                DEFAULT_TYPE, //
                "{\"location\": \"52.374081,4.912350\", \"city\": \"Amsterdam\", \"name\": \"NEMO Science Museum\"}",
                "{\"location\": \"52.369219,4.901618\", \"city\": \"Amsterdam\", \"name\": \"Museum Het Rembrandthuis\"}",
                "{\"location\": \"52.371667,4.914722\", \"city\": \"Amsterdam\", \"name\": \"Nederlands Scheepvaartmuseum\"}",
                "{\"location\": \"51.222900,4.405200\", \"city\": \"Antwerp\", \"name\": \"Letterenhuis\"}",
                "{\"location\": \"48.861111,2.336389\", \"city\": \"Paris\", \"name\": \"Musée du Louvre\"}",
                "{\"location\": \"48.860000,2.327000\", \"city\": \"Paris\", \"name\": \"Musée d'Orsay\"}");
    }

    private double max(final List<Person> people) {
        return people.stream() //
                .filter(p -> p.getAge() != null) //
                .mapToDouble(p -> p.getAge()) //
                .max() //
                .getAsDouble();
    }

    private double min(final List<Person> people) {
        return people.stream() //
                .filter(p -> p.getAge() != null) //
                .mapToDouble(p -> p.getAge()) //
                .min() //
                .getAsDouble();
    }

    private double sum(final List<Person> people) {
        return people.stream() //
                .filter(p -> p.getAge() != null) //
                .mapToDouble(p -> p.getAge()) //
                .sum();
    }

    private void testPercentileRankAggregation(
            final Index index,
            final List<Person> people,
            final Collection<Double> values) throws JsonProcessingException {
        final ImmutableSearch search = ImmutableSearch.builder() //
                .size(0) //
                .addAggregation(
                        ImmutablePercentileRanksAggregation.builder() //
                                .name("age") //
                                .field("age") //
                                .addAllValues(values) //
                                .build()) //
                .build();

        // check response
        final SearchResponse response = assertSearchFinds(index, search, people.size());
        final String json = JacksonUtils.toString(response.getAggregations(), true);
        for (final Double val : values) {
            JsonAssert.assertJsonNodePresent(json, "age.values." + val.toString().replaceAll("\\.", "\\\\."));
        }
    }

    protected void assertIndexDocuments(final Index index, final String type, final String... rows) {
        int id = 0;
        for (final String row : rows) {
            assertSuccessful( //
                    elasticClient.indexDocument(
                            index.getName(), //
                            type,
                            Integer.toString(++id),
                            row));
        }
    }
}
