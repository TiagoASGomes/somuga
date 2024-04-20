package org.somuga.exception.movie;

import org.somuga.exception.SomugaException;

public class MovieNotFoundException extends SomugaException {
    public MovieNotFoundException(String message) {
        super(message);
    }
}
