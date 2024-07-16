package org.somuga.testUtils;

import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.dto.user.UserPublicDto;

public record LikeMovieDto(
        Long id,
        UserPublicDto user,
        MoviePublicDto media
) {
}
