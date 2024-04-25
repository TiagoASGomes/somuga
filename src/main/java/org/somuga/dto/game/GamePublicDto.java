package org.somuga.dto.game;

import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.dto.media.MediaPublicDto;
import org.somuga.dto.platform.PlatformPublicDto;

import java.util.Date;
import java.util.List;

public record GamePublicDto(
        Long id,
        String title,
        Date releaseDate,
        DeveloperPublicDto developer,
        List<GameGenrePublicDto> genres,
        List<PlatformPublicDto> platforms,
        Double price,
        String description,
        int reviews,
        int likes
) implements MediaPublicDto {
}
