package org.somuga.dto.crew_role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Movie role create DTO")
public record MovieRoleCreateDto(
        @Schema(description = "The movie crew member ID", example = "1")
        @Min(1)
        @NotNull
        Long movieCrewId,
        @Schema(description = "The movie ID", example = "1")
        @NotNull
        String movieRole,
        @Schema(description = "The character name, if the role is actor", example = "John Doe")
        @Size(min = 1, max = 255)
        String characterName
) {
}
