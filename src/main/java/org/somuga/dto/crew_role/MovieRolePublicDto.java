package org.somuga.dto.crew_role;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "Movie role public data transfer object")
public record MovieRolePublicDto(
        @Schema(description = "The crew member id", example = "1")
        Long id,
        @Schema(description = "The crew member full name", example = "Tom Hanks")
        String fullName,
        @Schema(description = "The crew member birth date", example = "1956-07-09")
        Date birthDate,
        @Schema(description = "The crew member role in the movie", example = "Actor")
        String movieRole,
        @Schema(description = "The character played the member", example = "Forrest Gump")
        String characterName
) {
}
