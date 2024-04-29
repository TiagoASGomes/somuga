package org.somuga.dto.movie_crew;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.util.Date;

import static org.somuga.message.Messages.*;

public record MovieCrewCreateDto(
        @NotBlank(message = INVALID_NAME)
        @Size(min = 1, max = 50, message = INVALID_NAME_SIZE)
        String fullName,
        @Past(message = INVALID_BIRTH_DATE)
        @NotNull(message = INVALID_BIRTH_DATE)
        Date birthDate
) {
}
