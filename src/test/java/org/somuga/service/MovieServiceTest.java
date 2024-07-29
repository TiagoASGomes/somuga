package org.somuga.service;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.somuga.converter.MovieConverter;
import org.somuga.dto.crew_role.MovieRolePublicDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.entity.MovieCrew;
import org.somuga.enums.MediaType;
import org.somuga.enums.MovieRole;
import org.somuga.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class MovieServiceTest {

    private static MockedStatic<MovieConverter> movieConverterMockedStatic;
    private final Date date = new Date();
    private final Movie movie = getMovie();
    private final MoviePublicDto moviePublicDto = new MoviePublicDto(
            1L,
            "Movie Title",
            date,
            "Movie Description",
            120,
            List.of(new MovieRolePublicDto(1L, "John Doe", date, MovieRole.ACTOR.name(), "Character Name")),
            "http://media.com",
            "http://image.com",
            0,
            0
    );

    @MockBean
    private MovieCrewService crewService;
    @MockBean
    private MovieRepository movieRepo;
    @Autowired
    private MovieService movieService;

    @BeforeAll
    static void setUp() {
        movieConverterMockedStatic = mockStatic(MovieConverter.class);
    }

    @AfterAll
    static void tearDown() {
        movieConverterMockedStatic.close();
    }

    @AfterEach
    void reset() {
        movieConverterMockedStatic.reset();
    }

    private Movie getMovie() {
        Movie movie = Movie.builder()
                .id(1L)
                .title("Movie Title")
                .releaseDate(date)
                .description("Movie Description")
                .duration(120)
                .mediaUrl("http://media.com")
                .imageUrl("http://image.com")
                .mediaType(MediaType.MOVIE)
                .build();

        MovieCrew movieCrew = MovieCrew.builder()
                .id(1L)
                .fullName("John Doe")
                .birthDate(date)
                .build();

        movie.addMovieCrew(movieCrew, MovieRole.ACTOR, "Character Name");

        return movie;
    }

    @Test
    @DisplayName("Test get all movies with no search criteria")
    void getAll() {
        List<Movie> movies = List.of(movie);
        List<MoviePublicDto> moviePublicDtos = List.of(moviePublicDto);
        Page<Movie> moviePage = new PageImpl<>(movies);
        Pageable pageable = PageRequest.of(0, 10);

        movieConverterMockedStatic.when(() -> MovieConverter.fromEntityListToPublicDtoList(movies)).thenReturn(moviePublicDtos);
        Mockito.when(movieRepo.findAll(Mockito.<Specification<Movie>>any(), Mockito.any(Pageable.class))).thenReturn(moviePage);

        MoviePublicDto result = movieService.getAll(pageable, null, null).get(0);

        assertEquals(moviePublicDto, result);

        movieConverterMockedStatic.verify(() -> MovieConverter.fromEntityListToPublicDtoList(movies));
        Mockito.verify(movieRepo).findAll(Mockito.<Specification<Movie>>any(), Mockito.any(Pageable.class));
        movieConverterMockedStatic.verifyNoMoreInteractions();
        Mockito.verifyNoMoreInteractions(movieRepo);
    }

    @Test
    void getById() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void findById() {
    }

    @Test
    void adminDelete() {
    }
}