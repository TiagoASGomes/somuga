package org.somuga.converter;

import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MovieConverter {
    public static MoviePublicDto fromEntityToPublicDto(Movie movie) {
        return new MoviePublicDto(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getMediaType().name(),
                new ArrayList<>(movie.getActors()),
                movie.getProducer()
        );
    }

    public static List<MoviePublicDto> fromEntityListToPublicDtoList(List<Movie> movies) {
        return movies.stream()
                .map(MovieConverter::fromEntityToPublicDto)
                .toList();
    }

    public static Movie fromCreateDtoToEntity(MovieCreateDto movieDto) {
        Movie movie = new Movie();
        movie.setTitle(movieDto.title());
        movie.setActors(new HashSet<>(movieDto.actors()));
        movie.setProducer(movieDto.producer());
        movie.setReleaseDate(movieDto.releaseDate());
        return movie;
    }
}
