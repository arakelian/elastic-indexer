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

package com.arakelian.elastic.search;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.arakelian.elastic.model.VersionComponents;
import com.arakelian.elastic.model.aggs.ValuesSourceAggregation;
import com.arakelian.elastic.model.aggs.bucket.BucketOrder;
import com.arakelian.elastic.model.aggs.bucket.DateHistogramAggregation;
import com.arakelian.elastic.model.aggs.bucket.DateRangeAggregation;
import com.arakelian.elastic.model.aggs.bucket.GeoHashGridAggregation;
import com.arakelian.elastic.model.aggs.bucket.HistogramAggregation;
import com.arakelian.elastic.model.aggs.bucket.IpRangeAggregation;
import com.arakelian.elastic.model.aggs.bucket.MissingAggregation;
import com.arakelian.elastic.model.aggs.bucket.Range;
import com.arakelian.elastic.model.aggs.bucket.RangeAggregation;
import com.arakelian.elastic.model.aggs.bucket.SamplerAggregation;
import com.arakelian.elastic.model.aggs.bucket.TermsAggregation;
import com.arakelian.elastic.model.aggs.metrics.AvgAggregation;
import com.arakelian.elastic.model.aggs.metrics.CardinalityAggregation;
import com.arakelian.elastic.model.aggs.metrics.ExtendedStatsAggregation;
import com.arakelian.elastic.model.aggs.metrics.GeoBoundsAggregation;
import com.arakelian.elastic.model.aggs.metrics.GeoCentroidAggregation;
import com.arakelian.elastic.model.aggs.metrics.MaxAggregation;
import com.arakelian.elastic.model.aggs.metrics.MinAggregation;
import com.arakelian.elastic.model.aggs.metrics.PercentileRanksAggregation;
import com.arakelian.elastic.model.aggs.metrics.PercentileRanksAggregation.Method;
import com.arakelian.elastic.model.aggs.metrics.PercentilesAggregation;
import com.arakelian.elastic.model.aggs.metrics.StatsAggregation;
import com.arakelian.elastic.model.aggs.metrics.SumAggregation;
import com.arakelian.elastic.model.aggs.metrics.ValueCountAggregation;
import com.fasterxml.jackson.core.JsonGenerator;

public class WriteAggregationVisitor extends AbstractVisitor implements AggregationVisitor {
    public WriteAggregationVisitor(final JsonGenerator writer, final VersionComponents version) {
        super(writer, version);
    }

    @Override
    public boolean enterAvg(final AvgAggregation agg) {
        try {
            writer.writeFieldName("avg");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // avg
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterCardinality(final CardinalityAggregation agg) {
        try {
            writer.writeFieldName("cardinality");
            writer.writeStartObject();
            writeValueSource(agg);
            writeFieldValue("precision_threshold", agg.getPrecisionThreshold());
            writer.writeEndObject(); // cardinality
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterDateHistogram(final DateHistogramAggregation agg) {
        try {
            writer.writeFieldName("date_histogram");
            writer.writeStartObject();
            writeValueSource(agg);
            writeFieldValue("keyed", agg.isKeyed());
            writeFieldValue("offset", agg.getOffset());
            writeFieldValue("interval", agg.getInterval());
            writeFieldValue("min_doc_count", agg.getMinDocCount());
            writer.writeEndObject(); // date_histogram
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterDateRange(final DateRangeAggregation agg) {
        try {
            writer.writeFieldName("date_range");
            writer.writeStartObject();
            writeValueSource(agg);

            final List<Range> ranges = agg.getRanges();
            if (ranges.size() != 0) {
                writer.writeFieldName("ranges");
                writer.writeStartArray();
                for (final Range range : ranges) {
                    writer.writeStartObject();
                    writeFieldValue("key", range.getKey());
                    writeFieldValue("from", range.getFrom());
                    writeFieldValue("to", range.getTo());
                    writer.writeEndObject();
                }
                writer.writeEndArray();
            }

            writer.writeEndObject(); // date_range
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterExtendedStats(final ExtendedStatsAggregation agg) {
        try {
            writer.writeFieldName("extended_stats");
            writer.writeStartObject();
            writeValueSource(agg);
            writeFieldValue("sigma", agg.getSigma());
            writer.writeEndObject(); // extended_stats
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterGeoBounds(final GeoBoundsAggregation agg) {
        try {
            writer.writeFieldName("geo_bounds");
            writer.writeStartObject();
            writeValueSource(agg);
            writeFieldValue("wrap_longitude", agg.isWrapLongitude());
            writer.writeEndObject(); // geo_bounds
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterGeoCentroid(final GeoCentroidAggregation agg) {
        try {
            writer.writeFieldName("geo_centroid");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // geo_centroid
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterGeoHashGrid(final GeoHashGridAggregation agg) {
        try {
            writer.writeFieldName("geohash_grid");
            writer.writeStartObject();
            writeValueSource(agg);
            writeFieldValue("precision", agg.getPrecision());
            writeFieldValue("size", agg.getRequiredSize());
            writeFieldValue("shard_size", agg.getShardSize());
            writer.writeEndObject(); // geohash_grid
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterHistogram(final HistogramAggregation agg) {
        try {
            writer.writeFieldName("histogram");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // histogram
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterIpRange(final IpRangeAggregation agg) {
        try {
            writer.writeFieldName("ip_range");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // ip_range
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterMax(final MaxAggregation agg) {
        try {
            writer.writeFieldName("max");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // max
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterMin(final MinAggregation agg) {
        try {
            writer.writeFieldName("min");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // min
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterMissing(final MissingAggregation agg) {
        try {
            writer.writeFieldName("missing");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // missing
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterPercentileRanks(final PercentileRanksAggregation agg) {
        try {
            writer.writeFieldName("percentile_ranks");
            writer.writeStartObject();
            writeValueSource(agg);
            writeFieldValue("values", agg.getValues());

            final Method method = agg.getMethod();
            if (method != null) {
                writer.writeFieldName(method.name().toLowerCase());
                writer.writeStartObject();
                // these two fields values are mutually exclusive; if one is set, the other is null
                writeFieldValue("compression", agg.getCompression());
                writeFieldValue(
                        "number_of_significant_value_digits",
                        agg.getNumberOfSignificantValueDigits());
                writer.writeEndObject();
            }
            writeFieldValue("keyed", agg.isKeyed());
            writer.writeEndObject(); // percentile_ranks
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterPercentiles(final PercentilesAggregation agg) {
        try {
            writer.writeFieldName("percentiles");
            writer.writeStartObject();
            writeValueSource(agg);
            writeFieldValue("percents", agg.getPercents());

            final Method method = agg.getMethod();
            if (method != null) {
                writer.writeFieldName(method.name().toLowerCase());
                writer.writeStartObject();
                // these two fields values are mutually exclusive; if one is set, the other is null
                writeFieldValue("compression", agg.getCompression());
                writeFieldValue(
                        "number_of_significant_value_digits",
                        agg.getNumberOfSignificantValueDigits());
                writer.writeEndObject();
            }
            writeFieldValue("keyed", agg.isKeyed());
            writer.writeEndObject(); // percentiles
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterRange(final RangeAggregation agg) {
        try {
            writer.writeFieldName("range");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // range
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterSampler(final SamplerAggregation agg) {
        try {
            writer.writeFieldName("sampler");
            writer.writeStartObject();
            writeValueSource(agg);
            writeFieldValue("shard_size", agg.getShardSize());
            writer.writeEndObject(); // sampler
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterStats(final StatsAggregation agg) {
        try {
            writer.writeFieldName("stats");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // stats
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterSum(final SumAggregation agg) {
        try {
            writer.writeFieldName("sum");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // missing
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterTerms(final TermsAggregation agg) {
        try {
            writer.writeFieldName("terms");
            writer.writeStartObject();
            writeValueSource(agg);
            writeFieldValue("min_doc_count", agg.getMinDocCount());
            writeFieldValue("shard_min_doc_count", agg.getShardMinDocCount());
            writeFieldValue("show_term_doc_count_error", agg.isShowTermDocCountError());
            writeFieldValue("size", agg.getSize());
            writeFieldValue("shard_size", agg.getShardSize());

            final String include = agg.getInclude();
            if (include != null) {
                // regex
                writeFieldValue("include", include);
            } else {
                // set of values
                writeFieldValue("include", agg.getIncludeValues());
            }

            final String exclude = agg.getExclude();
            if (exclude != null) {
                // regex
                writeFieldValue("exclude", exclude);
            } else {
                // set of values
                writeFieldValue("exclude", agg.getExcludeValues());
            }

            writeOrder(agg.getOrder());
            writer.writeEndObject(); // terms
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    @Override
    public boolean enterValueCount(final ValueCountAggregation agg) {
        try {
            writer.writeFieldName("value_count");
            writer.writeStartObject();
            writeValueSource(agg);
            writer.writeEndObject(); // value_count
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return false;
    }

    public JsonGenerator getWriter() {
        return writer;
    }

    /**
     * Serialize an array of sorts
     *
     * @param orders
     *            list of sort fields
     *
     * @throws IOException
     *             if serialization fails
     */
    private void writeOrder(final List<BucketOrder> orders) throws IOException {
        if (orders.size() == 0) {
            return;
        }
        writer.writeFieldName("order");
        writer.writeStartArray();
        for (final BucketOrder order : orders) {
            writer.writeStartObject();

            String name = order.getFieldName();
            if (StringUtils.equals(name, "_key")) {
                if (!version.atLeast(6, 0, 0)) {
                    name = "_term";
                }
            }

            writer.writeFieldName(name);
            writer.writeString(order.getOrder().name().toLowerCase());
            writer.writeEndObject();
        }
        writer.writeEndArray();
    }

    private void writeValueSource(final ValuesSourceAggregation agg) throws IOException {
        writeFieldValue("field", agg.getField());
        writeFieldValue("format", agg.getFormat());
        writeFieldValue("missing", agg.getMissing());
        writeFieldValue("time_zone", agg.getTimeZone());
    }
}
