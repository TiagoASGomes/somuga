package org.somuga.service.interfaces;

import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.exception.movie.MovieNotFoundException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMovieService {

    List<MoviePublicDto> getAll(Pageable page);

    List<MoviePublicDto> searchByTitle(String title, Pageable page);

    List<MoviePublicDto> getByCrewId(Long crewId, Pageable page);

    MoviePublicDto getById(Long id) throws MovieNotFoundException;

    MoviePublicDto create(MovieCreateDto movie) throws MovieCrewNotFoundException;

    MoviePublicDto update(Long id, MovieCreateDto movie) throws MovieNotFoundException, MovieCrewNotFoundException;

    void delete(Long id) throws MovieNotFoundException;

    Movie findById(Long id) throws MovieNotFoundException;

}
