package org.somuga.service;

import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Media;
import org.somuga.service.interfaces.IMovieService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService implements IMovieService {
    @Override
    public List<MoviePublicDto> getAll(Pageable page) {
        return List.of();
    }

    @Override
    public MoviePublicDto getById(Long id) {
        return null;
    }

    @Override
    public MoviePublicDto create(MovieCreateDto game) {
        return null;
    }

    @Override
    public MoviePublicDto update(Long id, MovieCreateDto game) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Media findById(Long id) {
        return null;
    }
}
