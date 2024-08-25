package org.somuga.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GameListDto(
        @Schema(description = "List of games")
        List<GamePublicDto> games,
        @Schema(description = "Total number of games fitting the search criteria", example = "10")
        Long count
) {
}
