package org.somuga.dto.crew_role;

public record CrewRoleCreateDto(
        Long movieCrewId,
        String movieRole,
        String characterName
) {
}
