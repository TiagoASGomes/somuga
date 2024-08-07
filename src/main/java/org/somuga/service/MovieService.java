package org.somuga.service;

import org.somuga.converter.MovieConverter;
import org.somuga.dto.crew_role.MovieRoleCreateDto;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MovieLikePublicDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.entity.MovieCrew;
import org.somuga.entity.MovieCrewRole;
import org.somuga.enums.MediaType;
import org.somuga.enums.MovieRole;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.movie.InvalidCrewRoleException;
import org.somuga.exception.movie.MovieNotFoundException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.somuga.filters.SearchCriteria;
import org.somuga.filters.movie.MovieSpecificationBuilder;
import org.somuga.repository.MovieRepository;
import org.somuga.service.interfaces.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.somuga.util.message.Messages.*;

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
    public List<MoviePublicDto> getAll(Pageable page, String title, List<Long> crewIds) {
        List<SearchCriteria> params = createSearchCriteria(title, crewIds);
        MovieSpecificationBuilder builder = new MovieSpecificationBuilder();
        params.forEach(builder::with);

        List<Movie> movies = movieRepo.findAll(builder.build(), page).toList();

        return MovieConverter.fromEntityListToPublicDtoList(movies);
    }

    private List<SearchCriteria> createSearchCriteria(String title, List<Long> crewIds) {
        List<SearchCriteria> params = new ArrayList<>();
        if (title != null) {
            params.add(new SearchCriteria("title", title));
        }
        if (crewIds != null) {
            for (Long id : crewIds) {
                params.add(new SearchCriteria("crewId", id.toString()));
            }
        }
        return params;
    }

    @Override
    public MovieLikePublicDto getById(Long id) throws MovieNotFoundException {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Movie movie = findById(id);
        if (user.equals("anonymousUser")) {
            return MovieConverter.fromEntityToLikePublicDto(movie, false);
        }
        boolean isLiked = movie.getLikes()
                .stream()
                .anyMatch(like -> like.getUser().getId().equals(user));
        return MovieConverter.fromEntityToLikePublicDto(movie, isLiked);
    }

    public MoviePublicDto create(MovieCreateDto movieDto) throws MovieCrewNotFoundException, InvalidCrewRoleException {
        validateCrew(movieDto.crew());
        Movie movie = MovieConverter.fromCreateDtoToEntity(movieDto);
        List<MovieCrew> crew = new ArrayList<>();
        for (MovieRoleCreateDto roleDto : movieDto.crew()) {
            crew.add(crewService.findById(roleDto.movieCrewId()));
        }
        for (int i = 0; i < crew.size(); i++) {
            MovieRoleCreateDto roleDto = movieDto.crew().get(i);
            if (!roleDto.movieRole().equals(MovieRole.ACTOR.toString())) {
                movie.addMovieCrew(crew.get(i), MovieRole.valueOf(roleDto.movieRole().toUpperCase()), "");
            } else {
                movie.addMovieCrew(crew.get(i), MovieRole.ACTOR, roleDto.characterName());
            }
        }
        movie.setMediaType(MediaType.MOVIE);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        movie.setMediaCreatorId(auth.getName());
        return MovieConverter.fromEntityToPublicDto(movieRepo.save(movie));
    }


    @Override
    public MoviePublicDto update(Long id, MovieCreateDto movieDto) throws MovieNotFoundException, MovieCrewNotFoundException, InvalidCrewRoleException, InvalidPermissionException {
        Movie movie = findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!movie.getMediaCreatorId().equals(auth.getName())) {
            throw new InvalidPermissionException(UNAUTHORIZED_UPDATE);
        }
        validateCrew(movieDto.crew());
        List<MovieCrewRole> toDelete = new ArrayList<>(movie.getMovieCrew());
        for (MovieCrewRole role : toDelete) {
            movie.removeMovieCrew(role);
        }
        movie.setTitle(movieDto.title());
        movie.setReleaseDate(movieDto.releaseDate());
        movie.setDuration(movieDto.duration());
        movie.setDescription(movieDto.description());
        movie.setMediaUrl(movieDto.mediaUrl());
        movie.setImageUrl(movieDto.imageUrl());
        List<MovieCrew> crew = new ArrayList<>();
        for (MovieRoleCreateDto roleDto : movieDto.crew()) {
            crew.add(crewService.findById(roleDto.movieCrewId()));
        }
        for (int i = 0; i < crew.size(); i++) {
            MovieRoleCreateDto roleDto = movieDto.crew().get(i);
            if (!roleDto.movieRole().equals(MovieRole.ACTOR.toString())) {
                movie.addMovieCrew(crew.get(i), MovieRole.valueOf(roleDto.movieRole().toUpperCase()), "");
            } else {
                movie.addMovieCrew(crew.get(i), MovieRole.ACTOR, roleDto.characterName());
            }
        }
        return MovieConverter.fromEntityToPublicDto(movieRepo.save(movie));
    }


    @Override
    public void delete(Long id) throws MovieNotFoundException, InvalidPermissionException {
        Movie movie = findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!movie.getMediaCreatorId().equals(auth.getName())) {
            throw new InvalidPermissionException(UNAUTHORIZED_DELETE);
        }
        movieRepo.deleteById(id);
    }

    @Override
    public Movie findById(Long id) throws MovieNotFoundException {
        return movieRepo.findById(id).orElseThrow(() -> new MovieNotFoundException(MOVIE_NOT_FOUND + id));
    }

    @Override
    public void adminDelete(Long id) throws MovieNotFoundException {
        findById(id);
        movieRepo.deleteById(id);
    }

    private void validateCrew(List<MovieRoleCreateDto> crew) throws InvalidCrewRoleException {
        for (MovieRoleCreateDto roleDto : crew) {
            if (roleDto.movieCrewId() == null || roleDto.movieCrewId() <= 0) {
                throw new InvalidCrewRoleException(ID_GREATER_THAN_0);
            }
            if (roleDto.movieRole() == null) {
                throw new InvalidCrewRoleException(INVALID_MOVIE_ROLE);
            }
            try {
                MovieRole.valueOf(roleDto.movieRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidCrewRoleException(INVALID_MOVIE_ROLE);
            }
            if (roleDto.movieRole().equals(MovieRole.ACTOR.toString())) {
                if (roleDto.characterName() == null || roleDto.characterName().isBlank()) {
                    throw new InvalidCrewRoleException(CHARACTER_NAME_REQUIRED);
                }
                if (roleDto.characterName().length() > 255) {
                    throw new InvalidCrewRoleException(INVALID_CHARACTER_NAME);
                }
            }
        }
    }
}
