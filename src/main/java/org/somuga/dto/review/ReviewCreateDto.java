package org.somuga.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import static org.somuga.util.message.Messages.*;

public record ReviewCreateDto(
        @Min(value = 0, message = ID_GREATER_THAN_0)
        Long userId,
        @Min(value = 0, message = ID_GREATER_THAN_0)
        Long mediaId,
        @Min(value = 1, message = INVALID_SCORE)
        @Max(value = 10, message = INVALID_SCORE)
        Integer reviewScore,
        @Size(max = 1024, message = MAX_REVIEW_CHARACTERS)
        String writtenReview
) {
}
