package org.somuga.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.somuga.entity.Game;
import org.somuga.entity.Media;
import org.somuga.entity.Movie;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.exception.movie.MovieNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.somuga.util.message.Messages.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class MediaServiceTest {

    @MockBean
    private GameService gameService;
    @MockBean
    private MovieService movieService;
    @Autowired
    private MediaService mediaService;

    @Test
    @DisplayName("Test findById with game existing")
    void findByIdGame() throws Exception {
        Long id = 1L;
        Game game = Game.builder().id(id).build();

        when(gameService.findById(id)).thenReturn(game);
        when(movieService.findById(id)).thenThrow(new MovieNotFoundException(MOVIE_NOT_FOUND + id));

        Media result = mediaService.findById(id);

        assertEquals(game, result);

        Mockito.verify(gameService).findById(id);
        Mockito.verify(movieService).findById(id);
        Mockito.verifyNoMoreInteractions(gameService, movieService);
    }

    @Test
    @DisplayName("Test findById with movie existing")
    void findByIdMovie() throws Exception {
        Long id = 1L;
        Movie movie = Movie.builder().id(id).build();

        when(movieService.findById(id)).thenReturn(movie);

        Media result = mediaService.findById(id);

        assertEquals(movie, result);

        Mockito.verify(movieService).findById(id);
        Mockito.verifyNoMoreInteractions(movieService);
        Mockito.verifyNoInteractions(gameService);
    }

    @Test
    @DisplayName("Test findById with media not found")
    void findByIdNotFound() throws Exception {
        Long id = 1L;

        when(movieService.findById(id)).thenThrow(new MovieNotFoundException(MOVIE_NOT_FOUND + id));
        when(gameService.findById(id)).thenThrow(new GameNotFoundException(GAME_NOT_FOUND + id));

        String errorMessage = assertThrows(MediaNotFoundException.class, () -> mediaService.findById(id)).getMessage();

        assertEquals(MEDIA_NOT_FOUND + id, errorMessage);

        Mockito.verify(movieService).findById(id);
        Mockito.verify(gameService).findById(id);
        Mockito.verifyNoMoreInteractions(movieService, gameService);
    }
}