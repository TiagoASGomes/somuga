package org.somuga.dto.movie_crew;

import org.somuga.entity.MovieCrewRole;

import java.util.Date;
import java.util.List;

public record MovieCrewPublicDto(
//        TODO ver o que mais é necessário
        Long id,
        String name,
        Date birthDate,
        List<MovieCrewRole> roles

) {
}
