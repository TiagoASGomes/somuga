package org.somuga.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ReviewListDto(
        @Schema(description = "List of reviews")
        List<ReviewPublicDto> reviews,
        @Schema(description = "Total number of reviews fitting the search criteria", example = "10")
        Long count
) {
}
