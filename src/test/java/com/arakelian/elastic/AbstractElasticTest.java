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

import org.junit.Before;
import org.junit.ClassRule;

public class AbstractElasticTest {
    /** End of life 2018-06-08 **/
    public static final String ELASTICSEARCH__5_1_1 = "elasticsearch:5.1.1";

    /** End of life 2019-03-11 **/
    public static final String ELASTICSEARCH__5_6_4 = "elasticsearch:5.6.4";

    public static final String ELASTICSEARCH__6_1_0 = "docker.elastic.co/elasticsearch/elasticsearch:6.1.0";

    @ClassRule
    public static final ElasticDockerRule elastic = new ElasticDockerRule("elastic-test",
            ELASTICSEARCH__6_1_0, 9200);

    protected ElasticClient elasticClient;
    protected ElasticTestUtils elasticTestUtils;
    protected int majorVersion;

    @Before
    public void setupContext() {
        this.elasticClient = elastic.getElasticClient();
        this.elasticTestUtils = new ElasticTestUtils(elasticClient);
        this.majorVersion = elastic.getAbout().getVersion().getMajor();
    }
}
