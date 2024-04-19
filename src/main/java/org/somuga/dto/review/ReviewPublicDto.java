package org.somuga.dto.review;

import org.somuga.dto.media.MediaPublicDto;
import org.somuga.dto.user.UserPublicDto;

public record ReviewPublicDto(
        Long id,
        UserPublicDto user,
        MediaPublicDto media,
        int reviewScore,
        String writtenReview
) {
}
