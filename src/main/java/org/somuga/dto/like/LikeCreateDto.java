package org.somuga.dto.like;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import static org.somuga.util.message.Messages.ID_GREATER_THAN_0;
import static org.somuga.util.message.Messages.INVALID_ID;

public record LikeCreateDto(
        @NotBlank(message = INVALID_ID)
        String userId,
        @Min(value = 0, message = ID_GREATER_THAN_0)
        Long mediaId
) {

}
