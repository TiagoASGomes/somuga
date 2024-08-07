package org.somuga.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;
import org.somuga.dto.media.MediaPublicDto;
import org.somuga.dto.user.UserPublicDto;

@Schema(description = "Data transfer object for a public like")
public record LikePublicDto(
        @Schema(description = "The id of the like", example = "1")
        Long id,
        @Schema(description = "The user who liked the media")
        UserPublicDto user,
        @Schema(description = "The media that was liked")
        MediaPublicDto media
) {
}
