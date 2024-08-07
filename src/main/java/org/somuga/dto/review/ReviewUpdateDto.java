package org.somuga.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import static org.somuga.util.message.Messages.INVALID_SCORE;
import static org.somuga.util.message.Messages.MAX_REVIEW_CHARACTERS;

@Schema(description = "DTO for updating a review")
public record ReviewUpdateDto(
        @Schema(description = "Score of the review", example = "10")
        @Min(value = 1, message = INVALID_SCORE)
        @Max(value = 10, message = INVALID_SCORE)
        Integer reviewScore,
        @Schema(description = "Written review", example = "This movie was great!")
        @Size(max = 1024, message = MAX_REVIEW_CHARACTERS)
        String writtenReview
) {
}
