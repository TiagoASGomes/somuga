package org.somuga.testDtos;

import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.dto.user.UserPublicDto;

public record ReviewMovieDto(
        Long id,
        UserPublicDto user,
        MoviePublicDto media,
        int reviewScore,
        String writtenReview
) {
}
