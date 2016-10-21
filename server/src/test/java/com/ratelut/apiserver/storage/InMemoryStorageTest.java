package com.ratelut.apiserver.storage;

/**
 * Unit tests for InMemoryStorage.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class InMemoryStorageTest extends BaseStorageTest {
    @Override
    protected Storage createStorageForTest() {
        return new InMemoryStorage();
    }
}