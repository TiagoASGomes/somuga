package org.somuga.exception.review;

import org.somuga.exception.SomugaException;

public class AlreadyReviewedException extends SomugaException {
    public AlreadyReviewedException(String message) {
        super(message);
    }
}
