package org.somuga.exception.user;

import org.somuga.exception.SomugaException;

public class DuplicateFieldException extends SomugaException {
    public DuplicateFieldException(String message) {
        super(message);
    }
}
