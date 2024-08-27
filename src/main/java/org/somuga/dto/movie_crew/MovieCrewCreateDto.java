package org.somuga.dto.movie_crew;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static org.somuga.util.message.Messages.INVALID_NAME;
import static org.somuga.util.message.Messages.INVALID_NAME_SIZE;

@Schema(description = "DTO for creating a movie crew")
public record MovieCrewCreateDto(
        @Schema(description = "Full name of the cast member", example = "John Doe")
        @NotBlank(message = INVALID_NAME)
        @Size(min = 3, max = 100, message = INVALID_NAME_SIZE)
        String fullName
) {
}
