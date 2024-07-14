package org.somuga.service.interfaces;

import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMovieCrewService {
    List<MovieCrewPublicDto> getAll(Pageable page, String name);

    MovieCrewPublicDto getById(Long id) throws MovieCrewNotFoundException;

    MovieCrewPublicDto create(MovieCrewCreateDto movieCrew);

    MovieCrewPublicDto update(Long id, MovieCrewCreateDto movieCrew) throws MovieCrewNotFoundException, InvalidPermissionException;

    void delete(Long id) throws MovieCrewNotFoundException, InvalidPermissionException;
}
