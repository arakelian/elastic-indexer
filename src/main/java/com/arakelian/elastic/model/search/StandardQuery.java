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

package com.arakelian.elastic.model.search;

import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface StandardQuery extends Query {
    @Nullable
    public Float getBoost();

    /**
     * Returns the name of the query
     *
     * @return name of the query
     * @see <a href=
     *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-named-queries-and-filters.html">Named
     *      Queries</a>
     */
    @Nullable
    @JsonProperty("_name")
    public String getName();

    @Value.Derived
    @JsonIgnore
    @Value.Auxiliary
    public default boolean hasStandardDefaults() {
        return getName() == null && getBoost() == null;
    }
}
