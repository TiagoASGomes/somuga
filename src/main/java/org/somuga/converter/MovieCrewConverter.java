package org.somuga.converter;

import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.entity.MovieCrew;

import java.util.List;

public class MovieCrewConverter {

    public static MovieCrewPublicDto fromEntityToPublicDto(MovieCrew movieCrew) {
        return new MovieCrewPublicDto(
                movieCrew.getId(),
                movieCrew.getFullName(),
                movieCrew.getBirthDate(),
                movieCrew.getRoles()
        );
    }

    public static List<MovieCrewPublicDto> fromEntityListToPublicDtoList(List<MovieCrew> movieCrew) {
        return movieCrew.stream()
                .map(MovieCrewConverter::fromEntityToPublicDto)
                .toList();
    }

    public static MovieCrew fromCreateDtoToEntity(MovieCrewCreateDto movieCrewCreateDto) {
        return new MovieCrew(
                movieCrewCreateDto.fullName(),
                movieCrewCreateDto.birthDate()
        );
    }

}
