package org.somuga.dto.crew_role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import static org.somuga.message.Messages.*;

public record CrewRoleCreateDto(
        @Positive(message = ID_GREATER_THAN_0)
        Long movieCrewId,
        @Pattern(regexp = "^(ACTOR|DIRECTOR|PRODUCER|WRITER)$", message = INVALID_MOVIE_ROLE)
        String movieRole,
        @NotBlank(message = INVALID_CHARACTER_NAME)
        String characterName
) {
}
