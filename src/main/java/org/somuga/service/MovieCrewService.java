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

import static org.somuga.message.Messages.MOVIE_CREW_NOT_FOUND;

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
//        TODO: Verificar se o nome e data de nascimento já existe
        MovieCrew movieCrew = MovieCrewConverter.fromCreateDtoToEntity(movieCrewDto);
        return MovieCrewConverter.fromEntityToPublicDto(movieCrewRepository.save(movieCrew));
    }

    private MovieCrew findById(Long id) throws MovieCrewNotFoundException {
        return movieCrewRepository.findById(id).orElseThrow(() -> new MovieCrewNotFoundException(MOVIE_CREW_NOT_FOUND + id));
    }
}
