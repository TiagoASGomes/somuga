package org.somuga.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for a movie with like status")
public record MovieLikePublicDto(
        @Schema(description = "Movie")
        MoviePublicDto movie,
        @Schema(description = "If the user liked the movie", example = "true")
        boolean liked
) {
}
