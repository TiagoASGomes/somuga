package org.somuga.converter;

import org.somuga.dto.crew_role.CrewRolePublicDto;
import org.somuga.dto.crew_role.MovieRolePublicDto;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MovieLikePublicDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.entity.MovieCrewRole;

import java.util.ArrayList;
import java.util.List;

public class MovieConverter {

    private MovieConverter() {
    }


    public static MoviePublicDto fromEntityToPublicDto(Movie movie) {
        if (movie == null) return null;
        if (movie.getReviews() == null) {
            movie.setReviews(new ArrayList<>());
        }
        if (movie.getLikes() == null) {
            movie.setLikes(new ArrayList<>());
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
        if (movies == null) return new ArrayList<>();
        return movies.stream()
                .map(MovieConverter::fromEntityToPublicDto)
                .toList();
    }

    public static Movie fromCreateDtoToEntity(MovieCreateDto movieDto) {
        if (movieDto == null) return null;
        return Movie.builder()
                .title(movieDto.title())
                .releaseDate(movieDto.releaseDate())
                .description(movieDto.description())
                .duration(movieDto.duration())
                .mediaUrl(movieDto.mediaUrl())
                .imageUrl(movieDto.imageUrl())
                .build();
    }

    public static MovieRolePublicDto fromEntityToMovieRolePublicDto(MovieCrewRole role) {
        if (role == null) return null;
        return new MovieRolePublicDto(
                role.getMovieCrew().getId(),
                role.getMovieCrew().getFullName(),
                role.getMovieCrew().getBirthDate(),
                role.getMovieRole().name(),
                role.getCharacterName()
        );
    }

    public static List<MovieRolePublicDto> fromEntityListToMovieRolePublicDtoList(List<MovieCrewRole> roles) {
        if (roles == null) return new ArrayList<>();
        return roles.stream()
                .map(MovieConverter::fromEntityToMovieRolePublicDto)
                .toList();
    }

    public static List<CrewRolePublicDto> fromEntityListToCrewRolePublicDtoList(List<MovieCrewRole> roles) {
        if (roles == null) return new ArrayList<>();
        return roles.stream()
                .map(MovieConverter::fromEntityToCrewRolePublicDto)
                .toList();
    }

    private static CrewRolePublicDto fromEntityToCrewRolePublicDto(MovieCrewRole movieCrewRole) {
        if (movieCrewRole == null) return null;
        return new CrewRolePublicDto(
                movieCrewRole.getMovieRole().name(),
                movieCrewRole.getCharacterName(),
                movieCrewRole.getMovie().getTitle(),
                movieCrewRole.getMovie().getReleaseDate()
        );
    }

    public static MovieLikePublicDto fromEntityToLikePublicDto(Movie movie, boolean isLiked) {
        if (movie == null) return null;
        MoviePublicDto moviePublicDto = fromEntityToPublicDto(movie);
        return new MovieLikePublicDto(
                moviePublicDto,
                isLiked
        );
    }
}
