package org.somuga.dto.like;

import org.somuga.dto.media.MediaPublicDto;
import org.somuga.dto.user.UserPublicDto;

public record LikePublicDto(
        Long id,
        UserPublicDto user,
        MediaPublicDto media
) {
}
