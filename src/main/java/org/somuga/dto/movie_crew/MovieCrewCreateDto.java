package org.somuga.dto.movie_crew;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static org.somuga.util.message.Messages.*;

@Schema(description = "DTO for creating a movie crew")
public record MovieCrewCreateDto(
        @Schema(description = "Full name of the cast member", example = "John Doe")
        @NotBlank(message = INVALID_NAME)
        @Size(min = 3, max = 100, message = INVALID_NAME_SIZE)
        String fullName,
        @Schema(description = "Birth date of the cast member", example = "1990-01-01")
        @Past(message = INVALID_BIRTH_DATE)
        @NotNull(message = INVALID_BIRTH_DATE)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Date birthDate
) {
}
