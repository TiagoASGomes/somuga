package org.somuga.dto.game_genre;

import jakarta.validation.constraints.NotBlank;

import static org.somuga.message.Messages.INVALID_GENRE_NAME;

public record GameGenreCreateDto(
        @NotBlank(message = INVALID_GENRE_NAME)
        String genreName
) {
}
