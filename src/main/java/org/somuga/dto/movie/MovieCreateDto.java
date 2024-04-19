package org.somuga.dto.movie;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.List;

import static org.somuga.message.Messages.INVALID_TITLE;

public record MovieCreateDto(
        @NotBlank(message = INVALID_TITLE)
        String title,
        List<String> actors,
        String producer,
        Date releaseDate
) {
}
