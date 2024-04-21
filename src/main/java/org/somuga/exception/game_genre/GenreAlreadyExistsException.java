package org.somuga.exception.game_genre;

import org.somuga.exception.SomugaException;

public class GenreAlreadyExistsException extends SomugaException {
    public GenreAlreadyExistsException(String message) {
        super(message);
    }
}
