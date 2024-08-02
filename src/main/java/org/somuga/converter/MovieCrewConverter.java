package org.somuga.converter;

import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.entity.MovieCrew;

import java.util.ArrayList;
import java.util.List;

public class MovieCrewConverter {

    private MovieCrewConverter() {
    }

    public static MovieCrewPublicDto fromEntityToPublicDto(MovieCrew movieCrew) {
        if (movieCrew == null) return null;
        return new MovieCrewPublicDto(
                movieCrew.getId(),
                movieCrew.getFullName(),
                movieCrew.getBirthDate(),
                MovieConverter.fromEntityListToCrewRolePublicDtoList(movieCrew.getRoles())
        );
    }

    public static List<MovieCrewPublicDto> fromEntityListToPublicDtoList(List<MovieCrew> movieCrew) {
        if (movieCrew == null) return new ArrayList<>();
        return movieCrew.stream()
                .map(MovieCrewConverter::fromEntityToPublicDto)
                .toList();
    }

    public static MovieCrew fromCreateDtoToEntity(MovieCrewCreateDto movieCrewCreateDto) {
        if (movieCrewCreateDto == null) return null;
        return MovieCrew.builder()
                .fullName(movieCrewCreateDto.fullName())
                .birthDate(movieCrewCreateDto.birthDate())
                .build();
    }

}
