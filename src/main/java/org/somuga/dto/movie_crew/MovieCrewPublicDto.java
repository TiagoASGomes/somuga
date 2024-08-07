package org.somuga.dto.movie_crew;

import io.swagger.v3.oas.annotations.media.Schema;
import org.somuga.dto.crew_role.CrewRolePublicDto;

import java.util.Date;
import java.util.List;

@Schema(description = "DTO for a movie crew member")
public record MovieCrewPublicDto(
        @Schema(description = "Unique identifier of the crew member", example = "1")
        Long id,
        @Schema(description = "Full name of the crew member", example = "John Doe")
        String name,
        @Schema(description = "Birth date of the crew member", example = "1990-01-01")
        Date birthDate,
        @Schema(description = "Roles of the crew member")
        List<CrewRolePublicDto> roles
) {
}
