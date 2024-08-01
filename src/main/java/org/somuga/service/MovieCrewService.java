package org.somuga.service;

import org.somuga.converter.MovieCrewConverter;
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.entity.MovieCrew;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.somuga.repository.MovieCrewRepository;
import org.somuga.service.interfaces.IMovieCrewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.somuga.util.message.Messages.MOVIE_CREW_NOT_FOUND;

@Service
public class MovieCrewService implements IMovieCrewService {

    private final MovieCrewRepository movieCrewRepository;

    @Autowired
    public MovieCrewService(MovieCrewRepository movieCrewRepository) {
        this.movieCrewRepository = movieCrewRepository;
    }

    @Override
    public List<MovieCrewPublicDto> getAll(Pageable page, String name) {
        if (name != null) {
            return MovieCrewConverter.fromEntityListToPublicDtoList(movieCrewRepository.findByFullNameContainingIgnoreCase(name, page).toList());
        }
        return MovieCrewConverter.fromEntityListToPublicDtoList(movieCrewRepository.findAll(page).toList());
    }

    @Override
    public MovieCrewPublicDto getById(Long id) throws MovieCrewNotFoundException {
        return MovieCrewConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public MovieCrewPublicDto create(MovieCrewCreateDto movieCrewDto) {
        MovieCrew movieCrew = MovieCrewConverter.fromCreateDtoToEntity(movieCrewDto);
        return MovieCrewConverter.fromEntityToPublicDto(movieCrewRepository.save(movieCrew));
    }

    @Override
    public MovieCrewPublicDto update(Long id, MovieCrewCreateDto movieCrew) throws MovieCrewNotFoundException {
        MovieCrew crew = findById(id);
        crew.setFullName(movieCrew.fullName());
        crew.setBirthDate(movieCrew.birthDate());
        return MovieCrewConverter.fromEntityToPublicDto(movieCrewRepository.save(crew));
    }

    @Override
    public void delete(Long id) throws MovieCrewNotFoundException {
        findById(id);
        movieCrewRepository.deleteById(id);
    }

    @Override
    public MovieCrew findById(Long id) throws MovieCrewNotFoundException {
        return movieCrewRepository.findById(id).orElseThrow(() -> new MovieCrewNotFoundException(MOVIE_CREW_NOT_FOUND + id));
    }
}
