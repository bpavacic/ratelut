package com.ratelut.apiserver.storage;

import javax.inject.Singleton;

/**
 * Provides configuration for {@link SqlStorage}.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
@Singleton
public class SqlStorageConfig {
    private static final String JDBC_URL_ENV_NAME = "JDBC_URL";
    private static final String IN_MEMORY_JDBC_URL = "jdbc:sqlite::memory:";

    private final String jdbcUrl;

    public SqlStorageConfig() {
        this(System.getenv().getOrDefault(JDBC_URL_ENV_NAME, IN_MEMORY_JDBC_URL));
    }

    SqlStorageConfig(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }
}
