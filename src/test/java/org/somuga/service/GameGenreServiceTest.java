package org.somuga.service;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.somuga.converter.GameGenreConverter;
import org.somuga.dto.game_genre.GameGenreCreateDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.entity.GameGenre;
import org.somuga.repository.GameGenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.somuga.util.message.Messages.GENRE_ALREADY_EXISTS;
import static org.somuga.util.message.Messages.GENRE_NOT_FOUND;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class GameGenreServiceTest {

    private static MockedStatic<GameGenreConverter> gameGenreConverter;

    private final GameGenre genre = GameGenre.builder()
            .id(1L)
            .genre("Action")
            .build();

    private final GameGenrePublicDto genreDto = new GameGenrePublicDto(1L, "Action");

    @Autowired
    private GameGenreService gameGenreService;
    @MockBean
    private GameGenreRepository gameGenreRepository;

    @BeforeAll
    static void setUp() {
        gameGenreConverter = mockStatic(GameGenreConverter.class);
    }

    @AfterAll
    static void tearDown() {
        gameGenreConverter.close();
    }

    @AfterEach
    void tearDownEach() {
        gameGenreConverter.reset();
    }

    @Test
    @DisplayName("Test get all method without name parameter and return list of genres")
    void getAll() {
        List<GameGenre> genres = List.of(genre);

        Mockito.when(gameGenreRepository.findAll()).thenReturn(genres);
        gameGenreConverter.when(() -> GameGenreConverter.fromEntityListToPublicDtoList(genres)).thenReturn(List.of(genreDto));

        List<GameGenrePublicDto> result = gameGenreService.getAll(null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(genreDto, result.get(0));

        Mockito.verify(gameGenreRepository).findAll();
        gameGenreConverter.verify(() -> GameGenreConverter.fromEntityListToPublicDtoList(genres));
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test get all method with name parameter and return list of genres")
    void getAllWithName() {
        List<GameGenre> genres = List.of(genre);

        Mockito.when(gameGenreRepository.findByGenreContainingIgnoreCase("Action")).thenReturn(genres);
        gameGenreConverter.when(() -> GameGenreConverter.fromEntityListToPublicDtoList(genres)).thenReturn(List.of(genreDto));

        List<GameGenrePublicDto> result = gameGenreService.getAll("Action");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(genreDto, result.get(0));

        Mockito.verify(gameGenreRepository).findByGenreContainingIgnoreCase("Action");
        gameGenreConverter.verify(() -> GameGenreConverter.fromEntityListToPublicDtoList(genres));
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test get by id method and return genre")
    void getById() throws Exception {
        Mockito.when(gameGenreRepository.findById(1L)).thenReturn(Optional.of(genre));
        gameGenreConverter.when(() -> GameGenreConverter.fromEntityToPublicDto(genre)).thenReturn(genreDto);

        GameGenrePublicDto result = gameGenreService.getById(1L);

        Assertions.assertEquals(genreDto, result);

        Mockito.verify(gameGenreRepository).findById(1L);
        gameGenreConverter.verify(() -> GameGenreConverter.fromEntityToPublicDto(genre));
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test get by id with non-existent id and throw exception")
    void getByIdWithNonExistentId() {
        Mockito.when(gameGenreRepository.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(Exception.class, () -> gameGenreService.getById(1L)).getMessage();

        assertEquals(GENRE_NOT_FOUND + 1, errorMessage);

        Mockito.verify(gameGenreRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test create method and return created genre")
    void create() throws Exception {
        GameGenreCreateDto createDto = new GameGenreCreateDto("Action");

        Mockito.when(gameGenreRepository.save(genre)).thenReturn(genre);
        Mockito.when(gameGenreRepository.findByGenreIgnoreCase("Action")).thenReturn(Optional.empty());
        gameGenreConverter.when(() -> GameGenreConverter.fromCreateDtoToEntity(createDto)).thenReturn(genre);
        gameGenreConverter.when(() -> GameGenreConverter.fromEntityToPublicDto(genre)).thenReturn(genreDto);

        GameGenrePublicDto result = gameGenreService.create(createDto);

        assertEquals(genreDto, result);

        Mockito.verify(gameGenreRepository).save(genre);
        Mockito.verify(gameGenreRepository).findByGenreIgnoreCase("Action");
        gameGenreConverter.verify(() -> GameGenreConverter.fromEntityToPublicDto(genre));
        gameGenreConverter.verify(() -> GameGenreConverter.fromCreateDtoToEntity(createDto));
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test create method with existing genre and throw exception")
    void createWithExistingGenre() {
        GameGenreCreateDto createDto = new GameGenreCreateDto("Action");

        Mockito.when(gameGenreRepository.findByGenreIgnoreCase("Action")).thenReturn(Optional.of(genre));

        String errorMessage = assertThrows(Exception.class, () -> gameGenreService.create(createDto)).getMessage();

        assertEquals(GENRE_ALREADY_EXISTS + "Action", errorMessage);

        Mockito.verify(gameGenreRepository).findByGenreIgnoreCase("Action");
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test update method and return updated genre")
    void update() throws Exception {
        GameGenreCreateDto createDto = new GameGenreCreateDto("Adventure");

        Mockito.when(gameGenreRepository.save(genre)).thenReturn(genre);
        Mockito.when(gameGenreRepository.findByGenreIgnoreCase("Adventure")).thenReturn(Optional.empty());
        Mockito.when(gameGenreRepository.findById(1L)).thenReturn(Optional.of(genre));
        gameGenreConverter.when(() -> GameGenreConverter.fromEntityToPublicDto(genre)).thenReturn(genreDto);

        GameGenrePublicDto result = gameGenreService.update(1L, createDto);

        assertEquals(genreDto, result);

        Mockito.verify(gameGenreRepository).save(genre);
        Mockito.verify(gameGenreRepository).findByGenreIgnoreCase("Adventure");
        Mockito.verify(gameGenreRepository).findById(1L);
        gameGenreConverter.verify(() -> GameGenreConverter.fromEntityToPublicDto(genre));
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test update method with existing genre and throw exception")
    void updateWithExistingGenre() {
        GameGenreCreateDto createDto = new GameGenreCreateDto("Adventure");

        GameGenre genre2 = GameGenre.builder()
                .id(2L)
                .genre("Adventure")
                .build();
        Mockito.when(gameGenreRepository.findByGenreIgnoreCase("Adventure")).thenReturn(Optional.of(genre2));

        String errorMessage = assertThrows(Exception.class, () -> gameGenreService.update(1L, createDto)).getMessage();

        assertEquals(GENRE_ALREADY_EXISTS + "Adventure", errorMessage);

        Mockito.verify(gameGenreRepository).findByGenreIgnoreCase("Adventure");
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test delete method and delete genre")
    void delete() throws Exception {
        Mockito.when(gameGenreRepository.findById(1L)).thenReturn(Optional.of(genre));

        gameGenreService.delete(1L);

        Mockito.verify(gameGenreRepository).findById(1L);
        Mockito.verify(gameGenreRepository).deleteById(1L);
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test delete method with non-existent id and throw exception")
    void deleteWithNonExistentId() {
        Mockito.when(gameGenreRepository.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(Exception.class, () -> gameGenreService.delete(1L)).getMessage();

        assertEquals(GENRE_NOT_FOUND + 1, errorMessage);

        Mockito.verify(gameGenreRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test find by id method and return genre")
    void findById() throws Exception {
        Mockito.when(gameGenreRepository.findById(1L)).thenReturn(Optional.of(genre));

        GameGenre result = gameGenreService.findById(1L);

        assertEquals(genre, result);

        Mockito.verify(gameGenreRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test find by id with non-existent id and throw exception")
    void findByIdWithNonExistentId() {
        Mockito.when(gameGenreRepository.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(Exception.class, () -> gameGenreService.findById(1L)).getMessage();

        assertEquals(GENRE_NOT_FOUND + 1, errorMessage);

        Mockito.verify(gameGenreRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(gameGenreRepository);
        gameGenreConverter.verifyNoInteractions();
    }
}