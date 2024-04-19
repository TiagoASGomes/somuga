package org.somuga.dto.game;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.List;

import static org.somuga.message.Messages.INVALID_TITLE;

public record GameCreateDto(
        @NotBlank(message = INVALID_TITLE)
        String title,
        Date releaseDate,
        String company,
        String genre,
        List<String> platforms
) {
}
