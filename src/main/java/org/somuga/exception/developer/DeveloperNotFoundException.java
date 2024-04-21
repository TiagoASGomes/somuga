package org.somuga.exception.developer;

import org.somuga.exception.SomugaException;

public class DeveloperNotFoundException extends SomugaException {
    public DeveloperNotFoundException(String message) {
        super(message);
    }
}
