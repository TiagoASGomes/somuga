package org.somuga.converter;

import org.somuga.dto.media.MediaPublicDto;
import org.somuga.entity.Game;
import org.somuga.entity.Media;
import org.somuga.entity.Movie;

public class MediaConverter {
    public static MediaPublicDto fromMediaEntityToPublicDto(Media media) {
        if (media == null) return null;
        return switch (media.getMediaType()) {
            case GAME -> GameConverter.fromEntityToPublicDto((Game) media);
            case MOVIE -> MovieConverter.fromEntityToPublicDto((Movie) media);
        };
    }
}
