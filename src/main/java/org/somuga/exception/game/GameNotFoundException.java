package org.somuga.exception.game;

import org.somuga.exception.SomugaException;

public class GameNotFoundException extends SomugaException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
