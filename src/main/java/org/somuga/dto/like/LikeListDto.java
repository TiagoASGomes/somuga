package org.somuga.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record LikeListDto(
        @Schema(description = "List of likes")
        List<LikePublicDto> likes,
        @Schema(description = "Total number of likes fitting the criteria", example = "10")
        Long count
) {
}
