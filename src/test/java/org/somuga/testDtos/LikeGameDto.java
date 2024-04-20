package org.somuga.testDtos;

import org.somuga.dto.game.GamePublicDto;
import org.somuga.dto.user.UserPublicDto;

public record LikeGameDto(
        Long id,
        UserPublicDto user,
        GamePublicDto media
) {
}
