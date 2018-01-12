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
