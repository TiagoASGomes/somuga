package org.somuga.dto.movie;

import org.somuga.dto.media.MediaPublicDto;

import java.util.Date;
import java.util.List;

public record MoviePublicDto(
        Long id,
        String title,
        Date releaseDate,
        String mediaType,
        List<String> actors,
        String producer
) implements MediaPublicDto {
}
