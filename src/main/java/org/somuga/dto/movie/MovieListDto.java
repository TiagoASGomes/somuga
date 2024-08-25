package org.somuga.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MovieListDto(
        @Schema(description = "List of movies")
        List<MoviePublicDto> movies,
        @Schema(description = "Total number of movies fitting the search criteria", example = "10")
        Long count
) {
}
