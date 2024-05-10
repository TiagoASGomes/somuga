package org.somuga.dto.crew_role;

import java.util.Date;

public record MovieRolePublicDto(
        Long id,
        String fullName,
        Date birthDate,
        String movieRole,
        String characterName
) {
}
