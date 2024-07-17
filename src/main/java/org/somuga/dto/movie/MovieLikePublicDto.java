package org.somuga.dto.movie;

public record MovieLikePublicDto(
        MoviePublicDto movie,
        boolean liked
) {
}
