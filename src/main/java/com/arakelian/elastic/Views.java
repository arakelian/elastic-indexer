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

public class Views {
    public static class Elastic {
        public static class Version5 extends Elastic {
            public static class Version52 extends Version5 {
            }

            public static class Version53 extends Version52 {
            }

            public static class Version54 extends Version53 {
            }

            public static class Version55 extends Version54 {
            }

            public static class Version56 extends Version55 {
            }
        }

        public static class Version6 extends Elastic {
            public static class Version61 extends Version6 {
            }
        }
    }

    public static class Enhancement {
    }
}
