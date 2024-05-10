package org.somuga.dto.movie;

import org.somuga.dto.crew_role.MovieRolePublicDto;
import org.somuga.dto.media.MediaPublicDto;

import java.util.Date;
import java.util.List;

public record MoviePublicDto(
        Long id,
        String title,
        Date releaseDate,
        String description,
        Integer duration,
        List<MovieRolePublicDto> crew,
        String mediaUrl,
        String imageUrl,
        int likes,
        int reviews
) implements MediaPublicDto {
}
