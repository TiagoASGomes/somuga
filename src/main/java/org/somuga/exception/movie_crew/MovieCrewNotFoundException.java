package org.somuga.exception.movie_crew;

import org.somuga.exception.SomugaException;

public class MovieCrewNotFoundException extends SomugaException {
    public MovieCrewNotFoundException(String message) {
        super(message);
    }
}
