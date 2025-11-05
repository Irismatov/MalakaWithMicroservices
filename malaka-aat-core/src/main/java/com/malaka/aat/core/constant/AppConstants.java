package com.malaka.aat.core.constant;

/**
 * Application-wide constants.
 * Add your own constants here as needed.
 */
public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("Constants class");
    }

    // Pagination defaults
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_DIRECTION = "ASC";

    // Header names
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String API_VERSION_HEADER = "X-API-Version";

    // Date formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
