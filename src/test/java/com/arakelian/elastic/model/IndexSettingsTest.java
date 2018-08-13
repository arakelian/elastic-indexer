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

import java.io.IOException;

import org.junit.Test;

import com.arakelian.core.utils.SerializableTestUtils;
import com.arakelian.jackson.utils.JacksonTestUtils;

public class IndexSettingsTest extends AbstractElasticModelTest {
    public static final IndexSettings MINIMAL = ImmutableIndexSettings.builder() //
            .build();

    public static final IndexSettings CUSTOM = ImmutableIndexSettings.builder() //
            .putProperty("index.query.default_field", "text") //
            .putProperty("index.codec", "best_compression") //
            .build();

    private static final ImmutableAnalyzerSettings ANALYZER = ImmutableAnalyzerSettings.builder()
            .putAnalyzer(
                    "default",
                    ImmutableNamedAnalyzer.builder() //
                            .tokenizer("safe_icu_tokenizer") //
                            .addCharFilter("trim", "icu_folding", "uppercase") //
                            .build())
            .putAnalyzer(
                    "custom_analyzer",
                    ImmutableNamedAnalyzer.builder() //
                            .tokenizer("keyword") //
                            .addCharFilter("custom_filter") //
                            .addFilter("trim", "icu_folding", "uppercase") //
                            .build())
            .build();

    private static final ImmutableNormalizerSettings NORMALIZER = ImmutableNormalizerSettings.builder()
            .putNormalizer(
                    "my_normalizer",
                    ImmutableNamedNormalizer.builder() //
                            .type("custom") //
                            .addCharFilter("html_strip") //
                            .addFilter("uppercase", "asciifolding") //
                            .build())
            .build();

    private static final ImmutableCharFilterSettings CHAR_FILTER = ImmutableCharFilterSettings.builder()
            .putCharFilter(
                    "strip_spaces",
                    ImmutableNamedCharFilter.builder() //
                            .type("pattern_replace") //
                            .pattern("[\\s\\u00A0]+") //
                            .replacement("") //
                            .build())
            .build();

    private static final ImmutableTokenizerSettings TOKENIZER = ImmutableTokenizerSettings.builder()
            .putTokenizer(
                    "safe_icu_tokenizer",
                    ImmutableNamedTokenizer.builder() //
                            .type("icu_tokenizer") //
                            .putProperty("max_token_length", 200) //
                            .build())
            .build();

    private static final ImmutableFilterSettings FILTER = ImmutableFilterSettings.builder()
            .putFilter(
                    "shingler",
                    ImmutableNamedFilter.builder() //
                            .type("shingle") //
                            .putProperty("min_shingle_size", 2) //
                            .putProperty("max_shingle_size", 5) //
                            .build())
            .build();

    public static final IndexSettings FULL = ImmutableIndexSettings.builder() //
            .analysis(
                    ImmutableAnalysis.builder() //
                            .analyzer(ANALYZER) //
                            .normalizer(NORMALIZER) //
                            .charFilter(CHAR_FILTER) //
                            .tokenizer(TOKENIZER) //
                            .filter(FILTER) //
                            .build()) //
            .build();

    public IndexSettingsTest(final String number) {
        super(number);
    }

    @Test
    public void testJackson() throws IOException {
        JacksonTestUtils.testReadWrite(objectMapper, MINIMAL, IndexSettings.class);
        JacksonTestUtils.testReadWrite(objectMapper, CUSTOM, IndexSettings.class);
        JacksonTestUtils.testReadWrite(objectMapper, FULL, IndexSettings.class);
    }

    @Test
    public void testSerializable() {
        SerializableTestUtils.testSerializable(MINIMAL, IndexSettings.class);
        SerializableTestUtils.testSerializable(CUSTOM, IndexSettings.class);
        SerializableTestUtils.testSerializable(FULL, IndexSettings.class);
    }
}
