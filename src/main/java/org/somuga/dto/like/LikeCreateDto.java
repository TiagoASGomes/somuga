package org.somuga.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

import static org.somuga.util.message.Messages.ID_GREATER_THAN_0;

@Schema(description = "Data transfer object for creating a like")
public record LikeCreateDto(
        @Schema(description = "The id of the media to like", example = "1")
        @Min(value = 0, message = ID_GREATER_THAN_0)
        Long mediaId
) {
}
