package org.somuga.dto.crew_role;

import java.util.Date;

public record CrewRolePublicDto(
        String movieRole,
        String characterName,
        String movieTitle,
        Date movieReleaseDate
) {
}
