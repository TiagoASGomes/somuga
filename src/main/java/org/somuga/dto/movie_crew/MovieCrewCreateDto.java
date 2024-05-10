package org.somuga.dto.movie_crew;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static org.somuga.util.message.Messages.*;

public record MovieCrewCreateDto(
        @NotBlank(message = INVALID_NAME)
        @Size(min = 3, max = 100, message = INVALID_NAME_SIZE)
        String fullName,
        @Past(message = INVALID_BIRTH_DATE)
        @NotNull(message = INVALID_BIRTH_DATE)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Date birthDate
) {
}
