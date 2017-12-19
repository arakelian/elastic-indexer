package com.arakelian.elastic;

public class ElasticException extends RuntimeException {
    public ElasticException() {
        super();
    }

    public ElasticException(final String message) {
        super(message);
    }

    public ElasticException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ElasticException(
            final String message,
            final Throwable cause,
            final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ElasticException(final Throwable cause) {
        super(cause);
    }
}
