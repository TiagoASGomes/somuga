package org.somuga.exception.like;

import org.somuga.exception.SomugaException;

public class AlreadyLikedException extends SomugaException {
    public AlreadyLikedException(String message) {
        super(message);
    }
}
