package com.ratelut.apiserver.storage;

import java.sql.SQLException;

/**
 * Exception thrown by Storage implementations.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 *
 */
public class StorageException extends Exception {
    public StorageException(Exception e) {
        super(e);
    }
}
