package org.somuga.dto.movie_crew;

import java.util.Date;

public record MovieCrewCreateDto(
        String name,
        Date birthDate
) {
}
