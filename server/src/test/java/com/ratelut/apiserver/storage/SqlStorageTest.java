package com.ratelut.apiserver.storage;

import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Unit tests for {@link SqlStorage}.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class SqlStorageTest extends BaseStorageTest {
    private Connection connection;

    @Before
    public void setupDatabaseConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    }

    @After
    public void closeDatabaseConnection() throws SQLException {
        connection.close();
    }

    @Override
    protected Storage createStorageForTest() throws Exception {
        return new SqlStorage(connection);
    }
}