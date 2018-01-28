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

package com.arakelian.elastic.model.aggs.bucket;

public enum ExecutionHint {
    /**
     * GLOBAL_ORDINALS is the default option for keyword fields, it uses global ordinals to
     * allocates buckets dynamically so memory usage is linear to the number of values of the
     * documents that are part of the aggregation scope.
     **/
    GLOBAL_ORDINALS,

    /**
     * MAP should only be considered when very few documents match a query. Otherwise the
     * ordinals-based execution mode is significantly faster. By default, map is only used when
     * running an aggregation on scripts, since they don’t have ordinals.
     */
    MAP;
}
