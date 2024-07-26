package org.somuga.converter;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Game;
import org.somuga.entity.Movie;
import org.somuga.enums.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@ActiveProfiles("test")
class MediaConverterTest {

    private static MockedStatic<GameConverter> gameConverterMockedStatic;
    private static MockedStatic<MovieConverter> movieConverterMockedStatic;

    @BeforeAll
    static void setUp() {
        gameConverterMockedStatic = mockStatic(GameConverter.class);
        movieConverterMockedStatic = mockStatic(MovieConverter.class);
    }

    @AfterAll
    static void tearDownAll() {
        gameConverterMockedStatic.close();
        movieConverterMockedStatic.close();
    }

    @AfterEach
    void tearDown() {
        gameConverterMockedStatic.reset();
        movieConverterMockedStatic.reset();
    }

    @Test
    @DisplayName("Test fromMediaEntityToPublicDto should convert Game entity to GamePublicDto")
    void fromGameEntityToPublicDto() {
        Game game = Game.builder()
                .id(1L)
                .title("Game Title")
                .description("Game Description")
                .mediaType(MediaType.GAME)
                .build();

        GamePublicDto responseDto = new GamePublicDto(1L, "Game Title", null, null, null, null, null, "Game Description", 0, 0, null, null);

        gameConverterMockedStatic.when(() -> GameConverter.fromEntityToPublicDto(game)).thenReturn(responseDto);

        GamePublicDto gamePublicDto = (GamePublicDto) MediaConverter.fromMediaEntityToPublicDto(game);

        assertEquals(game.getId(), gamePublicDto.id());
        assertEquals(game.getTitle(), gamePublicDto.title());
        assertEquals(game.getDescription(), gamePublicDto.description());
        gameConverterMockedStatic.verify(() -> GameConverter.fromEntityToPublicDto(game));
        movieConverterMockedStatic.verifyNoInteractions();
        gameConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test fromMediaEntityToPublicDto should convert Movie entity to MoviePublicDto")
    void fromMovieEntityToPublicDto() {
        Movie movie = Movie.builder()
                .id(1L)
                .title("Movie Title")
                .description("Movie Description")
                .mediaType(MediaType.MOVIE)
                .build();

        MoviePublicDto responseDto = new MoviePublicDto(1L, "Movie Title", null, "Movie Description", null, null, null, null, 0, 0);

        movieConverterMockedStatic.when(() -> MovieConverter.fromEntityToPublicDto(movie)).thenReturn(responseDto);

        MoviePublicDto moviePublicDto = (MoviePublicDto) MediaConverter.fromMediaEntityToPublicDto(movie);

        assertEquals(movie.getId(), moviePublicDto.id());
        assertEquals(movie.getTitle(), moviePublicDto.title());
        assertEquals(movie.getDescription(), moviePublicDto.description());
        movieConverterMockedStatic.verify(() -> MovieConverter.fromEntityToPublicDto(movie));
        gameConverterMockedStatic.verifyNoInteractions();
        movieConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test fromMediaEntityToPublicDto should return null when media is null")
    void fromNullMediaEntityToPublicDto() {
        assertNull(MediaConverter.fromMediaEntityToPublicDto(null));

        movieConverterMockedStatic.verifyNoInteractions();
        gameConverterMockedStatic.verifyNoInteractions();
    }
}