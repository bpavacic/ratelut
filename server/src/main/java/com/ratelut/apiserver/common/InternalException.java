package com.ratelut.apiserver.common;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Exception thrown when internal error occurs.
 *
 * @author Boris Pavacic (boris.pavacic@gmail.com)
 */
public class InternalException extends Exception {
    public InternalException(String message, Exception cause) {
        super(message, cause);
    }

    public InternalException(Exception cause) {
        super(cause);
    }
}
