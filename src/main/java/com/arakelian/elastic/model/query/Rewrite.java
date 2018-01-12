package com.arakelian.elastic.model.query;

/**
 * @see <a href=
 *      "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-multi-term-rewrite.html">Multiterm
 *      Rewrite</a>
 */
public enum Rewrite {
    CONSTANT_SCORE, //
    SCORING_BOOLEAN, //
    CONSTANT_SCORE_BOOLEAN, //
    TOP_TERMS_N, //
    TOP_TERMS_BOOST_N, //
    TOP_TERMS_BLENDED_FREQS_N //
}
