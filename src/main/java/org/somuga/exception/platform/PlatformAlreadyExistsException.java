package org.somuga.exception.platform;

import org.somuga.exception.SomugaException;

public class PlatformAlreadyExistsException extends SomugaException {
    public PlatformAlreadyExistsException(String message) {
        super(message);
    }
}
