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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.arakelian.elastic.model.search.ImmutableBoolQuery;
import com.arakelian.elastic.model.search.ImmutableExistsQuery;
import com.arakelian.elastic.model.search.ImmutableFuzzyQuery;
import com.arakelian.elastic.model.search.ImmutableIdsQuery;
import com.arakelian.elastic.model.search.ImmutableMatchQuery;
import com.arakelian.elastic.model.search.ImmutablePrefixQuery;
import com.arakelian.elastic.model.search.ImmutableQueryStringQuery;
import com.arakelian.elastic.model.search.ImmutableRangeQuery;
import com.arakelian.elastic.model.search.ImmutableRegexpQuery;
import com.arakelian.elastic.model.search.ImmutableSearch;
import com.arakelian.elastic.model.search.ImmutableTermsQuery;
import com.arakelian.elastic.model.search.ImmutableWildcardQuery;
import com.arakelian.elastic.model.search.Operator;
import com.arakelian.elastic.model.search.QueryStringQuery;
import com.arakelian.elastic.model.search.RegexpFlag;
import com.arakelian.elastic.model.search.Search;
import com.arakelian.faker.model.Person;

public class ElasticClientSearchTest extends AbstractElasticDockerTest {
    public ElasticClientSearchTest(final String version) throws Exception {
        super(version);
    }

    @Test
    public void testBoolQuery() throws IOException {
        withPeople(10, (index, people) -> {
            final Person person = people.get(0);

            final QueryStringQuery query = ImmutableQueryStringQuery.builder() //
                    .defaultField("lastName") //
                    .queryString(person.getLastName()) //
                    .build();

            assertSearchFindsPerson(
                    index,
                    ImmutableSearch.builder() //
                            .query(
                                    ImmutableBoolQuery.builder() //
                                            .addMustClause(query) //
                                            .addShouldClause(query, query, query) //
                                            .minimumShouldMatch("1") // .
                                            .build()) //
                            .build(),
                    person);
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
            // pick first person
            final Person person = people.get(0);

            // build regular expression that matches last name
            final String lastname = person.getLastName().toLowerCase();
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
                    person);
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
