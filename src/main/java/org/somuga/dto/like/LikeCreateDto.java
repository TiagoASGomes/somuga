package org.somuga.dto.like;

import jakarta.validation.constraints.Min;

import static org.somuga.util.message.Messages.ID_GREATER_THAN_0;

public record LikeCreateDto(
        @Min(value = 0, message = ID_GREATER_THAN_0)
        Long mediaId
) {
}
