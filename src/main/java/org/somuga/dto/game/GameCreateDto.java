package org.somuga.dto.game;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

import static org.somuga.util.message.Messages.*;

public record GameCreateDto(
        @NotBlank(message = INVALID_TITLE)
        @Size(max = 255, message = MAX_TITLE_CHARACTERS)
        String title,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = INVALID_RELEASE_DATE)
        Date releaseDate,
        @NotNull(message = INVALID_DEVELOPER)
        String developerName,
        @NotNull(message = INVALID_GENRES)
        List<String> genres,
        @NotNull(message = INVALID_PLATFORMS)
        List<String> platformsNames,
        @Min(value = 0, message = INVALID_PRICE)
        @Max(value = 1000, message = INVALID_PRICE)
        Double price,
        @NotBlank(message = INVALID_DESCRIPTION)
        @Size(max = 1000, message = MAX_DESCRIPTION_CHARACTERS)
        String description,
        @NotBlank(message = INVALID_MEDIA_URL)
        String mediaUrl,
        String imageUrl
) {
}
