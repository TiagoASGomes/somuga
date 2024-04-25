package org.somuga.dto.game_genre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.somuga.message.Messages.INVALID_GENRE_NAME;

public record GameGenreCreateDto(
        @NotBlank(message = INVALID_GENRE_NAME)
        @Pattern(regexp = "^[a-zA-Z0-9 ]{1,50}$", message = INVALID_GENRE_NAME)
        String genreName
) {
}
