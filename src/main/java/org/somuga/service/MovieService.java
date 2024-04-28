package org.somuga.service;

import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.exception.movie.MovieNotFoundException;
import org.somuga.repository.MovieRepository;
import org.somuga.service.interfaces.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.somuga.message.Messages.MOVIE_NOT_FOUND;

@Service
public class MovieService implements IMovieService {

    private final MovieRepository movieRepo;

    @Autowired
    public MovieService(MovieRepository movieRepo) {
        this.movieRepo = movieRepo;
    }

    @Override
    public List<MoviePublicDto> getAll(Pageable page) {
        return null;
    }

    @Override
    public MoviePublicDto getById(Long id) throws MovieNotFoundException {
        return null;
    }

    @Override
    public MoviePublicDto create(MovieCreateDto movieDto) {
        return null;
    }

    @Override
    public MoviePublicDto update(Long id, MovieCreateDto movieDto) throws MovieNotFoundException {
        return null;
    }

    @Override
    public void delete(Long id) throws MovieNotFoundException {
    }

    @Override
    public Movie findById(Long id) throws MovieNotFoundException {
        return movieRepo.findById(id).orElseThrow(() -> new MovieNotFoundException(MOVIE_NOT_FOUND + id));
    }
}
