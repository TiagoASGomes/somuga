package org.somuga.exception.media;

import org.somuga.exception.SomugaException;

public class MediaNotFoundException extends SomugaException {
    public MediaNotFoundException(String message) {
        super(message);
    }
}
