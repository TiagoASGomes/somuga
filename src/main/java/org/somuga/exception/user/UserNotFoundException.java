package org.somuga.exception.user;

import org.somuga.exception.SomugaException;

public class UserNotFoundException extends SomugaException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
