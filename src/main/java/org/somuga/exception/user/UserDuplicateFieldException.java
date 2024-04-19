package org.somuga.exception.user;

import org.somuga.exception.SomugaException;

public class UserDuplicateFieldException extends SomugaException {
    public UserDuplicateFieldException(String message) {
        super(message);
    }
}
