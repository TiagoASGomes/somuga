package org.somuga.converter;

import org.somuga.dto.like.LikePublicDto;
import org.somuga.dto.media.MediaPublicDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.Like;

import java.util.List;

public class LikeConverter {

    public static LikePublicDto fromEntityToPublicDto(Like like) {
        UserPublicDto user = UserConverter.fromEntityToPublicDto(like.getUser());
        MediaPublicDto media = MediaConverter.fromMediaEntityToPublicDto(like.getMedia());
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

}
