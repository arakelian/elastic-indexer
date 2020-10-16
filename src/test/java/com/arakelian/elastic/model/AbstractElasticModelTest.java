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

package com.arakelian.elastic.model;

import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arakelian.elastic.AbstractElasticDockerTest;
import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractElasticModelTest {
    /** Logger **/
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractElasticDockerTest.class);

    public static Object[] data() {
        return new Object[] { //
                "5", //
                "5.2", //
                "5.3", //
                "5.4", //
                "5.5", //
                "5.6", //
                "6", //
                "6.1", //
                "6.2", //
                "6.3", //
                "6.4", //
                "6.5", //
                "6.6", //
                "6.7", //
                "6.8", //
                "7", //
                "7.1", //
                "7.2", //
                "7.3", //
                "7.9.2" };
    }

    protected ObjectMapper objectMapper;
    protected VersionComponents version;

    protected void configure(final String number) {
        LOGGER.info("Configuring for Elastic version {}", number);
        version = VersionComponents.of(number);
        Assertions.assertTrue(version.getMajor() >= 5);

        final ObjectMapper objectMapper = JacksonUtils.getObjectMapper();
        this.objectMapper = objectMapper;
        ElasticClientUtils.configure(objectMapper, version);
    }
}
