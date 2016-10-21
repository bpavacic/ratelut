package com.ratelut.apiserver.storage;

import com.google.inject.AbstractModule;

/**
 * Guice storage module.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class StorageModule extends AbstractModule {
    @Override
    public void configure() {
        bind(Storage.class).toInstance(new InMemoryStorage());
    }
}
