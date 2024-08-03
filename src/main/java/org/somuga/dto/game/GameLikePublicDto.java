package org.somuga.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Game like public DTO")
public record GameLikePublicDto(
        @Schema(description = "Game")
        GamePublicDto game,
        @Schema(description = "If the user liked the game", example = "true")
        boolean liked
) {
}
