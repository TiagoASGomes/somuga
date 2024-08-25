package org.somuga.converter;

import org.somuga.dto.like.LikeListDto;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.dto.media.MediaPublicDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.Like;
import org.somuga.entity.Media;
import org.somuga.entity.User;

import java.util.ArrayList;
import java.util.List;

public class LikeConverter {

    private LikeConverter() {
    }

    public static LikePublicDto fromEntityToPublicDto(Like like) {
        if (like == null) return null;
        UserPublicDto user = UserConverter.fromEntityToPublicDto(like.getUser());
        MediaPublicDto media = MediaConverter.fromMediaEntityToPublicDto(like.getMedia());
        return new LikePublicDto(
                like.getId(),
                user,
                media);
    }

    public static LikeListDto fromEntityListToPublicDtoList(List<Like> likes, Long count) {
        if (likes == null) return new LikeListDto(new ArrayList<>(0), 0L);
        List<LikePublicDto> likePublicDtos = likes.stream()
                .map(LikeConverter::fromEntityToPublicDto)
                .toList();
        return new LikeListDto(likePublicDtos, count);
    }

    public static Like fromCreateDtoToEntity(User user, Media media) {
        return Like.builder()
                .user(user)
                .media(media)
                .build();
    }
}
