package org.somuga.dto.crew_role;

import java.util.Date;

public record CrewRolePublicDto(
        String fullName,
        Date birthDate,
        String movieRole,
        String characterName
) {
}
