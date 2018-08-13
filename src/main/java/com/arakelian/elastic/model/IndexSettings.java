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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import com.arakelian.core.feature.Nullable;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@Value.Immutable(copy = false)
@JsonSerialize(as = ImmutableIndexSettings.class)
@JsonDeserialize(builder = ImmutableIndexSettings.Builder.class)
@JsonPropertyOrder({ "number_of_replicas", "number_of_shards", "analysis" })
public interface IndexSettings extends Serializable {
    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableAnalysis.class)
    @JsonDeserialize(builder = ImmutableAnalysis.Builder.class)
    @JsonPropertyOrder({ "analyzer", "normalizer", "filter", "char_filter", "tokenizer" })
    public interface Analysis extends Serializable {
        @Nullable
        @JsonProperty("analyzer")
        public AnalyzerSettings getAnalyzer();

        @Nullable
        @JsonProperty("normalizer")
        public NormalizerSettings getNormalizer();

        @Nullable
        @JsonProperty("char_filter")
        public CharFilterSettings getCharFilter();

        @Nullable
        @JsonProperty("filter")
        public FilterSettings getFilter();

        @Nullable
        @JsonProperty("tokenizer")
        public TokenizerSettings getTokenizer();
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableAnalyzerSettings.class)
    @JsonDeserialize(builder = ImmutableAnalyzerSettings.Builder.class)
    public interface AnalyzerSettings extends Serializable {
        @JsonAnyGetter
        @Value.Default
        public default Map<String, NamedAnalyzer> getAnalyzers() {
            return ImmutableMap.of();
        }
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableNormalizerSettings.class)
    @JsonDeserialize(builder = ImmutableNormalizerSettings.Builder.class)
    public interface NormalizerSettings extends Serializable {
        @JsonAnyGetter
        @Value.Default
        public default Map<String, NamedNormalizer> getNormalizers() {
            return ImmutableMap.of();
        }
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableCharFilterSettings.class)
    @JsonDeserialize(builder = ImmutableCharFilterSettings.Builder.class)
    public interface CharFilterSettings extends Serializable {
        @JsonAnyGetter
        @Value.Default
        public default Map<String, NamedCharFilter> getCharFilters() {
            return ImmutableMap.of();
        }
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableFilterSettings.class)
    @JsonDeserialize(builder = ImmutableFilterSettings.Builder.class)
    public interface FilterSettings extends Serializable {
        @JsonAnyGetter
        @Value.Default
        public default Map<String, NamedFilter> getFilters() {
            return ImmutableMap.of();
        }
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableNamedAnalyzer.class)
    @JsonDeserialize(builder = ImmutableNamedAnalyzer.Builder.class)
    @JsonPropertyOrder({ "tokenizer", "char_filter", "filter" })
    public interface NamedAnalyzer extends Serializable {
        @Value.Default
        @JsonProperty("char_filter")
        public default List<String> getCharFilter() {
            return ImmutableList.of();
        }

        @Value.Default
        @JsonProperty("filter")
        public default List<String> getFilter() {
            return ImmutableList.of();
        }

        @JsonProperty("tokenizer")
        public String getTokenizer();
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableNamedNormalizer.class)
    @JsonDeserialize(builder = ImmutableNamedNormalizer.Builder.class)
    @JsonPropertyOrder({ "type", "char_filter", "filter" })
    public interface NamedNormalizer extends Serializable {
        @Value.Default
        @JsonProperty("char_filter")
        public default List<String> getCharFilter() {
            return ImmutableList.of();
        }

        @Value.Default
        @JsonProperty("filter")
        public default List<String> getFilter() {
            return ImmutableList.of();
        }

        @JsonProperty("type")
        public String getType();
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableNamedCharFilter.class)
    @JsonDeserialize(builder = ImmutableNamedCharFilter.Builder.class)
    @JsonPropertyOrder({ "type", "pattern", "replacement" })
    public interface NamedCharFilter extends Serializable {
        @Value.Default
        @JsonProperty("pattern")
        public default String getPattern() {
            return StringUtils.EMPTY;
        }

        @Value.Default
        @JsonProperty("replacement")
        public default String getReplacement() {
            return StringUtils.EMPTY;
        }

        @JsonProperty("type")
        public String getType();
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableNamedFilter.class)
    @JsonDeserialize(builder = ImmutableNamedFilter.Builder.class)
    @JsonPropertyOrder({ "type" })
    public interface NamedFilter extends Serializable {
        @JsonAnyGetter
        @Value.Default
        public default Map<String, Object> getProperties() {
            return ImmutableMap.of();
        }

        @JsonProperty("type")
        public String getType();
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableNamedTokenizer.class)
    @JsonDeserialize(builder = ImmutableNamedTokenizer.Builder.class)
    @JsonPropertyOrder({ "type" })
    public interface NamedTokenizer extends Serializable {
        @JsonAnyGetter
        @Value.Default
        public default Map<String, Object> getProperties() {
            return ImmutableMap.of();
        }

        @JsonProperty("type")
        public String getType();
    }

    @Value.Immutable(copy = false)
    @JsonSerialize(as = ImmutableTokenizerSettings.class)
    @JsonDeserialize(builder = ImmutableTokenizerSettings.Builder.class)
    public interface TokenizerSettings extends Serializable {
        @JsonAnyGetter
        @Value.Default
        public default Map<String, NamedTokenizer> getTokenizers() {
            return ImmutableMap.of();
        }
    }

    @JsonAnyGetter
    @Value.Default
    public default Map<String, Object> getProperties() {
        return ImmutableMap.of();
    }

    @Value.Default
    @JsonProperty("number_of_replicas")
    public default int getNumberOfReplicas() {
        return 1;
    }

    @Nullable
    @JsonProperty("analysis")
    public Analysis getAnalysis();

    @Value.Default
    @JsonProperty("number_of_shards")
    public default int getNumberOfShards() {
        return 5;
    }
}
