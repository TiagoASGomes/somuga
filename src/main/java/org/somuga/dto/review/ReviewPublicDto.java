package org.somuga.dto.review;

import org.somuga.dto.user.UserPublicDto;

public record ReviewPublicDto(
        Long id,
        UserPublicDto user,
        Long mediaId,
        int reviewScore,
        String writtenReview
) {
}
