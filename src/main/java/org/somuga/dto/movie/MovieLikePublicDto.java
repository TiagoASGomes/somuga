package org.somuga.dto.movie;

import org.somuga.dto.crew_role.MovieRolePublicDto;

import java.util.Date;
import java.util.List;

public record MovieLikePublicDto(
        Long id,
        String title,
        Date releaseDate,
        String description,
        Integer duration,
        List<MovieRolePublicDto> crew,
        String mediaUrl,
        String imageUrl,
        int likes,
        int reviews,
        boolean liked
) {
}
