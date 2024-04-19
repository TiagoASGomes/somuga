package org.somuga.service.interfaces;

import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Media;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMovieService {

    List<MoviePublicDto> getAll(Pageable page);

    MoviePublicDto getById(Long id);

    MoviePublicDto create(MovieCreateDto game);

    MoviePublicDto update(Long id, MovieCreateDto game);

    void delete(Long id);

    Media findById(Long id);
}
