package org.somuga.converter;

import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewListDto;
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

    public static MovieCrewListDto fromEntityListToPublicDtoList(List<MovieCrew> movieCrew, Long count) {
        if (movieCrew == null) return new MovieCrewListDto(new ArrayList<>(), 0L);
        List<MovieCrewPublicDto> movieCrewPublicDtos = movieCrew.stream()
                .map(MovieCrewConverter::fromEntityToPublicDto)
                .toList();
        return new MovieCrewListDto(movieCrewPublicDtos, count);
    }

    public static MovieCrew fromCreateDtoToEntity(MovieCrewCreateDto movieCrewCreateDto) {
        if (movieCrewCreateDto == null) return null;
        return MovieCrew.builder()
                .fullName(movieCrewCreateDto.fullName())
                .birthDate(movieCrewCreateDto.birthDate())
                .build();
    }

}
