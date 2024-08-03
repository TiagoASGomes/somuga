package org.somuga.dto.game_genre;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.somuga.util.message.Messages.INVALID_GENRE_NAME;

@Schema(description = "DTO for creating a new game genre")
public record GameGenreCreateDto(
        @Schema(description = "Name of the genre", example = "Action")
        @NotBlank(message = INVALID_GENRE_NAME)
        @Pattern(regexp = "^[a-zA-Z0-9 ]{1,50}$", message = INVALID_GENRE_NAME)
        String genreName
) {
}
