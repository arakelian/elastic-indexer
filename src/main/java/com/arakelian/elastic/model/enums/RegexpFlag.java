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

import com.arakelian.elastic.model.search.Query;

/**
 * Regular expression flags that can be applied to {@link Query}.
 *
 * @see <a href=
 *      "http://lucene.apache.org/core/4_9_0/core/org/apache/lucene/util/automaton/RegExp.html">Lucene
 *      documentation</a>
 */
public enum RegexpFlag {
    /**
     * Enables intersection of the form: <code>&lt;expression&gt; &amp; &lt;expression&gt;</code>
     */
    INTERSECTION,

    /**
     * Enables complement expression of the form: <code>~&lt;expression&gt;</code>
     */
    COMPLEMENT,

    /**
     * Enables empty language expression: <code>#</code>
     */
    EMPTY,

    /**
     * Enables any string expression: <code>@</code>
     */
    ANYSTRING,

    /**
     * Enables numerical interval expression: <code>&lt;n-m&gt;</code>
     */
    INTERVAL,

    /**
     * Disables all available option flags
     */
    NONE
}
