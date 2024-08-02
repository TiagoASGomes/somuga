package org.somuga.service;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.somuga.converter.LikeConverter;
import org.somuga.converter.MediaConverter;
import org.somuga.converter.UserConverter;
import org.somuga.dto.like.LikeCreateDto;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.entity.Game;
import org.somuga.entity.Like;
import org.somuga.entity.User;
import org.somuga.enums.MediaType;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.like.AlreadyLikedException;
import org.somuga.exception.like.LikeNotFoundException;
import org.somuga.repository.LikeRepository;
import org.somuga.service.interfaces.IMediaService;
import org.somuga.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.somuga.util.message.Messages.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class LikeServiceTest {

    private static MockedStatic<LikeConverter> likeConverter;

    private final Like like = Like.builder()
            .id(1L)
            .user(User.builder().id("user").build())
            .media(Game.builder().id(1L).mediaType(MediaType.GAME).build())
            .build();
    private final LikePublicDto likePublicDto = new LikePublicDto(
            1L,
            UserConverter.fromEntityToPublicDto(like.getUser()),
            MediaConverter.fromMediaEntityToPublicDto(like.getMedia())
    );

    @Autowired
    private LikeService likeService;
    @MockBean
    private LikeRepository likeRepository;
    @MockBean
    private IUserService userService;
    @MockBean
    private IMediaService mediaService;

    @BeforeAll
    static void setUp() {
        likeConverter = mockStatic(LikeConverter.class);
    }

    @AfterAll
    static void tearDown() {
        likeConverter.close();
    }

    @AfterEach
    void reset() {
        likeConverter.reset();
    }

    @Test
    @DisplayName("Test getAllByUserId should return a list of LikePublicDto")
    void getAllByUserId() {
        Pageable page = PageRequest.of(0, 10);
        List<Like> likes = List.of(like);
        Page<Like> likePage = new PageImpl<>(likes);
        List<LikePublicDto> likePublicDtos = List.of(likePublicDto);

        likeConverter.when(() -> LikeConverter.fromEntityListToPublicDtoList(likes)).thenReturn(likePublicDtos);
        Mockito.when(likeRepository.findByUserId("user", page)).thenReturn(likePage);

        List<LikePublicDto> result = likeService.getAllByUserId("user", page);

        assertEquals(likePublicDtos, result);

        likeConverter.verify(() -> LikeConverter.fromEntityListToPublicDtoList(likes));
        likeConverter.verifyNoMoreInteractions();
        Mockito.verify(likeRepository).findByUserId("user", page);
        Mockito.verifyNoMoreInteractions(likeRepository);
    }

    @Test
    @DisplayName("Test getAllByMediaId should return a list of LikePublicDto")
    void getAllByMediaId() {
        Pageable page = PageRequest.of(0, 10);
        List<Like> likes = List.of(like);
        Page<Like> likePage = new PageImpl<>(likes);
        List<LikePublicDto> likePublicDtos = List.of(likePublicDto);

        likeConverter.when(() -> LikeConverter.fromEntityListToPublicDtoList(likes)).thenReturn(likePublicDtos);
        Mockito.when(likeRepository.findByMediaId(1L, page)).thenReturn(likePage);

        List<LikePublicDto> result = likeService.getAllByMediaId(1L, page);

        assertEquals(likePublicDtos, result);

        likeConverter.verify(() -> LikeConverter.fromEntityListToPublicDtoList(likes));
        likeConverter.verifyNoMoreInteractions();
        Mockito.verify(likeRepository).findByMediaId(1L, page);
        Mockito.verifyNoMoreInteractions(likeRepository);
    }

    @Test
    @DisplayName("Test create should return a LikePublicDto")
    @WithMockUser(username = "user")
    void create() throws Exception {
        LikeCreateDto likeCreateDto = new LikeCreateDto(1L);

        likeConverter.when(() -> LikeConverter.fromCreateDtoToEntity(like.getUser(), like.getMedia())).thenReturn(like);
        likeConverter.when(() -> LikeConverter.fromEntityToPublicDto(like)).thenReturn(likePublicDto);
        Mockito.when(likeRepository.findByMediaIdAndUserId(1L, "user")).thenReturn(Optional.empty());
        Mockito.when(userService.findById("user")).thenReturn(like.getUser());
        Mockito.when(mediaService.findById(1L)).thenReturn(like.getMedia());
        Mockito.when(likeRepository.save(like)).thenReturn(like);

        LikePublicDto result = likeService.create(likeCreateDto);

        assertEquals(likePublicDto, result);

        likeConverter.verify(() -> LikeConverter.fromCreateDtoToEntity(like.getUser(), like.getMedia()));
        likeConverter.verify(() -> LikeConverter.fromEntityToPublicDto(like));
        likeConverter.verifyNoMoreInteractions();
        Mockito.verify(likeRepository).findByMediaIdAndUserId(1L, "user");
        Mockito.verify(userService).findById("user");
        Mockito.verify(mediaService).findById(1L);
        Mockito.verify(likeRepository).save(like);
        Mockito.verifyNoMoreInteractions(likeRepository, userService, mediaService);
    }

    @Test
    @DisplayName("Test create should throw AlreadyLikedException")
    @WithMockUser(username = "user")
    void createShouldThrowAlreadyLikedException() {
        LikeCreateDto likeCreateDto = new LikeCreateDto(1L);

        Mockito.when(likeRepository.findByMediaIdAndUserId(1L, "user")).thenReturn(Optional.of(like));

        String errorMessage = assertThrows(AlreadyLikedException.class, () -> likeService.create(likeCreateDto)).getMessage();

        assertEquals(ALREADY_LIKED, errorMessage);
        Mockito.verify(likeRepository).findByMediaIdAndUserId(1L, "user");
        Mockito.verifyNoMoreInteractions(likeRepository);
        verifyNoInteractions(userService, mediaService);
        likeConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test delete should delete a like")
    @WithMockUser(username = "user")
    void delete() throws Exception {
        Mockito.when(likeRepository.findById(1L)).thenReturn(Optional.of(like));

        likeService.delete(1L);

        Mockito.verify(likeRepository).findById(1L);
        Mockito.verify(likeRepository).deleteById(1L);
        Mockito.verifyNoMoreInteractions(likeRepository);
        verifyNoInteractions(userService, mediaService);
        likeConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test delete should throw LikeNotFoundException")
    @WithMockUser(username = "user")
    void deleteShouldThrowLikeNotFoundException() {
        Mockito.when(likeRepository.findById(1L)).thenReturn(Optional.empty());

        String message = assertThrows(LikeNotFoundException.class, () -> likeService.delete(1L)).getMessage();


        assertEquals(LIKE_NOT_FOUND + 1L, message);
        Mockito.verify(likeRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(likeRepository);
        verifyNoInteractions(userService, mediaService);
        likeConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test delete with different user should throw InvalidPermissionException")
    @WithMockUser(username = "user2")
    void deleteWithInvalidPermission() {
        Mockito.when(likeRepository.findById(1L)).thenReturn(Optional.of(like));

        String message = assertThrows(InvalidPermissionException.class, () -> likeService.delete(1L)).getMessage();

        assertEquals(UNAUTHORIZED_DELETE, message);
        Mockito.verify(likeRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(likeRepository);
        verifyNoInteractions(userService, mediaService);
        likeConverter.verifyNoInteractions();
    }
}