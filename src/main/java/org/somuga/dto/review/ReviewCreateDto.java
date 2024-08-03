package org.somuga.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import static org.somuga.util.message.Messages.*;

@Schema(description = "DTO for creating a review")
public record ReviewCreateDto(
        @Schema(description = "Unique identifier of the media", example = "1")
        @Min(value = 0, message = ID_GREATER_THAN_0)
        Long mediaId,
        @Schema(description = "Score of the review", example = "10")
        @Min(value = 1, message = INVALID_SCORE)
        @Max(value = 10, message = INVALID_SCORE)
        Integer reviewScore,
        @Schema(description = "Written review", example = "This movie was great!")
        @Size(max = 1024, message = MAX_REVIEW_CHARACTERS)
        String writtenReview
) {
}
