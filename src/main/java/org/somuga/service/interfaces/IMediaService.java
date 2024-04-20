package org.somuga.service.interfaces;

import org.somuga.entity.Media;
import org.somuga.exception.media.MediaNotFoundException;

public interface IMediaService {
    Media findById(Long id) throws MediaNotFoundException;
}
