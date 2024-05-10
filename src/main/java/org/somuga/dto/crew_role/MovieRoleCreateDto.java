package org.somuga.dto.crew_role;

public record MovieRoleCreateDto(
        Long movieCrewId,
        String movieRole,
        String characterName
) {
}
