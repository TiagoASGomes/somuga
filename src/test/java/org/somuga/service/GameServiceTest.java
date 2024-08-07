package org.somuga.service;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.somuga.converter.DeveloperConverter;
import org.somuga.converter.GameConverter;
import org.somuga.converter.GameGenreConverter;
import org.somuga.converter.PlatformConverter;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GameLikePublicDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.*;
import org.somuga.enums.MediaType;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.somuga.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.somuga.util.message.Messages.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class GameServiceTest {

    private static MockedStatic<GameConverter> gameConverter;
    private final Developer developer = Developer.builder().id(1L).developerName("developer").build();
    private final GameGenre genre = GameGenre.builder().id(1L).genre("genre").build();
    private final Platform platform = Platform.builder().id(1L).platformName("platform").build();
    private final Date date = new Date();
    private final Game game = Game.builder().id(1L)
            .title("game")
            .mediaType(MediaType.GAME)
            .description("description")
            .releaseDate(date)
            .imageUrl("image")
            .mediaUrl("media")
            .price(10.1)
            .developer(developer)
            .genres(new ArrayList<>(List.of(genre)))
            .platforms(new ArrayList<>(List.of(platform)))
            .likes(new ArrayList<>())
            .reviews(new ArrayList<>())
            .build();
    private final GamePublicDto gamePublicDto = new GamePublicDto(
            1L,
            "game",
            date,
            DeveloperConverter.fromEntityToPublicDto(developer),
            GameGenreConverter.fromEntityListToPublicDtoList(List.of(genre)),
            PlatformConverter.fromEntityListToPublicDtoList(List.of(platform)),
            10.1,
            "description",
            0,
            0,
            "image",
            "media"
    );

    @MockBean
    private GameRepository gameRepo;
    @MockBean
    private DeveloperService developerService;
    @MockBean
    private PlatformService platformService;
    @MockBean
    private GameGenreService genreService;
    @Autowired
    private GameService gameService;

    @BeforeAll
    public static void setUp() {
        gameConverter = mockStatic(GameConverter.class);
    }

    @AfterAll
    public static void tearDown() {
        gameConverter.close();
    }

    @AfterEach
    void reset() {
        gameConverter.reset();
    }

    @Test
    @DisplayName("Test getAll method with no parameters and expect to return a list of GamePublicDto")
    void getAll() {
        List<Game> games = List.of(game);
        Page<Game> gamesPage = new PageImpl<>(games);
        List<GamePublicDto> gamePublicDtos = List.of(gamePublicDto);
        Pageable pageable = PageRequest.of(0, 10);

        gameConverter.when(() -> GameConverter.fromEntityListToPublicDtoList(games)).thenReturn(gamePublicDtos);
        Mockito.when(gameRepo.findAll(Mockito.<Specification<Game>>any(), Mockito.any(Pageable.class))).thenReturn(gamesPage);

        List<GamePublicDto> result = gameService.getAll(pageable, null, null, null, null);

        assertEquals(gamePublicDtos, result);
        gameConverter.verify(() -> GameConverter.fromEntityListToPublicDtoList(games));
        gameConverter.verifyNoMoreInteractions();
        Mockito.verify(gameRepo).findAll(Mockito.<Specification<Game>>any(), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(gameRepo);
    }

    @Test
    @DisplayName("Test getAll method with parameters and expect to return a list of GamePublicDto")
    void getAllWithParameters() {
        List<Game> games = List.of(game);
        Page<Game> gamesPage = new PageImpl<>(games);
        List<GamePublicDto> gamePublicDtos = List.of(gamePublicDto);
        Pageable pageable = PageRequest.of(0, 10);

        gameConverter.when(() -> GameConverter.fromEntityListToPublicDtoList(games)).thenReturn(gamePublicDtos);
        Mockito.when(gameRepo.findAll(Mockito.<Specification<Game>>any(), Mockito.any(Pageable.class))).thenReturn(gamesPage);

        List<GamePublicDto> result = gameService.getAll(pageable, "game", List.of("platform"), List.of("genre"), "developer");

        assertEquals(gamePublicDtos, result);
        gameConverter.verify(() -> GameConverter.fromEntityListToPublicDtoList(games));
        gameConverter.verifyNoMoreInteractions();
        Mockito.verify(gameRepo).findAll(Mockito.<Specification<Game>>any(), Mockito.any(Pageable.class));
        Mockito.verifyNoMoreInteractions(gameRepo);
    }

    @Test
    @DisplayName("Test getById method with authenticated user and expect to return a GameLikePublicDto")
    @WithMockUser(username = "user")
    void getById() throws Exception {
        GameLikePublicDto gameLikePublicDto = new GameLikePublicDto(gamePublicDto, false);

        Mockito.when(gameRepo.findById(game.getId())).thenReturn(Optional.of(game));
        gameConverter.when(() -> GameConverter.fromEntityToPublicLikeDto(game, false)).thenReturn(gameLikePublicDto);

        GameLikePublicDto result = gameService.getById(game.getId());

        assertEquals(gameLikePublicDto, result);
        Mockito.verify(gameRepo).findById(game.getId());
        gameConverter.verify(() -> GameConverter.fromEntityToPublicLikeDto(game, false));
        gameConverter.verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(gameRepo);
    }

    @Test
    @DisplayName("Test getById method with authenticated user and liked game and expect to return a GameLikePublicDto")
    @WithMockUser(username = "user")
    void getByIdLiked() throws Exception {
        GameLikePublicDto gameLikePublicDto = new GameLikePublicDto(gamePublicDto, true);

        game.getLikes().add(Like.builder()
                .id(1L)
                .user(User.builder().id("user").build())
                .media(game)
                .build());

        Mockito.when(gameRepo.findById(game.getId())).thenReturn(Optional.of(game));
        gameConverter.when(() -> GameConverter.fromEntityToPublicLikeDto(game, true)).thenReturn(gameLikePublicDto);

        GameLikePublicDto result = gameService.getById(game.getId());

        assertEquals(gameLikePublicDto, result);
        Mockito.verify(gameRepo).findById(game.getId());
        gameConverter.verify(() -> GameConverter.fromEntityToPublicLikeDto(game, true));
        gameConverter.verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(gameRepo);
    }

    @Test
    @DisplayName("Test getById method with unauthenticated user and expect to return a GameLikePublicDto")
    @WithAnonymousUser
    void getByIdAnonymous() throws Exception {
        GameLikePublicDto gameLikePublicDto = new GameLikePublicDto(gamePublicDto, false);

        Mockito.when(gameRepo.findById(game.getId())).thenReturn(Optional.of(game));
        gameConverter.when(() -> GameConverter.fromEntityToPublicLikeDto(game, false)).thenReturn(gameLikePublicDto);

        GameLikePublicDto result = gameService.getById(game.getId());

        assertEquals(gameLikePublicDto, result);
        Mockito.verify(gameRepo).findById(game.getId());
        gameConverter.verify(() -> GameConverter.fromEntityToPublicLikeDto(game, false));
        gameConverter.verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(gameRepo);
    }

    @Test
    @DisplayName("Test create method and expect game to be created")
    @WithMockUser(username = "user")
    void create() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                "game",
                date,
                1L,
                List.of(1L),
                List.of(1L),
                10.1,
                "description",
                "image",
                "media"
        );
        Game convertedGame = Game.builder()
                .title("game")
                .releaseDate(date)
                .price(10.1)
                .description("description")
                .imageUrl("image")
                .mediaUrl("media")
                .build();

        Mockito.when(developerService.findById(1L)).thenReturn(developer);
        Mockito.when(genreService.findById(1L)).thenReturn(genre);
        Mockito.when(platformService.findById(1L)).thenReturn(platform);
        Mockito.when(gameRepo.save(convertedGame)).thenReturn(game);
        gameConverter.when(() -> GameConverter.fromEntityToPublicDto(game)).thenReturn(gamePublicDto);
        gameConverter.when(() -> GameConverter.fromCreateDtoToEntity(gameCreateDto)).thenReturn(convertedGame);

        GamePublicDto result = gameService.create(gameCreateDto);

        assertEquals(gamePublicDto, result);
        Mockito.verify(developerService).findById(1L);
        Mockito.verify(genreService).findById(1L);
        Mockito.verify(platformService).findById(1L);
        Mockito.verify(gameRepo).save(convertedGame);
        gameConverter.verify(() -> GameConverter.fromEntityToPublicDto(game));
        gameConverter.verify(() -> GameConverter.fromCreateDtoToEntity(gameCreateDto));
        gameConverter.verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(developerService);
        Mockito.verifyNoMoreInteractions(genreService);
        Mockito.verifyNoMoreInteractions(platformService);
        Mockito.verifyNoMoreInteractions(gameRepo);
    }

    @Test
    @DisplayName("Test create method with invalid developer id and expect DeveloperNotFoundException")
    @WithMockUser(username = "user")
    void createInvalidDeveloper() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                "game",
                date,
                1L,
                List.of(1L),
                List.of(1L),
                10.1,
                "description",
                "image",
                "media"
        );

        Mockito.when(developerService.findById(1L)).thenThrow(new DeveloperNotFoundException(DEVELOPER_NOT_FOUND + 1L));
        gameConverter.when(() -> GameConverter.fromCreateDtoToEntity(gameCreateDto)).thenReturn(Game.builder().build());

        assertThrows(DeveloperNotFoundException.class, () -> gameService.create(gameCreateDto));
        Mockito.verify(developerService).findById(1L);
        gameConverter.verify(() -> GameConverter.fromCreateDtoToEntity(gameCreateDto));
        Mockito.verifyNoMoreInteractions(developerService);
        Mockito.verifyNoInteractions(genreService);
        Mockito.verifyNoInteractions(platformService);
        Mockito.verifyNoInteractions(gameRepo);
        gameConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test create method with invalid genre id and expect GenreNotFoundException")
    @WithMockUser(username = "user")
    void createInvalidGenre() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                "game",
                date,
                1L,
                List.of(1L),
                List.of(1L),
                10.1,
                "description",
                "image",
                "media"
        );

        Mockito.when(developerService.findById(1L)).thenReturn(developer);
        Mockito.when(genreService.findById(1L)).thenThrow(new GenreNotFoundException(GENRE_NOT_FOUND + 1L));
        Mockito.when(platformService.findById(1L)).thenReturn(platform);
        gameConverter.when(() -> GameConverter.fromCreateDtoToEntity(gameCreateDto)).thenReturn(Game.builder().build());

        assertThrows(GenreNotFoundException.class, () -> gameService.create(gameCreateDto));
        Mockito.verify(developerService).findById(1L);
        Mockito.verify(genreService).findById(1L);
        Mockito.verify(platformService).findById(1L);
        gameConverter.verify(() -> GameConverter.fromCreateDtoToEntity(gameCreateDto));
        Mockito.verifyNoMoreInteractions(developerService);
        Mockito.verifyNoMoreInteractions(genreService);
        Mockito.verifyNoMoreInteractions(platformService);
        Mockito.verifyNoInteractions(gameRepo);
        gameConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test create method with invalid platform id and expect PlatformNotFoundException")
    @WithMockUser(username = "user")
    void createInvalidPlatform() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                "game",
                date,
                1L,
                List.of(1L),
                List.of(1L),
                10.1,
                "description",
                "image",
                "media"
        );

        Mockito.when(developerService.findById(1L)).thenReturn(developer);
        Mockito.when(platformService.findById(1L)).thenThrow(new PlatformNotFoundException(PLATFORM_NOT_FOUND + 1L));
        gameConverter.when(() -> GameConverter.fromCreateDtoToEntity(gameCreateDto)).thenReturn(Game.builder().build());

        assertThrows(PlatformNotFoundException.class, () -> gameService.create(gameCreateDto));
        Mockito.verify(developerService).findById(1L);
        Mockito.verify(platformService).findById(1L);
        gameConverter.verify(() -> GameConverter.fromCreateDtoToEntity(gameCreateDto));
        Mockito.verifyNoMoreInteractions(developerService);
        Mockito.verifyNoMoreInteractions(platformService);
        Mockito.verifyNoInteractions(genreService);
        Mockito.verifyNoInteractions(gameRepo);
        gameConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test update method and expect game to be updated")
    @WithMockUser(username = "user")
    void update() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                "game2",
                date,
                1L,
                List.of(1L),
                List.of(1L),
                10.1,
                "description",
                "image",
                "media"
        );
        Game convertedGame = Game.builder()
                .title("game2")
                .releaseDate(date)
                .price(10.1)
                .description("description")
                .imageUrl("image")
                .mediaUrl("media")
                .build();

        GamePublicDto gamePublicDto = new GamePublicDto(
                1L,
                "game2",
                date,
                DeveloperConverter.fromEntityToPublicDto(developer),
                GameGenreConverter.fromEntityListToPublicDtoList(List.of(genre)),
                PlatformConverter.fromEntityListToPublicDtoList(List.of(platform)),
                10.1,
                "description",
                0,
                0,
                "image",
                "media"
        );

        game.setMediaCreatorId("user");

        Mockito.when(gameRepo.findById(1L)).thenReturn(Optional.of(game));
        Mockito.when(developerService.findById(1L)).thenReturn(developer);
        Mockito.when(genreService.findById(1L)).thenReturn(genre);
        Mockito.when(platformService.findById(1L)).thenReturn(platform);
        Mockito.when(gameRepo.save(Mockito.any(Game.class))).thenReturn(convertedGame);
        gameConverter.when(() -> GameConverter.fromEntityToPublicDto(convertedGame)).thenReturn(gamePublicDto);

        GamePublicDto result = gameService.update(1L, gameCreateDto);

        assertEquals(gamePublicDto, result);
        Mockito.verify(gameRepo).findById(1L);
        Mockito.verify(developerService).findById(1L);
        Mockito.verify(genreService).findById(1L);
        Mockito.verify(platformService).findById(1L);
        Mockito.verify(gameRepo).save(Mockito.any(Game.class));
        gameConverter.verify(() -> GameConverter.fromEntityToPublicDto(convertedGame));
        gameConverter.verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(gameRepo);
        Mockito.verifyNoMoreInteractions(developerService);
        Mockito.verifyNoMoreInteractions(genreService);
        Mockito.verifyNoMoreInteractions(platformService);
    }

    @Test
    @DisplayName("Test update method different user and expect InvalidPermissionException")
    @WithMockUser(username = "user2")
    void updateDifferentUser() {
        GameCreateDto gameCreateDto = new GameCreateDto(
                "game2",
                date,
                1L,
                List.of(1L),
                List.of(1L),
                10.1,
                "description",
                "image",
                "media"
        );

        game.setMediaCreatorId("user");

        Mockito.when(gameRepo.findById(1L)).thenReturn(Optional.of(game));

        String errorMessage = assertThrows(InvalidPermissionException.class, () -> gameService.update(1L, gameCreateDto)).getMessage();

        assertEquals(UNAUTHORIZED_UPDATE, errorMessage);
        Mockito.verify(gameRepo).findById(1L);
        Mockito.verifyNoMoreInteractions(gameRepo);
        Mockito.verifyNoInteractions(developerService);
        Mockito.verifyNoInteractions(genreService);
        Mockito.verifyNoInteractions(platformService);
        gameConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test delete method and expect game to be deleted")
    @WithMockUser(username = "user")
    void delete() throws Exception {
        game.setMediaCreatorId("user");

        Mockito.when(gameRepo.findById(1L)).thenReturn(Optional.of(game));

        gameService.delete(1L);

        Mockito.verify(gameRepo).findById(1L);
        Mockito.verify(gameRepo).delete(game);
        Mockito.verifyNoMoreInteractions(gameRepo);
        Mockito.verifyNoInteractions(developerService);
        Mockito.verifyNoInteractions(genreService);
        Mockito.verifyNoInteractions(platformService);
        gameConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test delete method different user and expect InvalidPermissionException")
    @WithMockUser(username = "user2")
    void deleteDifferentUser() {
        game.setMediaCreatorId("user");

        Mockito.when(gameRepo.findById(1L)).thenReturn(Optional.of(game));

        String errorMessage = assertThrows(InvalidPermissionException.class, () -> gameService.delete(1L)).getMessage();

        assertEquals(UNAUTHORIZED_DELETE, errorMessage);
        Mockito.verify(gameRepo).findById(1L);
        Mockito.verifyNoMoreInteractions(gameRepo);
        Mockito.verifyNoInteractions(developerService);
        Mockito.verifyNoInteractions(genreService);
        Mockito.verifyNoInteractions(platformService);
        gameConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test findById method and expect game to be found")
    void findById() throws Exception {
        Mockito.when(gameRepo.findById(1L)).thenReturn(Optional.of(game));

        Game result = gameService.findById(1L);

        assertEquals(game, result);
        Mockito.verify(gameRepo).findById(1L);
        Mockito.verifyNoMoreInteractions(gameRepo);
    }

    @Test
    @DisplayName("Test findById method and expect game to not be found")
    void findByIdNotFound() {
        Mockito.when(gameRepo.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(Exception.class, () -> gameService.findById(1L)).getMessage();

        assertEquals(GAME_NOT_FOUND + 1L, errorMessage);
        Mockito.verify(gameRepo).findById(1L);
        Mockito.verifyNoMoreInteractions(gameRepo);
    }
}