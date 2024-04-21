package org.somuga.exception.platform;

import org.somuga.exception.SomugaException;

public class PlatformNotFoundException extends SomugaException {
    public PlatformNotFoundException(String message) {
        super(message);
    }
}
