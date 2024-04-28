package org.somuga.converter;

import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;

import java.util.List;

public class MovieConverter {
    public static MoviePublicDto fromEntityToPublicDto(Movie movie) {
        return null;
    }

    public static List<MoviePublicDto> fromEntityListToPublicDtoList(List<Movie> movies) {
        return movies.stream()
                .map(MovieConverter::fromEntityToPublicDto)
                .toList();
    }

    public static Movie fromCreateDtoToEntity(MovieCreateDto movieDto) {
        return null;
    }
}
