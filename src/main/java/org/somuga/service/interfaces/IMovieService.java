package org.somuga.service.interfaces;

import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.exception.movie.MovieNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMovieService {

    List<MoviePublicDto> getAll(Pageable page);

    MoviePublicDto getById(Long id) throws MovieNotFoundException;

    MoviePublicDto create(MovieCreateDto movie);

    MoviePublicDto update(Long id, MovieCreateDto movie) throws MovieNotFoundException;

    void delete(Long id) throws MovieNotFoundException;

    Movie findById(Long id) throws MovieNotFoundException;
}
