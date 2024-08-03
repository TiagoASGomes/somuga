package org.somuga.dto.game_genre;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for displaying a game genre")
public record GameGenrePublicDto(
        @Schema(description = "ID of the genre", example = "1")
        Long id,
        @Schema(description = "Name of the genre", example = "Action")
        String genreName
) {
}
