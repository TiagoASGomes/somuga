package org.somuga.exception.review;

import org.somuga.exception.SomugaException;

public class ReviewNotFoundException extends SomugaException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
