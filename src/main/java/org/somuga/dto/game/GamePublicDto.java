package org.somuga.dto.game;

import org.somuga.dto.media.MediaPublicDto;

import java.util.Date;
import java.util.List;

public record GamePublicDto(
        Long id,
        String title,
        Date releaseDate,
        String mediaType,
        String company,
        String genre,
        List<String> platforms
) implements MediaPublicDto {
}
