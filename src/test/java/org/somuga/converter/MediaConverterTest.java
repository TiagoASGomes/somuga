package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Game;
import org.somuga.entity.Movie;
import org.somuga.enums.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class MediaConverterTest {
    //TODO mock GameConverter and MovieConverter

    @Test
    @DisplayName("Test fromMediaEntityToPublicDto should convert Game entity to GamePublicDto")
    void fromGameEntityToPublicDto() {
        Game game = Game.builder()
                .id(1L)
                .title("Game Title")
                .description("Game Description")
                .mediaType(MediaType.GAME)
                .build();

        GamePublicDto gamePublicDto = (GamePublicDto) MediaConverter.fromMediaEntityToPublicDto(game);

        assertEquals(game.getId(), gamePublicDto.id());
        assertEquals(game.getTitle(), gamePublicDto.title());
        assertEquals(game.getDescription(), gamePublicDto.description());
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

        MoviePublicDto moviePublicDto = (MoviePublicDto) MediaConverter.fromMediaEntityToPublicDto(movie);

        assertEquals(movie.getId(), moviePublicDto.id());
        assertEquals(movie.getTitle(), moviePublicDto.title());
        assertEquals(movie.getDescription(), moviePublicDto.description());
    }

    @Test
    @DisplayName("Test fromMediaEntityToPublicDto should return null when media is null")
    void fromNullMediaEntityToPublicDto() {
        assertNull(MediaConverter.fromMediaEntityToPublicDto(null));
    }
}