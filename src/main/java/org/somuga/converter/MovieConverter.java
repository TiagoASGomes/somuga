package org.somuga.converter;

import org.somuga.dto.crew_role.CrewRolePublicDto;
import org.somuga.dto.crew_role.MovieRolePublicDto;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.entity.MovieCrewRole;

import java.util.HashSet;
import java.util.List;

public class MovieConverter {
    public static MoviePublicDto fromEntityToPublicDto(Movie movie) {
        if (movie.getReviews() == null) {
            movie.setReviews(new HashSet<>());
        }
        if (movie.getLikes() == null) {
            movie.setLikes(new HashSet<>());
        }
        return new MoviePublicDto(
                movie.getId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getDescription(),
                movie.getDuration(),
                fromEntityListToMovieRolePublicDtoList(movie.getMovieCrew()),
                movie.getMediaUrl(),
                movie.getImageUrl(),
                movie.getLikes().size(),
                movie.getReviews().size()
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
        movie.setMediaUrl(movieDto.mediaUrl());
        movie.setImageUrl(movieDto.imageUrl());
        return movie;
    }

    public static MovieRolePublicDto fromEntityToMovieRolePublicDto(MovieCrewRole role) {
        return new MovieRolePublicDto(
                role.getMovieCrew().getId(),
                role.getMovieCrew().getFullName(),
                role.getMovieCrew().getBirthDate(),
                role.getMovieRole().name(),
                role.getCharacterName()
        );
    }

    public static List<MovieRolePublicDto> fromEntityListToMovieRolePublicDtoList(List<MovieCrewRole> roles) {
        return roles.stream()
                .map(MovieConverter::fromEntityToMovieRolePublicDto)
                .toList();
    }

    public static List<CrewRolePublicDto> fromEntityListToCrewRolePublicDtoList(List<MovieCrewRole> roles) {
        return roles.stream()
                .map(MovieConverter::fromEntityToCrewRolePublicDto)
                .toList();
    }

    private static CrewRolePublicDto fromEntityToCrewRolePublicDto(MovieCrewRole movieCrewRole) {
        return new CrewRolePublicDto(
                movieCrewRole.getMovieRole().name(),
                movieCrewRole.getCharacterName(),
                movieCrewRole.getMovie().getTitle(),
                movieCrewRole.getMovie().getReleaseDate()
        );
    }
}
