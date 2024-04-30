package org.somuga.converter;

import org.somuga.dto.crew_role.CrewRolePublicDto;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.entity.MovieCrewRole;

import java.util.List;

public class MovieConverter {
    public static MoviePublicDto fromEntityToPublicDto(Movie movie) {
        return new MoviePublicDto(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getDescription(),
                movie.getDuration(),
                fromEntityListToCrewRolePublicDtoList(movie.getMovieCrew())

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
        movie.setReleaseDate(movieDto.releaseDate());
        movie.setDescription(movieDto.description());
        movie.setDuration(movieDto.duration());
        return movie;
    }

    public static CrewRolePublicDto fromEntityToCrewRolePublicDto(MovieCrewRole role) {
        return new CrewRolePublicDto(
                role.getMovieCrew().getFullName(),
                role.getMovieCrew().getBirthDate(),
                role.getMovieRole().name(),
                role.getCharacterName()
        );
    }

    public static List<CrewRolePublicDto> fromEntityListToCrewRolePublicDtoList(List<MovieCrewRole> roles) {
        return roles.stream()
                .map(MovieConverter::fromEntityToCrewRolePublicDto)
                .toList();
    }
}
