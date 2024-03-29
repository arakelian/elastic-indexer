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

package com.arakelian.elastic.model.enums;

/**
 * Multi-term rewrite types
 * 
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-term-rewrite.html">Multiterm
 *      Rewrite</a>
 */
public enum Rewrite {
    CONSTANT_SCORE, //
    SCORING_BOOLEAN, //
    CONSTANT_SCORE_BOOLEAN, //
    TOP_TERMS_N, //
    TOP_TERMS_BOOST_N, //
    TOP_TERMS_BLENDED_FREQS_N //
}
