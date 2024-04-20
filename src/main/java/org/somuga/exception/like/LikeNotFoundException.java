package org.somuga.exception.like;

import org.somuga.exception.SomugaException;

public class LikeNotFoundException extends SomugaException {
    public LikeNotFoundException(String message) {
        super(message);
    }
}
