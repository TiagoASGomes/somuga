package org.somuga.service.interfaces;

import org.somuga.entity.Media;

public interface IMovieService {
    Media findById(Long id);
}
