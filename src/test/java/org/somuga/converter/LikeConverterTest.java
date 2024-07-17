package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.entity.Game;
import org.somuga.entity.Like;
import org.somuga.entity.User;
import org.somuga.enums.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LikeConverterTest {
    //TODO mock UserConverter and MediaConverter
    private final User user = User.builder()
            .id("1")
            .userName("user")
            .build();

    private final Game media = Game.builder()
            .id(1L)
            .title("game")
            .mediaType(MediaType.GAME)
            .build();

    @Test
    @DisplayName("Test fromEntityToPublicDto should convert entity to public dto")
    void fromEntityToPublicDto() {
        Like like = Like.builder()
                .id(1L)
                .user(user)
                .media(media)
                .build();

        LikePublicDto likePublicDto = LikeConverter.fromEntityToPublicDto(like);

        assertEquals(like.getId(), likePublicDto.id());
        assertEquals(like.getUser().getId(), likePublicDto.user().id());
        assertEquals(like.getMedia().getId(), likePublicDto.media().id());
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when entity is null")
    void fromEntityToPublicDtoShouldReturnNull() {
        LikePublicDto likePublicDto = LikeConverter.fromEntityToPublicDto(null);

        assertEquals(null, likePublicDto);
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should convert list of entities to list of public dtos")
    void fromEntityListToPublicDtoList() {
        List<Like> likes = List.of(
                Like.builder()
                        .id(1L)
                        .user(user)
                        .media(media)
                        .build(),
                Like.builder()
                        .id(2L)
                        .user(user)
                        .media(media)
                        .build()
        );

        List<LikePublicDto> likePublicDtos = LikeConverter.fromEntityListToPublicDtoList(likes);

        assertEquals(likes.size(), likePublicDtos.size());
        for (int i = 0; i < likes.size(); i++) {
            assertEquals(likes.get(i).getId(), likePublicDtos.get(i).id());
            assertEquals(likes.get(i).getUser().getId(), likePublicDtos.get(i).user().id());
            assertEquals(likes.get(i).getMedia().getId(), likePublicDtos.get(i).media().id());
        }
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when input list is empty")
    void fromEntityListToPublidDtoListShouldReturnEmptyList() {
        List<LikePublicDto> likePublicDtos = LikeConverter.fromEntityListToPublicDtoList(List.of());

        assertEquals(0, likePublicDtos.size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when input list is null")
    void fromEntityListToPublidDtoListShouldReturnEmptyListWhenInputListIsNull() {
        List<LikePublicDto> likePublicDtos = LikeConverter.fromEntityListToPublicDtoList(null);

        assertEquals(0, likePublicDtos.size());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert create dto to entity")
    void fromCreateDtoToEntity() {
        Like like = LikeConverter.fromCreateDtoToEntity(user, media);

        assertNull(like.getId());
        assertEquals(user, like.getUser());
        assertEquals(media, like.getMedia());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return like when parameters are null")
    void fromCreateDtoToEntityShouldReturnNull() {
        Like like = LikeConverter.fromCreateDtoToEntity(null, null);

        assertNotNull(like);
        assertNull(like.getId());
        assertNull(like.getUser());
        assertNull(like.getMedia());
    }

}