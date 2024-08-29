package org.somuga.dto.crew_role;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Crew role public DTO")
public record CrewRolePublicDto(
        @Schema(description = "The movie role", example = "Actor")
        String movieRole,
        @Schema(description = "The character name, empty if role is not actor", example = "Neo")
        String characterName,
        @Schema(description = "The movie ID", example = "1")
        Long movieId,
        @Schema(description = "The movie title", example = "The Matrix")
        String movieTitle,
        @Schema(description = "The movie release date", example = "1999-03-31")
        LocalDate movieReleaseDate
) {
}
