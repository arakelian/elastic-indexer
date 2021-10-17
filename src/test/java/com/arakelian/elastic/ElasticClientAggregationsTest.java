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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.arakelian.elastic.model.Field;
import com.arakelian.elastic.model.ImmutableField;
import com.arakelian.elastic.model.ImmutableMapping;
import com.arakelian.elastic.model.Index;
import com.arakelian.elastic.model.Mapping;
import com.arakelian.elastic.model.VersionComponents;
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
import com.arakelian.jackson.MapPath;
import com.arakelian.jackson.model.GeoPoint;

public class ElasticClientAggregationsTest extends AbstractElasticDockerTest {
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
                                .type(Field.Type.GEO_POINT) //
                                .build()) //
                .addField(
                        ImmutableField.builder() //
                                .name("city") //
                                .type(Field.Type.TEXT) //
                                .build()) //
                .addField(
                        ImmutableField.builder() //
                                .name("name") //
                                .type(Field.Type.TEXT) //
                                .build()) //
                .build();
    }

    private void indexGeoPointDocuments(final Index index) {
        assertIndexDocuments(
                index,
                _DOC, //
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
            final MapPath agg = response.getAggregations().get("age");
            assertEquals(avg(people), agg.getDouble("value"), 0.001d);
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
            final MapPath agg = response.getAggregations().get("age");
            assertEquals(countDistinct(people), agg.getDouble("value"), 0.001d);
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
            final MapPath agg = response.getAggregations().get("age");
            assertEquals(people.size(), agg.getDouble("count"), 0.001d);
            assertEquals(min(people), agg.getDouble("min"), 0.001d);
            assertEquals(max(people), agg.getDouble("max"), 0.001d);
            assertEquals(avg(people), agg.getDouble("avg"), 0.001d);
            assertEquals(sum(people), agg.getDouble("sum"), 0.001d);
            assertNotNull(agg.getObject("sum_of_squares"));
            assertNotNull(agg.getObject("variance"));
            assertNotNull(agg.getObject("std_deviation"));
            assertNotNull(agg.getObject("std_deviation_bounds/upper"));
            assertNotNull(agg.getObject("std_deviation_bounds/lower"));
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
            final MapPath agg = response.getAggregations().get("viewport");

            assertEquals(48.861d, agg.getDouble("bounds/top_left/lat"), 0.001d);
            assertEquals(2.326d, agg.getDouble("bounds/top_left/lon"), 0.001d);
            assertEquals(GeoPoint.of(48.861d, 2.327d), agg.getGeoPoint("bounds/top_left").round(3));

            assertEquals(48.859d, agg.getDouble("bounds/bottom_right/lat"), 0.001d);
            assertEquals(2.336d, agg.getDouble("bounds/bottom_right/lon"), 0.001d);
            assertEquals(GeoPoint.of(48.86d, 2.336d), agg.getGeoPoint("bounds/bottom_right").round(3));
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
            final MapPath agg = response.getAggregations().get("centroid");
            assertEquals(51.009d, agg.getDouble("location/lat"), 0.001d);
            assertEquals(3.966d, agg.getDouble("location/lon"), 0.001d);

            // started version 5.5
            final VersionComponents version = elasticClient.getVersion();
            if (version.atLeast(5, 5, 0)) {
                assertEquals(6d, agg.getDouble("count"), 0.001d);
            }
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
            final MapPath agg = response.getAggregations().get("age");
            assertEquals(max(people), agg.getDouble("value"), 0.001d);
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
            final MapPath agg = response.getAggregations().get("age");
            assertEquals(min(people), agg.getDouble("value"), 0.001d);
        });
    }

    private void testPercentileRankAggregation(
            final Index index,
            final List<Person> people,
            final Collection<Double> values) {
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
        final MapPath agg = response.getAggregations().get("age");
        for (final Double val : values) {
            final String key = "values." + val.toString();
            assertTrue(agg.hasProperty(key), "Cannot find property: " + key);
        }
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
                                    .addPercents(1, 5, 25, 50, 75, 95, 99) //
                                    .numberOfSignificantValueDigits(3) //
                                    .keyed(true) //
                                    .build()) //
                    .build();

            // check response
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final MapPath agg = response.getAggregations().get("age");
            assertTrue(agg.hasProperty("values.1.0"));
            assertTrue(agg.hasProperty("values.5.0"));
            assertTrue(agg.hasProperty("values.25.0"));
            assertTrue(agg.hasProperty("values.50.0"));
            assertTrue(agg.hasProperty("values.75.0"));
            assertTrue(agg.hasProperty("values.95.0"));
            assertTrue(agg.hasProperty("values.99.0"));

            @SuppressWarnings("unchecked")
            final Map<String, Double> values = (Map<String, Double>) agg.getObject("values");

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
            final MapPath agg = response.getAggregations().get("age");
            assertEquals(people.size(), agg.getDouble("count"), 0.001d);
            assertEquals(min(people), agg.getDouble("min"), 0.001d);
            assertEquals(max(people), agg.getDouble("max"), 0.001d);
            assertEquals(avg(people), agg.getDouble("avg"), 0.001d);
            assertEquals(sum(people), agg.getDouble("sum"), 0.001d);
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
            final MapPath agg = response.getAggregations().get("age");
            assertEquals(sum(people), agg.getDouble("value"), 0.001d);
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
                                    .addIncludeValues(Gender.MALE.name(), Gender.FEMALE.name()) //
                                    .addOrder(BucketOrder.KEY_DESC) //
                                    .showTermDocCountError(true) //
                                    .build()) //
                    .build();

            // execute terms aggregation
            final SearchResponse response = assertSearchFinds(index, search, people.size());
            final MapPath agg = response.getAggregations().get("genders");
            assertEquals(0, agg.getDouble("doc_count_error_upper_bound"), 0.001d);
            assertEquals(0, agg.getDouble("sum_other_doc_count"), 0.001d);

            // terms should appear in this order, because we sorted terms in descending order
            final List buckets = agg.getList("buckets");
            assertEquals(2, buckets.size());

            final MapPath male = MapPath.of((Map) buckets.get(0));
            final MapPath female = MapPath.of((Map) buckets.get(1));

            assertEquals("MALE", male.getString("key"));
            assertEquals("FEMALE", female.getString("key"));

            // we don't know what counts will be, but they should exist
            assertTrue(male.hasProperty("doc_count"));
            assertTrue(female.hasProperty("doc_count"));

            // we shouldn't have any errors with single shard used for testing
            assertEquals(0, male.getDouble("doc_count_error_upper_bound"), 0.001d);
            assertEquals(0, female.getDouble("doc_count_error_upper_bound"), 0.001d);
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
            final MapPath agg = response.getAggregations().get("age");
            assertEquals(count(people), agg.getDouble("value"), 0.001d);
        });
    }
}
