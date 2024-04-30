package org.somuga.dto.movie;

import jakarta.validation.constraints.*;
import org.somuga.dto.crew_role.CrewRoleCreateDto;

import java.util.Date;
import java.util.List;

import static org.somuga.message.Messages.*;

public record MovieCreateDto(
        @NotBlank(message = INVALID_TITLE)
        String title,
        @Past(message = INVALID_RELEASE_DATE)
        Date releaseDate,
        @NotBlank(message = INVALID_DESCRIPTION)
        @Size(max = 1000, message = MAX_DESCRIPTION_CHARACTERS)
        String description,
        @Min(value = 1, message = INVALID_DURATION)
        @Min(value = 1440, message = INVALID_DURATION)
        Integer duration,
        @NotNull(message = INVALID_CREW_ROLE)
        List<CrewRoleCreateDto> crew
) {
}
