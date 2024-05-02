package org.somuga.exception.movie;

import org.somuga.exception.SomugaException;

public class InvalidCrewRoleException extends SomugaException {
    public InvalidCrewRoleException(String message) {
        super(message);
    }
}
