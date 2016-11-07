package com.ratelut.apiserver.storage;

/**
 * Unit tests for {@link SqlStorage}.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class SqlStorageTest extends BaseStorageTest {
    private static final String IN_MEMORY_JDBC_URL = "jdbc:sqlite::memory:";

    @Override
    protected Storage createStorageForTest() throws Exception {
        return new SqlStorage(new SqlStorageConfig(IN_MEMORY_JDBC_URL));
    }
}