package org.somuga.dto.movie_crew;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MovieCrewListDto(
        @Schema(description = "List of movie crew members")
        List<MovieCrewPublicDto> movieCrews,
        @Schema(description = "Total number of movie crew members fitting the search criteria", example = "1")
        Long count
) {
}
