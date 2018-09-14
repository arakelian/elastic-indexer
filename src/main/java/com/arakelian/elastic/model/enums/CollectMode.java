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
 * Deferring calculation of child aggregations
 *
 * For fields with many unique terms and a small number of required results it can be more efficient
 * to delay the calculation of child aggregations until the top parent-level aggs have been pruned.
 * Ordinarily, all branches of the aggregation tree are expanded in one depth-first pass and only
 * then any pruning occurs. In some scenarios this can be very wasteful and can hit memory
 * constraints.
 *
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-terms-aggregation.html#_collect_mode">Collect
 *      Mode</a>
 */
public enum CollectMode {
    BREADTH_FIRST, DEPTH_FIRST;
}
