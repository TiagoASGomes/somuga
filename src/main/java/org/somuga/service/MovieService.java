package org.somuga.service;

import org.somuga.entity.Media;
import org.somuga.service.interfaces.IMovieService;
import org.springframework.stereotype.Service;

@Service
public class MovieService implements IMovieService {
    @Override
    public Media findById(Long id) {
        return null;
    }
}
