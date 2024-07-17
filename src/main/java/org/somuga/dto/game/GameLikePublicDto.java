package org.somuga.dto.game;

public record GameLikePublicDto(
        GamePublicDto game,
        boolean liked
) {
}
