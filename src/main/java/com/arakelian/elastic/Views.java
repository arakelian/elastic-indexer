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

import com.arakelian.elastic.Views.Elastic.Version5;
import com.arakelian.elastic.Views.Elastic.Version6;
import com.arakelian.elastic.Views.Elastic.Version7;
import com.arakelian.elastic.model.VersionComponents;

public class Views {
    public static abstract class Elastic implements HasVersion {
        public static class Version5 extends Elastic {
            public static class Version52 extends Version5 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(5, 2);
                }
            }

            public static class Version53 extends Version52 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(5, 3);
                }
            }

            public static class Version54 extends Version53 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(5, 4);
                }
            }

            public static class Version55 extends Version54 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(5, 5);
                }
            }

            public static class Version56 extends Version55 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(5, 6);
                }
            }

            @Override
            public VersionComponents getVersion() {
                return VersionComponents.of(5, 0);
            }
        }

        public static class Version6 extends Elastic {
            public static class Version61 extends Version6 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(6, 1);
                }
            }

            public static class Version62 extends Version61 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(6, 2);
                }
            }

            public static class Version63 extends Version62 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(6, 3);
                }
            }

            public static class Version64 extends Version63 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(6, 4);
                }
            }

            public static class Version65 extends Version64 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(6, 5);
                }
            }

            public static class Version66 extends Version65 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(6, 6);
                }
            }

            public static class Version67 extends Version66 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(6, 7);
                }
            }

            public static class Version68 extends Version67 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(6, 8);
                }
            }

            @Override
            public VersionComponents getVersion() {
                return VersionComponents.of(6, 0);
            }
        }

        public static class Version7 extends Elastic {
            public static class Version71 extends Version7 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(7, 1);
                }
            }

            public static class Version72 extends Version71 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(7, 2);
                }
            }

            public static class Version73 extends Version72 {
                @Override
                public VersionComponents getVersion() {
                    return VersionComponents.of(7, 3);
                }
            }

            @Override
            public VersionComponents getVersion() {
                return VersionComponents.of(7, 0);
            }
        }

        @Override
        public abstract VersionComponents getVersion();
    }

    /**
     * This annotation prevents the associated field from being output by Jackson with anything
     * other than the default ObjectMapper.
     */
    public static class Enhancement {
    }

    public static interface HasVersion {
        public VersionComponents getVersion();
    }

    public static final Version5 VERSION_5 = new Version5();

    public static final Version6 VERSION_6 = new Version6();

    public static final Version7 VERSION_7 = new Version7();
}
