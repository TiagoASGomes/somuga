package org.somuga.service;

import org.somuga.converter.MovieCrewConverter;
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.entity.MovieCrew;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.somuga.repository.MovieCrewRepository;
import org.somuga.service.interfaces.IMovieCrewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.somuga.util.message.Messages.MOVIE_CREW_NOT_FOUND;
import static org.somuga.util.message.Messages.UNAUTHORIZED_UPDATE;

@Service
public class MovieCrewService implements IMovieCrewService {

    private final MovieCrewRepository movieCrewRepository;

    @Autowired
    public MovieCrewService(MovieCrewRepository movieCrewRepository) {
        this.movieCrewRepository = movieCrewRepository;
    }

    @Override
    public List<MovieCrewPublicDto> getAll(Pageable page) {
        return MovieCrewConverter.fromEntityListToPublicDtoList(movieCrewRepository.findAll(page).toList());
    }

    @Override
    public MovieCrewPublicDto getById(Long id) throws MovieCrewNotFoundException {
        return MovieCrewConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public List<MovieCrewPublicDto> getByName(String name, Pageable page) {
        return MovieCrewConverter.fromEntityListToPublicDtoList(movieCrewRepository.findByFullNameContainingIgnoreCase(name, page).toList());
    }

    @Override
    public MovieCrewPublicDto create(MovieCrewCreateDto movieCrewDto) {
        MovieCrew movieCrew = MovieCrewConverter.fromCreateDtoToEntity(movieCrewDto);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        movieCrew.setCrewCreatorId(auth.getName());
        return MovieCrewConverter.fromEntityToPublicDto(movieCrewRepository.save(movieCrew));
    }

    @Override
    public MovieCrewPublicDto update(Long id, MovieCrewCreateDto movieCrew) throws MovieCrewNotFoundException, InvalidPermissionException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MovieCrew crew = findById(id);
        if (!crew.getCrewCreatorId().equals(auth.getName())) {
            throw new InvalidPermissionException(UNAUTHORIZED_UPDATE);
        }
        crew.setFullName(movieCrew.fullName());
        crew.setBirthDate(movieCrew.birthDate());
        return MovieCrewConverter.fromEntityToPublicDto(movieCrewRepository.save(crew));
    }

    public MovieCrew findById(Long id) throws MovieCrewNotFoundException {
        return movieCrewRepository.findById(id).orElseThrow(() -> new MovieCrewNotFoundException(MOVIE_CREW_NOT_FOUND + id));
    }
}
