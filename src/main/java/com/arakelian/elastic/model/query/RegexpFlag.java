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

package com.arakelian.elastic.model.query;

public enum RegexpFlag {
    /**
     * Enables intersection of the form: <tt>&lt;expression&gt; &amp; &lt;expression&gt;</tt>
     */
    INTERSECTION,

    /**
     * Enables complement expression of the form: <tt>~&lt;expression&gt;</tt>
     */
    COMPLEMENT,

    /**
     * Enables empty language expression: <tt>#</tt>
     */
    EMPTY,

    /**
     * Enables any string expression: <tt>@</tt>
     */
    ANYSTRING,

    /**
     * Enables numerical interval expression: <tt>&lt;n-m&gt;</tt>
     */
    INTERVAL,

    /**
     * Disables all available option flags
     */
    NONE
}
