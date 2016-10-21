package com.ratelut.apiserver.storage;

import com.google.common.base.Throwables;
import com.google.inject.AbstractModule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Guice storage module.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class StorageModule extends AbstractModule {
    private static final String JDBC_URL_ENV_NAME = "JDBC_URL";
    private static final String IN_MEMORY_JDBC_URL = "jdbc:sqlite::memory:";

    @Override
    public void configure() {
        String jdbcUrl = System.getenv().getOrDefault(JDBC_URL_ENV_NAME, IN_MEMORY_JDBC_URL);

        try {
            System.out.println("Using JDBC URL " + jdbcUrl);
            Connection connection = DriverManager.getConnection(jdbcUrl);

            bind(Storage.class).toInstance(new SqlStorage(connection));
        } catch (SQLException e) {
            Throwables.propagate(e);
        }
    }
}
