package org.somuga.dto.movie;

import jakarta.validation.constraints.*;
import org.somuga.dto.crew_role.MovieRoleCreateDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

import static org.somuga.util.message.Messages.*;

public record MovieCreateDto(
        @NotBlank(message = INVALID_TITLE)
        @Size(max = 255, message = MAX_TITLE_CHARACTERS)
        String title,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past(message = INVALID_RELEASE_DATE)
        @NotNull(message = INVALID_RELEASE_DATE)
        Date releaseDate,
        @NotBlank(message = INVALID_DESCRIPTION)
        @Size(max = 1000, message = MAX_DESCRIPTION_CHARACTERS)
        String description,
        @Min(value = 1, message = INVALID_DURATION)
        @Max(value = 1440, message = INVALID_DURATION)
        @NotNull(message = INVALID_DURATION)
        Integer duration,
        @NotNull(message = INVALID_CREW_ROLE)
        @Size(min = 1, message = INVALID_CREW_ROLE)
        List<MovieRoleCreateDto> crew,
        @NotBlank(message = INVALID_MEDIA_URL)
        String mediaUrl,
        String imageUrl
) {
}
