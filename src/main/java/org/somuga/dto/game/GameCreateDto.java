package org.somuga.dto.game;

import java.util.Date;
import java.util.List;

public record GameCreateDto(
        String title,
        Date releaseDate,
        String company,
        String genre,
        List<String> platforms

) {
}
