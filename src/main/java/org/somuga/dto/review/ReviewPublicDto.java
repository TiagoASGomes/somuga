package org.somuga.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import org.somuga.dto.user.UserPublicDto;

@Schema(description = "DTO for a review")
public record ReviewPublicDto(
        @Schema(description = "Unique identifier of the review", example = "1")
        Long id,
        @Schema(description = "User who wrote the review")
        UserPublicDto user,
        @Schema(description = "Unique identifier of the media", example = "1")
        Long mediaId,
        @Schema(description = "Score of the review", example = "10")
        int reviewScore,
        @Schema(description = "Written review", example = "This movie was great!")
        String writtenReview
) {
}
