package org.somuga.converter;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.somuga.dto.developer.DeveloperPublicDto;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GameLikePublicDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.entity.Developer;
import org.somuga.entity.Game;
import org.somuga.entity.GameGenre;
import org.somuga.entity.Platform;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class GameConverterTest {

    private static MockedStatic<DeveloperConverter> developerConverterMockedStatic;
    private static MockedStatic<GameGenreConverter> gameGenreConverterMockedStatic;
    private static MockedStatic<PlatformConverter> platformConverterMockedStatic;

    private final Developer developer = Developer.builder()
            .id(1L)
            .developerName("Developer Name")
            .build();

    private final DeveloperPublicDto developerPublicDto = new DeveloperPublicDto(
            1L,
            "Developer Name",
            new ArrayList<>(0)
    );

    private final List<GameGenre> genres = List.of(
            GameGenre.builder()
                    .id(1L)
                    .genre("Genre 1")
                    .build()
    );

    private final List<GameGenrePublicDto> gameGenrePublicDtos = List.of(
            new GameGenrePublicDto(1L, "Genre 1")
    );

    private final List<Platform> platforms = List.of(
            Platform.builder()
                    .id(1L)
                    .platformName("Platform 1")
                    .build()
    );

    private final List<PlatformPublicDto> platformPublicDtos = List.of(
            new PlatformPublicDto(1L, "Platform 1")
    );

    @BeforeAll
    static void setUp() {
        developerConverterMockedStatic = Mockito.mockStatic(DeveloperConverter.class);
        gameGenreConverterMockedStatic = Mockito.mockStatic(GameGenreConverter.class);
        platformConverterMockedStatic = Mockito.mockStatic(PlatformConverter.class);
    }

    @AfterAll
    static void tearDownAll() {
        developerConverterMockedStatic.close();
        gameGenreConverterMockedStatic.close();
        platformConverterMockedStatic.close();
    }

    @AfterEach
    void init() {
        developerConverterMockedStatic.reset();
        gameGenreConverterMockedStatic.reset();
        platformConverterMockedStatic.reset();
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should convert entity to public dto")
    void fromEntityToPublicDto() {
        Game game = Game.builder()
                .id(1L)
                .title("Game Title")
                .releaseDate(new Date())
                .price(10.0)
                .description("Game Description")
                .mediaUrl("http://media.com")
                .imageUrl("http://image.com")
                .developer(developer)
                .genres(genres)
                .platforms(platforms)
                .build();

        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityToPublicDto(developer))
                .thenReturn(developerPublicDto);
        gameGenreConverterMockedStatic.when(() -> GameGenreConverter.fromEntityListToPublicDtoList(genres))
                .thenReturn(gameGenrePublicDtos);
        platformConverterMockedStatic.when(() -> PlatformConverter.fromEntityListToPublicDtoList(platforms))
                .thenReturn(platformPublicDtos);

        GamePublicDto gamePublicDto = GameConverter.fromEntityToPublicDto(game);

        assertEquals(game.getId(), gamePublicDto.id());
        assertEquals(game.getTitle(), gamePublicDto.title());
        assertEquals(game.getReleaseDate(), gamePublicDto.releaseDate());
        assertEquals(game.getPrice(), gamePublicDto.price());
        assertEquals(game.getDescription(), gamePublicDto.description());
        assertEquals(game.getMediaUrl(), gamePublicDto.mediaUrl());
        assertEquals(game.getImageUrl(), gamePublicDto.imageUrl());
        assertEquals(game.getGenres().size(), gamePublicDto.genres().size());
        assertEquals(game.getPlatforms().size(), gamePublicDto.platforms().size());

        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityToPublicDto(developer));
        gameGenreConverterMockedStatic.verify(() -> GameGenreConverter.fromEntityListToPublicDtoList(genres));
        platformConverterMockedStatic.verify(() -> PlatformConverter.fromEntityListToPublicDtoList(platforms));
        developerConverterMockedStatic.verifyNoMoreInteractions();
        gameGenreConverterMockedStatic.verifyNoMoreInteractions();
        platformConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when entity is null")
    void fromEntityToPublicDtoReturnNull() {
        GamePublicDto gamePublicDto = GameConverter.fromEntityToPublicDto(null);
        assertNull(gamePublicDto);

        developerConverterMockedStatic.verifyNoInteractions();
        gameGenreConverterMockedStatic.verifyNoInteractions();
        platformConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return empty entity when entity is empty")
    void fromEntityToPublicDtoReturnEmpty() {
        Game game = new Game();

        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityToPublicDto(null))
                .thenReturn(null);
        gameGenreConverterMockedStatic.when(() -> GameGenreConverter.fromEntityListToPublicDtoList(null))
                .thenReturn(new ArrayList<>(0));
        platformConverterMockedStatic.when(() -> PlatformConverter.fromEntityListToPublicDtoList(null))
                .thenReturn(new ArrayList<>(0));

        GamePublicDto gamePublicDto = GameConverter.fromEntityToPublicDto(game);

        assertNull(gamePublicDto.id());
        assertNull(gamePublicDto.title());
        assertNull(gamePublicDto.releaseDate());
        assertNull(gamePublicDto.price());
        assertNull(gamePublicDto.description());
        assertNull(gamePublicDto.mediaUrl());
        assertNull(gamePublicDto.imageUrl());
        assertEquals(0, gamePublicDto.genres().size());
        assertEquals(0, gamePublicDto.platforms().size());

        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityToPublicDto(null));
        gameGenreConverterMockedStatic.verify(() -> GameGenreConverter.fromEntityListToPublicDtoList(null));
        platformConverterMockedStatic.verify(() -> PlatformConverter.fromEntityListToPublicDtoList(null));
        developerConverterMockedStatic.verifyNoMoreInteractions();
        gameGenreConverterMockedStatic.verifyNoMoreInteractions();
        platformConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should convert list of entities to list of public dtos")
    void fromEntityListToPublicDtoList() {
        Game game = Game.builder()
                .id(1L)
                .title("Game Title")
                .releaseDate(new Date())
                .price(10.0)
                .description("Game Description")
                .mediaUrl("http://media.com")
                .imageUrl("http://image.com")
                .developer(developer)
                .genres(genres)
                .platforms(platforms)
                .build();

        List<Game> games = List.of(game, game);

        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityToPublicDto(developer))
                .thenReturn(developerPublicDto);
        gameGenreConverterMockedStatic.when(() -> GameGenreConverter.fromEntityListToPublicDtoList(genres))
                .thenReturn(gameGenrePublicDtos);
        platformConverterMockedStatic.when(() -> PlatformConverter.fromEntityListToPublicDtoList(platforms))
                .thenReturn(platformPublicDtos);

        List<GamePublicDto> gamePublicDtos = GameConverter.fromEntityListToPublicDtoList(games);

        assertEquals(games.size(), gamePublicDtos.size());

        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityToPublicDto(developer), Mockito.times(games.size()));
        gameGenreConverterMockedStatic.verify(() -> GameGenreConverter.fromEntityListToPublicDtoList(genres), Mockito.times(games.size()));
        platformConverterMockedStatic.verify(() -> PlatformConverter.fromEntityListToPublicDtoList(platforms), Mockito.times(games.size()));
        developerConverterMockedStatic.verifyNoMoreInteractions();
        gameGenreConverterMockedStatic.verifyNoMoreInteractions();
        platformConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when list of entities is empty")
    void fromEntityListToPublicDtoListReturnEmpty() {
        List<Game> games = List.of();

        List<GamePublicDto> gamePublicDtos = GameConverter.fromEntityListToPublicDtoList(games);

        assertEquals(0, gamePublicDtos.size());
        developerConverterMockedStatic.verifyNoInteractions();
        gameGenreConverterMockedStatic.verifyNoInteractions();
        platformConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when list of entities is null")
    void fromEntityListToPublicDtoListReturnNull() {
        List<GamePublicDto> gamePublicDtos = GameConverter.fromEntityListToPublicDtoList(null);

        assertEquals(0, gamePublicDtos.size());
        developerConverterMockedStatic.verifyNoInteractions();
        gameGenreConverterMockedStatic.verifyNoInteractions();
        platformConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert create dto to entity")
    void fromCreateDtoToEntity() {
        GameCreateDto gameDto = new GameCreateDto(
                "Game Title",
                new Date(),
                1L,
                List.of(1L, 2L),
                List.of(1L, 2L),
                10.0,
                "Game Description",
                "http://media.com",
                "http://image.com"
        );

        Game game = GameConverter.fromCreateDtoToEntity(gameDto);

        assertEquals(gameDto.title(), game.getTitle());
        assertEquals(gameDto.releaseDate(), game.getReleaseDate());
        assertEquals(gameDto.price(), game.getPrice());
        assertEquals(gameDto.description(), game.getDescription());
        assertEquals(gameDto.mediaUrl(), game.getMediaUrl());
        assertEquals(gameDto.imageUrl(), game.getImageUrl());
        assertNull(game.getDeveloper());
        assertNull(game.getGenres());
        assertNull(game.getPlatforms());
        developerConverterMockedStatic.verifyNoInteractions();
        gameGenreConverterMockedStatic.verifyNoInteractions();
        platformConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return null when create dto is null")
    void fromCreateDtoToEntityReturnEmpty() {
        Game game = GameConverter.fromCreateDtoToEntity(null);

        assertNull(game);
        developerConverterMockedStatic.verifyNoInteractions();
        gameGenreConverterMockedStatic.verifyNoInteractions();
        platformConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromEntityToPublicLikeDto should convert entity to public like dto")
    void fromEntityToPublicLikeDto() {
        Game game = Game.builder()
                .id(1L)
                .title("Game Title")
                .releaseDate(new Date())
                .price(10.0)
                .description("Game Description")
                .mediaUrl("http://media.com")
                .imageUrl("http://image.com")
                .developer(developer)
                .genres(genres)
                .platforms(platforms)
                .build();

        developerConverterMockedStatic.when(() -> DeveloperConverter.fromEntityToPublicDto(developer))
                .thenReturn(developerPublicDto);
        gameGenreConverterMockedStatic.when(() -> GameGenreConverter.fromEntityListToPublicDtoList(genres))
                .thenReturn(gameGenrePublicDtos);
        platformConverterMockedStatic.when(() -> PlatformConverter.fromEntityListToPublicDtoList(platforms))
                .thenReturn(platformPublicDtos);

        GameLikePublicDto gameLikePublicDto = GameConverter.fromEntityToPublicLikeDto(game, true);

        assertEquals(game.getId(), gameLikePublicDto.game().id());
        assertEquals(game.getTitle(), gameLikePublicDto.game().title());
        assertEquals(game.getReleaseDate(), gameLikePublicDto.game().releaseDate());
        assertEquals(game.getPrice(), gameLikePublicDto.game().price());
        assertEquals(game.getDescription(), gameLikePublicDto.game().description());
        assertEquals(game.getMediaUrl(), gameLikePublicDto.game().mediaUrl());
        assertEquals(game.getImageUrl(), gameLikePublicDto.game().imageUrl());
        assertEquals(game.getDeveloper().getId(), gameLikePublicDto.game().developer().id());
        assertEquals(game.getGenres().size(), gameLikePublicDto.game().genres().size());
        assertEquals(game.getPlatforms().size(), gameLikePublicDto.game().platforms().size());
        assertTrue(gameLikePublicDto.liked());

        developerConverterMockedStatic.verify(() -> DeveloperConverter.fromEntityToPublicDto(developer));
        gameGenreConverterMockedStatic.verify(() -> GameGenreConverter.fromEntityListToPublicDtoList(genres));
        platformConverterMockedStatic.verify(() -> PlatformConverter.fromEntityListToPublicDtoList(platforms));
        developerConverterMockedStatic.verifyNoMoreInteractions();
        gameGenreConverterMockedStatic.verifyNoMoreInteractions();
        platformConverterMockedStatic.verifyNoMoreInteractions();
    }
}