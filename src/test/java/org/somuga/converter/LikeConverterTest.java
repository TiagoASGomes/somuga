package org.somuga.converter;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.Game;
import org.somuga.entity.Like;
import org.somuga.entity.User;
import org.somuga.enums.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@ActiveProfiles("test")
class LikeConverterTest {

    private static MockedStatic<UserConverter> userConverterMockedStatic;
    private static MockedStatic<MediaConverter> mediaConverterMockedStatic;

    private final User user = User.builder()
            .id("1")
            .userName("user")
            .build();

    private final UserPublicDto userPublicDto = new UserPublicDto("1", "user", new Date());

    private final Game media = Game.builder()
            .id(1L)
            .title("game")
            .mediaType(MediaType.GAME)
            .build();

    private final GamePublicDto mediaPublicDto = new GamePublicDto(1L, "game", null, null, null, null, null, null, 0, 0, null, null);

    @BeforeAll
    static void setUp() {
        userConverterMockedStatic = mockStatic(UserConverter.class);
        mediaConverterMockedStatic = mockStatic(MediaConverter.class);
    }

    @AfterAll
    static void tearDownAll() {
        userConverterMockedStatic.close();
        mediaConverterMockedStatic.close();
    }

    @AfterEach
    void tearDown() {
        userConverterMockedStatic.reset();
        mediaConverterMockedStatic.reset();
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should convert entity to public dto")
    void fromEntityToPublicDto() {
        Like like = Like.builder()
                .id(1L)
                .user(user)
                .media(media)
                .build();

        userConverterMockedStatic.when(() -> UserConverter.fromEntityToPublicDto(user)).thenReturn(userPublicDto);
        mediaConverterMockedStatic.when(() -> MediaConverter.fromMediaEntityToPublicDto(media)).thenReturn(mediaPublicDto);

        LikePublicDto likePublicDto = LikeConverter.fromEntityToPublicDto(like);

        assertEquals(like.getId(), likePublicDto.id());
        assertEquals(like.getUser().getId(), likePublicDto.user().id());
        assertEquals(like.getMedia().getId(), likePublicDto.media().id());

        userConverterMockedStatic.verify(() -> UserConverter.fromEntityToPublicDto(user));
        mediaConverterMockedStatic.verify(() -> MediaConverter.fromMediaEntityToPublicDto(media));
        userConverterMockedStatic.verifyNoMoreInteractions();
        mediaConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when entity is null")
    void fromEntityToPublicDtoShouldReturnNull() {
        LikePublicDto likePublicDto = LikeConverter.fromEntityToPublicDto(null);

        assertNull(likePublicDto);
        userConverterMockedStatic.verifyNoInteractions();
        mediaConverterMockedStatic.verifyNoInteractions();
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

        userConverterMockedStatic.when(() -> UserConverter.fromEntityToPublicDto(user)).thenReturn(userPublicDto);
        mediaConverterMockedStatic.when(() -> MediaConverter.fromMediaEntityToPublicDto(media)).thenReturn(mediaPublicDto);

        List<LikePublicDto> likePublicDtos = LikeConverter.fromEntityListToPublicDtoList(likes);

        assertEquals(likes.size(), likePublicDtos.size());
        for (int i = 0; i < likes.size(); i++) {
            assertEquals(likes.get(i).getId(), likePublicDtos.get(i).id());
            assertEquals(likes.get(i).getUser().getId(), likePublicDtos.get(i).user().id());
            assertEquals(likes.get(i).getMedia().getId(), likePublicDtos.get(i).media().id());
        }
        userConverterMockedStatic.verify(() -> UserConverter.fromEntityToPublicDto(user), Mockito.times(likes.size()));
        mediaConverterMockedStatic.verify(() -> MediaConverter.fromMediaEntityToPublicDto(media), Mockito.times(likes.size()));
        userConverterMockedStatic.verifyNoMoreInteractions();
        mediaConverterMockedStatic.verifyNoMoreInteractions();

    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when input list is empty")
    void fromEntityListToPublidDtoListShouldReturnEmptyList() {
        List<LikePublicDto> likePublicDtos = LikeConverter.fromEntityListToPublicDtoList(List.of());

        assertEquals(0, likePublicDtos.size());
        userConverterMockedStatic.verifyNoInteractions();
        mediaConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when input list is null")
    void fromEntityListToPublidDtoListShouldReturnEmptyListWhenInputListIsNull() {
        List<LikePublicDto> likePublicDtos = LikeConverter.fromEntityListToPublicDtoList(null);

        assertEquals(0, likePublicDtos.size());
        userConverterMockedStatic.verifyNoInteractions();
        mediaConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert create dto to entity")
    void fromCreateDtoToEntity() {
        Like like = LikeConverter.fromCreateDtoToEntity(user, media);

        assertNull(like.getId());
        assertEquals(user, like.getUser());
        assertEquals(media, like.getMedia());
        userConverterMockedStatic.verifyNoInteractions();
        mediaConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return like when parameters are null")
    void fromCreateDtoToEntityShouldReturnNull() {
        Like like = LikeConverter.fromCreateDtoToEntity(null, null);

        assertNotNull(like);
        assertNull(like.getId());
        assertNull(like.getUser());
        assertNull(like.getMedia());
        userConverterMockedStatic.verifyNoInteractions();
        mediaConverterMockedStatic.verifyNoInteractions();
    }

}