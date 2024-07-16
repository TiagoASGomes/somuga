package org.somuga.testUtils;

import org.somuga.dto.game.GamePublicDto;
import org.somuga.dto.user.UserPublicDto;

public record LikeGameDto(
        Long id,
        UserPublicDto user,
        GamePublicDto media
) {
}
