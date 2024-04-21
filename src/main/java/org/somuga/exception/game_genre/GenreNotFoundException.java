package org.somuga.exception.game_genre;

import org.somuga.exception.SomugaException;

public class GenreNotFoundException extends SomugaException {
    public GenreNotFoundException(String message) {
        super(message);
    }
}
