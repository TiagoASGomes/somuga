package org.somuga.service.interfaces;

import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMovieCrewService {
    List<MovieCrewPublicDto> getAll(Pageable page);

    MovieCrewPublicDto getById(Long id) throws MovieCrewNotFoundException;

    List<MovieCrewPublicDto> getByName(String name, Pageable page);

    MovieCrewPublicDto create(MovieCrewCreateDto movieCrew);
}
