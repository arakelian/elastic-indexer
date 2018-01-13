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

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.arakelian.elastic.utils.ElasticClientUtils;
import com.arakelian.jackson.utils.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(Parameterized.class)
public abstract class AbstractElasticModelTest {
    @Parameters(name = "version-{0}")
    public static Object[] data() {
        return new Object[] { //
                "5", //
                "5.2", //
                "5.3", //
                "5.4", //
                "5.5", //
                "5.6", //
                "6", //
                "6.1" };
    }

    protected final ObjectMapper objectMapper;
    protected final VersionComponents version;

    public AbstractElasticModelTest(final String number) {
        version = VersionComponents.of(number);
        Assert.assertTrue(version.getMajor() >= 5);

        final ObjectMapper objectMapper = JacksonUtils.getObjectMapper();
        this.objectMapper = objectMapper;
        ElasticClientUtils.configure(objectMapper, version);
    }
}
