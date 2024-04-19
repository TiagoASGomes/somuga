package org.somuga.converter;

import org.somuga.dto.like.LikePublicDto;
import org.somuga.dto.media.MediaPublicDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.Game;
import org.somuga.entity.Like;
import org.somuga.entity.Media;
import org.somuga.entity.Movie;

import java.util.List;

public class LikeConverter {

    public static LikePublicDto fromEntityToPublicDto(Like like) {
        UserPublicDto user = UserConverter.fromEntityToPublicDto(like.getUser());
        MediaPublicDto media = fromMediaEntityToPublicDto(like.getMedia());
        return new LikePublicDto(
                like.getId(),
                user,
                media);
    }

    public static List<LikePublicDto> fromEntityListToPublidDtoList(List<Like> likes) {
        return likes.stream()
                .map(LikeConverter::fromEntityToPublicDto)
                .toList();
    }

    public static MediaPublicDto fromMediaEntityToPublicDto(Media media) {
        if (media == null) return null;
        return switch (media.getMediaType()) {
            case GAME -> GameConverter.fromEntityToPublicDto((Game) media);
            case MOVIE -> MovieConverter.fromEntityToPublicDto((Movie) media);
        };
    }
}
