package org.somuga.service.interfaces;

import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MovieLikePublicDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.movie.InvalidCrewRoleException;
import org.somuga.exception.movie.MovieNotFoundException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IMovieService {

    List<MoviePublicDto> getAll(Pageable page, String title, List<Long> crewId);

    MovieLikePublicDto getById(Long id) throws MovieNotFoundException;

    MoviePublicDto create(MovieCreateDto movie) throws MovieCrewNotFoundException, InvalidCrewRoleException;

    MoviePublicDto update(Long id, MovieCreateDto movie) throws MovieNotFoundException, MovieCrewNotFoundException, InvalidCrewRoleException, InvalidPermissionException;

    void delete(Long id) throws MovieNotFoundException, InvalidPermissionException;

    Movie findById(Long id) throws MovieNotFoundException;

    void adminDelete(Long id) throws MovieNotFoundException;
}
