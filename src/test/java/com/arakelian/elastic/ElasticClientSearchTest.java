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

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.arakelian.elastic.model.enums.DistanceType;
import com.arakelian.elastic.model.enums.GeoShapeType;
import com.arakelian.elastic.model.enums.Operator;
import com.arakelian.elastic.model.enums.RegexpFlag;
import com.arakelian.elastic.model.enums.ShapeRelation;
import com.arakelian.elastic.model.enums.SpatialStrategy;
import com.arakelian.elastic.model.enums.ValidationMethod;
import com.arakelian.elastic.model.search.GeoBoundingBoxQuery;
import com.arakelian.elastic.model.search.GeoDistanceQuery;
import com.arakelian.elastic.model.search.GeoPolygonQuery;
import com.arakelian.elastic.model.search.GeoShapeQuery;
import com.arakelian.elastic.model.search.Highlighter.Field;
import com.arakelian.elastic.model.search.ImmutableBoolQuery;
import com.arakelian.elastic.model.search.ImmutableExistsQuery;
import com.arakelian.elastic.model.search.ImmutableFuzzyQuery;
import com.arakelian.elastic.model.search.ImmutableGeoBoundingBoxQuery;
import com.arakelian.elastic.model.search.ImmutableGeoDistanceQuery;
import com.arakelian.elastic.model.search.ImmutableGeoPolygonQuery;
import com.arakelian.elastic.model.search.ImmutableGeoShapeQuery;
import com.arakelian.elastic.model.search.ImmutableHighlight;
import com.arakelian.elastic.model.search.ImmutableIdsQuery;
import com.arakelian.elastic.model.search.ImmutableItem;
import com.arakelian.elastic.model.search.ImmutableMatchQuery;
import com.arakelian.elastic.model.search.ImmutableMoreLikeThisQuery;
import com.arakelian.elastic.model.search.ImmutablePrefixQuery;
import com.arakelian.elastic.model.search.ImmutableQueryStringQuery;
import com.arakelian.elastic.model.search.ImmutableRangeQuery;
import com.arakelian.elastic.model.search.ImmutableRegexpQuery;
import com.arakelian.elastic.model.search.ImmutableSearch;
import com.arakelian.elastic.model.search.ImmutableShape;
import com.arakelian.elastic.model.search.ImmutableTermsQuery;
import com.arakelian.elastic.model.search.ImmutableWildcardQuery;
import com.arakelian.elastic.model.search.MoreLikeThisQuery;
import com.arakelian.elastic.model.search.QueryStringQuery;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.faker.model.Person;
import com.arakelian.jackson.model.Coordinate;
import com.arakelian.jackson.model.GeoPoint;

public class ElasticClientSearchTest extends AbstractElasticDockerTest {
    public ElasticClientSearchTest(final String version) throws Exception {
        super(version);
    }

    @Test
    public void testBoolQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final Person person = people.get(0);

            final QueryStringQuery query = ImmutableQueryStringQuery.builder() //
                    .name("my_querystring_filter") //
                    .boost(2.0f) //
                    .defaultField("lastName") //
                    .queryString(person.getLastName()) //
                    .build();

            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableBoolQuery.builder() //
                                            .addMustClause(query) //
                                            .addShouldClauses(query, query, query) //
                                            .minimumShouldMatch("1") // .
                                            .build()) //
                            .build(),
                    person);
        });
    }

    @Test
    public void testGeoShapeQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final GeoShapeQuery query = ImmutableGeoShapeQuery.builder() //
                    .name("my_geoshape_query") //
                    .boost(2.0f) //
                    .fieldName("shape") //
                    .shape(
                            ImmutableShape.builder() //
                                    .type(GeoShapeType.LINESTRING) //
                                    .addCoordinate(Coordinate.of(102.0, 2.0)) //
                                    .addCoordinate(Coordinate.of(103.0, 2.0)) //
                                    .build())
                    .relation(ShapeRelation.CONTAINS) //
                    .strategy(SpatialStrategy.RECURSIVE) //
                    .build();

            // just a syntax check, no people should be found
            assertSearchFinds(
                    index,
                    ImmutableSearch.builder() //
                            .query(query) //
                            .build(),
                    0);
        });
    }

    @Test
    public void testGeoPolygonQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // bermuda triangle
            final GeoPolygonQuery query = ImmutableGeoPolygonQuery.builder() //
                    .name("my_geopolygon_query") //
                    .boost(2.0f) //
                    .fieldName("location") //
                    .addPoint(GeoPoint.of(-64.73, 32.31)) //
                    .addPoint(GeoPoint.of(-80.19, 25.76)) //
                    .addPoint(GeoPoint.of(-66.09, 18.43)) //
                    .addPoint(GeoPoint.of(-64.73, 32.31)) //
                    .validationMethod(ValidationMethod.IGNORE_MALFORMED) //
                    .build();

            // just a syntax check, no people should be found
            assertSearchFinds(
                    index,
                    ImmutableSearch.builder() //
                            .query(query) //
                            .build(),
                    0);
        });
    }

    @Test
    public void testMoreLikeThisQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // bermuda triangle
            final MoreLikeThisQuery query = ImmutableMoreLikeThisQuery.builder() //
                    .name("my_morelikethis") //
                    .boost(2.0f) //
                    .addLikeText("four score and seven years ago") //
                    .addLikeItem(ImmutableItem.builder().id("id").build()) //
                    .minTermFrequency(10) //
                    .minDocFrequency(10) //
                    .maxDocFrequency(0) //
                    .minWordLength(10) //
                    .maxWordLength(0) //
                    .maxQueryTerms(0) //
                    .minimumShouldMatch("100%") //
                    .build();

            // just a syntax check, no people should be found
            assertSearchFinds(
                    index,
                    ImmutableSearch.builder() //
                            .query(query) //
                            .build(),
                    0);
        });
    }

    @Test
    public void testGeoBoundingBoxQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final GeoBoundingBoxQuery tlbr = ImmutableGeoBoundingBoxQuery.builder() //
                    .name("my_geoboundingbox") //
                    .boost(2.0f) //
                    .fieldName("location") //
                    .topLeft(GeoPoint.of(40.73, -74.1)) //
                    .bottomRight(GeoPoint.of(40.01, -71.12)) //
                    .validationMethod(ValidationMethod.IGNORE_MALFORMED) //
                    .type(GeoBoundingBoxQuery.Type.MEMORY) //
                    .build();

            // just a syntax check, no people should be found
            assertSearchFinds(
                    index,
                    ImmutableSearch.builder() //
                            .query(tlbr) //
                            .build(),
                    0);

            final GeoBoundingBoxQuery trbl = ImmutableGeoBoundingBoxQuery.builder() //
                    .name("my_geoboundingbox") //
                    .boost(2.0f) //
                    .fieldName("location") //
                    .topRight(GeoPoint.of(40.73, 40.01)) //
                    .bottomLeft(GeoPoint.of(-74.1, -71.12)) //
                    .validationMethod(ValidationMethod.IGNORE_MALFORMED) //
                    .type(GeoBoundingBoxQuery.Type.MEMORY) //
                    .build();

            // just a syntax check, no people should be found
            assertSearchFinds(
                    index,
                    ImmutableSearch.builder() //
                            .query(trbl) //
                            .build(),
                    0);
        });
    }

    @Test
    public void testGeoDistanceQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final GeoDistanceQuery tlbr = ImmutableGeoDistanceQuery.builder() //
                    .name("my_geodistance_query") //
                    .boost(2.0f) //
                    .fieldName("location") //
                    .distance("12km") //
                    .point(GeoPoint.of(40.73, -74.1)) //
                    .validationMethod(ValidationMethod.IGNORE_MALFORMED) //
                    .distanceType(DistanceType.ARC) //
                    .build();

            // just a syntax check, no people should be found
            assertSearchFinds(
                    index,
                    ImmutableSearch.builder() //
                            .query(tlbr) //
                            .build(),
                    0);
        });
    }

    @Test
    public void testExistsQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // all people should have a last name
            assertSearchFinds(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableExistsQuery.builder() //
                                            .name("my_exists_filter") //
                                            .boost(2.0f) //
                                            .fieldName("lastName") //
                                            .build()) //
                            .build(),
                    10);

            // no people should have a value for this field
            assertSearchFinds(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableExistsQuery.builder() //
                                            .name("my_exists_filter") //
                                            .boost(2.0f) //
                                            .fieldName(ALWAYS_EMPTY_FIELD) //
                                            .build()) //
                            .build(),
                    0);
        });
    }

    @Test
    public void testFuzzyQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // pick first person
            final Person person = people.get(0);

            // build regular expression that matches last name
            int fuzziness = 0;
            final String lastname = person.getLastName().toLowerCase();
            final StringBuilder fuzzy = new StringBuilder(lastname);
            for (int i = 0, length = lastname.length(); i + 1 < length && fuzziness < 2; i += 2) {
                // swap letters
                final char ch = lastname.charAt(i);
                final char ch2 = lastname.charAt(i + 1);
                fuzzy.setCharAt(i, ch2);
                fuzzy.setCharAt(i + 1, ch);
                fuzziness++;
            }

            // find person using fuzzy (edit distance) query
            assertSearchFindsOneOf(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableFuzzyQuery.builder() //
                                            .fieldName("lastName") //
                                            .fuzziness(Integer.toString(fuzziness)) //
                                            .transpositions(true) //
                                            .value(fuzzy.toString()) //
                                            .build()) //
                            .build(),
                    person);
        });
    }

    @Test
    public void testIdsQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // get first person
            final Person person = people.get(0);

            // find them using their ID
            final Search search = ImmutableSearch.builder() //
                    .query(
                            ImmutableIdsQuery.builder() //
                                    .name("ids_query") //
                                    .addValue(person.getId()) //
                                    .build()) //
                    .build();

            // verify returned data matches
            assertSearchFindsPerson(index, search, person);
        });
    }

    @Test
    public void testMatchQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // pick a person
            final Person person = people.get(0);

            // create a phrase that contains their last name
            final String phase = "find a person whose last name is " + person.getLastName();

            // search for a person using the phrase; the phrase should have been analyzed and we
            // should have a match on the last name
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableMatchQuery.builder() //
                                            .fieldName("lastName") //
                                            .value(phase) //
                                            .operator(Operator.OR) //
                                            .build()) //
                            .highlight(
                                    ImmutableHighlight.builder() //
                                            .addField(Field.of("lastName")) //
                                            .build())
                            .build(),
                    person);
        });
    }

    @Test
    public void testPrefixQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // pick first person
            final Person person = people.get(0);

            // build partial last name
            final String lastname = person.getLastName().toLowerCase();
            final String partialLastname = StringUtils.left(lastname, lastname.length() - 1);

            // use prefix query to find person
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutablePrefixQuery.builder() //
                                            .fieldName("lastName") //
                                            .value(partialLastname) //
                                            .build()) //
                            .build(),
                    person);
        });
    }

    @Test
    public void testQueryStringQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final Person person = people.get(0);

            // use default_field
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableQueryStringQuery.builder() //
                                            .defaultField("lastName") //
                                            .queryString(person.getLastName()) //
                                            .build()) //
                            .build(),
                    person);

            // use complex query
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableQueryStringQuery.builder() //
                                            .queryString("lastName:" + person.getLastName()) //
                                            .boost(2.0f) //
                                            .name("name") //
                                            .build()) //
                            .build(),
                    person);
        });
    }

    @Test
    public void testRangeQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // pick person
            final Person person = people.get(0);

            // find person using age range
            assertSearchFindsOneOf(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableRangeQuery.builder() //
                                            .fieldName("age") //
                                            .lower(person.getAge() - 1) //
                                            .upper(person.getAge() + 1) //
                                            .build()) //
                            .build(),
                    person);

            // find person using date range
            assertSearchFindsOneOf(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableRangeQuery.builder() //
                                            .fieldName("birthdate") //
                                            .lower(person.getBirthdate().minusYears(1)) //
                                            .upper(person.getBirthdate().plusYears(1)) //
                                            .build()) //
                            .build(),
                    person);
        });
    }

    @Test
    public void testRegexpQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // pick person with longest name
            Optional<Person> person = people.stream() //
                    .max(Comparator.comparingInt(p -> p.getLastName().length()));

            // build regular expression that matches last name
            final String lastname = person.get().getLastName().toLowerCase();
            final StringBuilder regexp = new StringBuilder(lastname);
            for (int i = 0, length = lastname.length(); i < length; i += 2) {
                regexp.setCharAt(i, '.'); // matches any character
            }

            // find person using regular expression
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableRegexpQuery.builder() //
                                            .fieldName("lastName") //
                                            .addFlag(RegexpFlag.NONE) //
                                            .value(regexp.toString()) //
                                            .build()) //
                            .build(),
                    person.get());
        });
    }

    @Test
    public void testTermsQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final Person person = people.get(0);

            // try simple search
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableTermsQuery.builder() //
                                            .fieldName("lastName") //
                                            .addValue(person.getLastName().toLowerCase()) //
                                            .build()) //
                            .build(),
                    person);

            // try again with boost and named query
            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableTermsQuery.builder() //
                                            .fieldName("lastName") //
                                            .addValue(person.getLastName().toLowerCase()) //
                                            .boost(2.0f) //
                                            .name("last") //
                                            .build()) //
                            .build(),
                    person);
        });
    }

    @Test
    public void testWildcardQuery() throws IOException {
        withPeople(10, (index, people) -> {
            // standard analyzer forces to lowercase
            final Person person = people.get(0);
            final String lastName = person.getLastName().toLowerCase();
            final Search search = ImmutableSearch.builder() //
                    .query(
                            ImmutableWildcardQuery.builder() //
                                    .fieldName("lastName") //
                                    .value(StringUtils.left(lastName, lastName.length() - 1) + "?") //
                                    .build()) //
                    .build();

            assertSearchFindsPerson(index, search, person);
        });
    }
}
