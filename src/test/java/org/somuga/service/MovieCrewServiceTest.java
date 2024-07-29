package org.somuga.service;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.somuga.converter.MovieCrewConverter;
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.entity.MovieCrew;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.movie_crew.MovieCrewNotFoundException;
import org.somuga.repository.MovieCrewRepository;
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
class MovieCrewServiceTest {

    private static MockedStatic<MovieCrewConverter> movieCrewConverter;
    private final MovieCrew movieCrew = MovieCrew.builder()
            .id(1L)
            .fullName("John Doe")
            .crewCreatorId("admin")
            .birthDate(new Date())
            .crewCreatorId("admin")
            .build();
    private final MovieCrewPublicDto movieCrewPublicDto = new MovieCrewPublicDto(
            1L,
            "John Doe",
            new Date(),
            new ArrayList<>()
    );
    @MockBean
    private MovieCrewRepository movieCrewRepository;
    @Autowired
    private MovieCrewService movieCrewService;

    @BeforeAll
    static void setUp() {
        movieCrewConverter = mockStatic(MovieCrewConverter.class);
    }

    @AfterAll
    static void tearDown() {
        movieCrewConverter.close();
    }

    @AfterEach
    void reset() {
        movieCrewConverter.reset();
    }

    @Test
    @DisplayName("Get all movie crew members with no search query")
    void getAll() {
        List<MovieCrew> movieCrewList = List.of(movieCrew);
        Page<MovieCrew> page = new PageImpl<>(movieCrewList);
        List<MovieCrewPublicDto> movieCrewPublicDtoList = List.of(movieCrewPublicDto);
        Pageable pageable = PageRequest.of(0, 10);

        movieCrewConverter.when(() -> MovieCrewConverter.fromEntityListToPublicDtoList(movieCrewList))
                .thenReturn(movieCrewPublicDtoList);
        Mockito.when(movieCrewRepository.findAll(pageable)).thenReturn(page);

        List<MovieCrewPublicDto> result = movieCrewService.getAll(pageable, null);

        assertEquals(movieCrewPublicDtoList, result);

        movieCrewConverter.verify(() -> MovieCrewConverter.fromEntityListToPublicDtoList(movieCrewList));
        Mockito.verify(movieCrewRepository).findAll(pageable);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
        movieCrewConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Get all movie crew members with search query")
    void getAllWithName() {
        List<MovieCrew> movieCrewList = List.of(movieCrew);
        Page<MovieCrew> page = new PageImpl<>(movieCrewList);
        List<MovieCrewPublicDto> movieCrewPublicDtoList = List.of(movieCrewPublicDto);
        Pageable pageable = PageRequest.of(0, 10);

        movieCrewConverter.when(() -> MovieCrewConverter.fromEntityListToPublicDtoList(movieCrewList))
                .thenReturn(movieCrewPublicDtoList);
        Mockito.when(movieCrewRepository.findByFullNameContainingIgnoreCase("John", pageable)).thenReturn(page);

        List<MovieCrewPublicDto> result = movieCrewService.getAll(pageable, "John");

        assertEquals(movieCrewPublicDtoList, result);

        movieCrewConverter.verify(() -> MovieCrewConverter.fromEntityListToPublicDtoList(movieCrewList));
        Mockito.verify(movieCrewRepository).findByFullNameContainingIgnoreCase("John", pageable);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
        movieCrewConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Get movie crew member by id")
    void getById() throws Exception {
        movieCrewConverter.when(() -> MovieCrewConverter.fromEntityToPublicDto(movieCrew))
                .thenReturn(movieCrewPublicDto);
        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.of(movieCrew));

        MovieCrewPublicDto result = movieCrewService.getById(1L);

        assertEquals(movieCrewPublicDto, result);

        movieCrewConverter.verify(() -> MovieCrewConverter.fromEntityToPublicDto(movieCrew));
        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
        movieCrewConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Get movie crew member by id throws exception")
    void getByIdThrowsException() {
        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(MovieCrewNotFoundException.class, () -> movieCrewService.getById(1L)).getMessage();

        assertEquals(MOVIE_CREW_NOT_FOUND + 1L, errorMessage);
        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
        movieCrewConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Create movie crew member")
    @WithMockUser(username = "admin")
    void create() {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("John Doe", new Date());

        movieCrewConverter.when(() -> MovieCrewConverter.fromCreateDtoToEntity(movieCrewCreateDto))
                .thenReturn(movieCrew);
        movieCrewConverter.when(() -> MovieCrewConverter.fromEntityToPublicDto(movieCrew)).thenReturn(movieCrewPublicDto);
        Mockito.when(movieCrewRepository.save(movieCrew)).thenReturn(movieCrew);

        MovieCrewPublicDto result = movieCrewService.create(movieCrewCreateDto);

        assertEquals(movieCrewPublicDto, result);

        movieCrewConverter.verify(() -> MovieCrewConverter.fromCreateDtoToEntity(movieCrewCreateDto));
        movieCrewConverter.verify(() -> MovieCrewConverter.fromEntityToPublicDto(movieCrew));
        Mockito.verify(movieCrewRepository).save(movieCrew);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
        movieCrewConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Update movie crew member")
    @WithMockUser(username = "admin")
    void update() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("John Doe2", new Date());

        movieCrewConverter.when(() -> MovieCrewConverter.fromEntityToPublicDto(movieCrew)).thenReturn(movieCrewPublicDto);
        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.of(movieCrew));
        Mockito.when(movieCrewRepository.save(movieCrew)).thenReturn(movieCrew);

        MovieCrewPublicDto result = movieCrewService.update(1L, movieCrewCreateDto);

        assertEquals(movieCrewPublicDto, result);

        movieCrewConverter.verify(() -> MovieCrewConverter.fromEntityToPublicDto(movieCrew));
        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verify(movieCrewRepository).save(movieCrew);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
        movieCrewConverter.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Update movie crew member throws exception")
    @WithMockUser(username = "user")
    void updateThrowsException() {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("John Doe", new Date());

        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.of(movieCrew));

        String errorMessage = assertThrows(InvalidPermissionException.class, () -> movieCrewService.update(1L, movieCrewCreateDto)).getMessage();

        assertEquals(UNAUTHORIZED_UPDATE, errorMessage);
        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
        movieCrewConverter.verifyNoInteractions();
    }

    @Test
    @DisplayName("Delete movie crew member")
    @WithMockUser(username = "admin")
    void delete() throws Exception {

        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.of(movieCrew));

        movieCrewService.delete(1L);

        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verify(movieCrewRepository).deleteById(1L);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
    }

    @Test
    @DisplayName("Delete movie crew member throws crew not found exception")
    @WithMockUser(username = "admin")
    void deleteThrowsException() {
        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(MovieCrewNotFoundException.class, () -> movieCrewService.delete(1L)).getMessage();

        assertEquals(MOVIE_CREW_NOT_FOUND + 1L, errorMessage);
        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
    }

    @Test
    @DisplayName("Delete movie crew member throws permission exception")
    @WithMockUser(username = "user")
    void deleteThrowsPermissionException() {
        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.of(movieCrew));

        String errorMessage = assertThrows(InvalidPermissionException.class, () -> movieCrewService.delete(1L)).getMessage();

        assertEquals(UNAUTHORIZED_DELETE, errorMessage);
        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
    }

    @Test
    @DisplayName("Admin delete movie crew member")
    void adminDelete() throws Exception {
        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.of(movieCrew));

        movieCrewService.adminDelete(1L);

        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verify(movieCrewRepository).deleteById(1L);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
    }

    @Test
    @DisplayName("Admin delete movie crew member throws exception")
    void adminDeleteThrowsException() {
        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(MovieCrewNotFoundException.class, () -> movieCrewService.adminDelete(1L)).getMessage();

        assertEquals(MOVIE_CREW_NOT_FOUND + 1L, errorMessage);
        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
    }

    @Test
    @DisplayName("Find movie crew member by id")
    void findById() throws Exception {
        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.of(movieCrew));

        MovieCrew result = movieCrewService.findById(1L);

        assertEquals(movieCrew, result);

        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
    }

    @Test
    @DisplayName("Find movie crew member by id throws exception")
    void findByIdThrowsException() {
        Mockito.when(movieCrewRepository.findById(1L)).thenReturn(Optional.empty());

        String errorMessage = assertThrows(MovieCrewNotFoundException.class, () -> movieCrewService.findById(1L)).getMessage();

        assertEquals(MOVIE_CREW_NOT_FOUND + 1L, errorMessage);
        Mockito.verify(movieCrewRepository).findById(1L);
        Mockito.verifyNoMoreInteractions(movieCrewRepository);
    }
}