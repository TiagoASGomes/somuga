package org.somuga.service;

import org.somuga.converter.MovieConverter;
import org.somuga.dto.crew_role.CrewRoleCreateDto;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.entity.MovieCrew;
import org.somuga.enums.MediaType;
import org.somuga.enums.MovieRole;
import org.somuga.exception.movie.InvalidCrewRoleException;
import org.somuga.exception.movie.MovieNotFoundException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.somuga.repository.MovieRepository;
import org.somuga.service.interfaces.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.somuga.message.Messages.*;

@Service
public class MovieService implements IMovieService {

    private final MovieRepository movieRepo;
    private final MovieCrewService crewService;

    @Autowired
    public MovieService(MovieRepository movieRepo, MovieCrewService crewService) {
        this.movieRepo = movieRepo;
        this.crewService = crewService;
    }

    @Override
    public List<MoviePublicDto> getAll(Pageable page) {
        return MovieConverter.fromEntityListToPublicDtoList(movieRepo.findAll(page).toList());
    }

    @Override
    public List<MoviePublicDto> searchByTitle(String title, Pageable page) {
        return MovieConverter.fromEntityListToPublicDtoList(movieRepo.findByTitleContainingIgnoreCase(title, page).toList());
    }

    @Override
    public List<MoviePublicDto> getByCrewId(Long crewId, Pageable page) {
        return MovieConverter.fromEntityListToPublicDtoList(movieRepo.findByCrewId(crewId, page).toList());
    }

    @Override
    public MoviePublicDto getById(Long id) throws MovieNotFoundException {
        return MovieConverter.fromEntityToPublicDto(findById(id));
    }

    public MoviePublicDto create(MovieCreateDto movieDto) throws MovieCrewNotFoundException, InvalidCrewRoleException {
        validateCrew(movieDto.crew());
        Movie movie = MovieConverter.fromCreateDtoToEntity(movieDto);
        List<MovieCrew> crew = new ArrayList<>();
        for (CrewRoleCreateDto roleDto : movieDto.crew()) {
            crew.add(crewService.findById(roleDto.movieCrewId()));
        }
        for (int i = 0; i < crew.size(); i++) {
            CrewRoleCreateDto roleDto = movieDto.crew().get(i);
            movie.addMovieCrew(crew.get(i), MovieRole.valueOf(roleDto.movieRole()), roleDto.characterName());
        }
        movie.setMediaType(MediaType.MOVIE);
        return MovieConverter.fromEntityToPublicDto(movieRepo.save(movie));
    }


    @Override
    public MoviePublicDto update(Long id, MovieCreateDto movieDto) throws MovieNotFoundException, MovieCrewNotFoundException, InvalidCrewRoleException {
        findById(id);
        validateCrew(movieDto.crew());
        Movie movie = MovieConverter.fromCreateDtoToEntity(movieDto);
        movie.setId(id);
        List<MovieCrew> crew = new ArrayList<>();
        for (CrewRoleCreateDto roleDto : movieDto.crew()) {
            crew.add(crewService.findById(roleDto.movieCrewId()));
        }
        for (int i = 0; i < crew.size(); i++) {
            CrewRoleCreateDto roleDto = movieDto.crew().get(i);
            movie.addMovieCrew(crew.get(i), MovieRole.valueOf(roleDto.movieRole()), roleDto.characterName());
        }
        return MovieConverter.fromEntityToPublicDto(movieRepo.save(movie));
    }

    @Override
    public void delete(Long id) throws MovieNotFoundException {
        findById(id);
        movieRepo.deleteById(id);
    }

    @Override
    public Movie findById(Long id) throws MovieNotFoundException {
        return movieRepo.findById(id).orElseThrow(() -> new MovieNotFoundException(MOVIE_NOT_FOUND + id));
    }

    private void validateCrew(List<CrewRoleCreateDto> crew) throws InvalidCrewRoleException {
        for (CrewRoleCreateDto roleDto : crew) {
            if (roleDto.movieCrewId() == null || roleDto.movieCrewId() <= 0) {
                throw new InvalidCrewRoleException(ID_GREATER_THAN_0);
            }
            if (roleDto.movieRole() == null) {
                throw new InvalidCrewRoleException(INVALID_MOVIE_ROLE);
            }
            if (roleDto.characterName().length() > 255) {
                throw new InvalidCrewRoleException(INVALID_CHARACTER_NAME);
            }
            try {
                MovieRole.valueOf(roleDto.movieRole());
            } catch (IllegalArgumentException e) {
                throw new InvalidCrewRoleException(INVALID_MOVIE_ROLE);
            }
        }
    }
}
