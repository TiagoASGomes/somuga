package org.somuga.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.Error;
import org.somuga.dto.crew_role.CrewRoleCreateDto;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.MovieCrew;
import org.somuga.repository.MovieCrewRepository;
import org.somuga.repository.MovieCrewRoleRepository;
import org.somuga.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.somuga.message.Messages.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_PATH = "/api/v1/movie";
    private final List<MovieCrew> crew = new ArrayList<>();
    private final String TITLE = "Title";
    private final Date RELEASE_DATE = new Date();
    private final String DESCRIPTION = "Description";
    private final Integer DURATION = 120;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieCrewRepository movieCrewRepository;
    @Autowired
    private MovieCrewRoleRepository movieCrewRoleRepository;

    @BeforeAll
    public static void setUpMapper() {
        mapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void setUp() {
        MovieCrew director = new MovieCrew("Director", new Date());
        MovieCrew producer = new MovieCrew("Producer", new Date());
        MovieCrew writer = new MovieCrew("Writer", new Date());
        MovieCrew actor = new MovieCrew("Actor", new Date());
        crew.add(director);
        crew.add(producer);
        crew.add(writer);
        crew.add(actor);
        movieCrewRepository.saveAll(crew);
    }

    @AfterEach
    public void cleanUp() {
        movieRepository.deleteAll();
        movieCrewRepository.deleteAll();
    }

    public MoviePublicDto createMovie(String title, Date releaseDate, String description, Integer duration, List<CrewRoleCreateDto> crew) throws Exception {
        MovieCreateDto movieCreateDto = new MovieCreateDto(title, releaseDate, description, duration, crew);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, MoviePublicDto.class);
    }

    public Error createMovieBadRequest(String title, Date releaseDate, String description, Integer duration, List<CrewRoleCreateDto> crew) throws Exception {
        MovieCreateDto movieCreateDto = new MovieCreateDto(title, releaseDate, description, duration, crew);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, Error.class);
    }

    public List<CrewRoleCreateDto> createAllRoles() {
        List<CrewRoleCreateDto> crewRoleCreateDtos = new ArrayList<>();
        for (MovieCrew movieCrew : crew) {
            if (movieCrew.getFullName().equals("Actor")) {
                CrewRoleCreateDto crewRoleCreateDto = new CrewRoleCreateDto(movieCrew.getId(), movieCrew.getFullName().toUpperCase(), "Character1");
                crewRoleCreateDtos.add(crewRoleCreateDto);
                continue;
            }
            CrewRoleCreateDto crewRoleCreateDto = new CrewRoleCreateDto(movieCrew.getId(), movieCrew.getFullName().toUpperCase(), "");
            crewRoleCreateDtos.add(crewRoleCreateDto);
        }
        return crewRoleCreateDtos;
    }

    public List<CrewRoleCreateDto> createOneRole(Long id, String fullName, String character) {
        return List.of(new CrewRoleCreateDto(id, fullName, character));
    }

    @Test
    @DisplayName("Test create a movie and expect status 201")
    void testCreateMovie() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());

        assertEquals(TITLE, moviePublicDto.title());
        assertEquals(RELEASE_DATE, moviePublicDto.releaseDate());
        assertEquals(DESCRIPTION, moviePublicDto.description());
        assertEquals(DURATION, moviePublicDto.duration());
        assertEquals(crew.size(), moviePublicDto.crew().size());
    }

    @Test
    @DisplayName("Test create a movie with empty title and expect status 400")
    void testCreateMovieWithEmptyTitle() throws Exception {
        Error error = createMovieBadRequest("", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());

        assertTrue(error.getMessage().contains(INVALID_TITLE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with title exceeding 255 characters and expect status 400")
    void testCreateMovieWithTitleExceeding255Characters() throws Exception {
        String title = "a".repeat(256);

        Error error = createMovieBadRequest(title, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());

        assertTrue(error.getMessage().contains(MAX_TITLE_CHARACTERS));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with null title and expect status 400")
    void testCreateMovieWithNullTitle() throws Exception {
        Error error = createMovieBadRequest(null, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());

        assertTrue(error.getMessage().contains(INVALID_TITLE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie future date and expect status 400")
    void testCreateMovieWithFutureDate() throws Exception {
        Error error = createMovieBadRequest(TITLE, new Date(System.currentTimeMillis() + 1000000), DESCRIPTION, DURATION, createAllRoles());

        assertTrue(error.getMessage().contains(INVALID_RELEASE_DATE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with null date and expect status 400")
    void testCreateMovieWithNullDate() throws Exception {
        Error error = createMovieBadRequest(TITLE, null, DESCRIPTION, DURATION, createAllRoles());

        assertTrue(error.getMessage().contains(INVALID_RELEASE_DATE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with empty description and expect status 400")
    void testCreateMovieWithEmptyDescription() throws Exception {
        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, "", DURATION, createAllRoles());

        assertTrue(error.getMessage().contains(INVALID_DESCRIPTION));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with description exceeding 1000 characters and expect status 400")
    void testCreateMovieWithDescriptionExceeding1000Characters() throws Exception {
        String description = "a".repeat(1001);

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, description, DURATION, createAllRoles());

        assertTrue(error.getMessage().contains(MAX_DESCRIPTION_CHARACTERS));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with null description and expect status 400")
    void testCreateMovieWithNullDescription() throws Exception {
        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, null, DURATION, createAllRoles());

        assertTrue(error.getMessage().contains(INVALID_DESCRIPTION));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with duration under 0 and expect status 400")
    void testCreateMovieWithDurationUnder0() throws Exception {
        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, 0, createAllRoles());

        assertTrue(error.getMessage().contains(INVALID_DURATION));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with duration over 1440 and expect status 400")
    void testCreateMovieWithDurationAbove1440() throws Exception {
        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, 1441, createAllRoles());

        assertTrue(error.getMessage().contains(INVALID_DURATION));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with null duration and expect status 400")
    void testCreateMovieWithNullDuration() throws Exception {
        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, null, createAllRoles());

        assertTrue(error.getMessage().contains(INVALID_DURATION));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with empty crew and expect status 400")
    void testCreateMovieWithEmptyCrew() throws Exception {
        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, new ArrayList<>());

        assertTrue(error.getMessage().contains(INVALID_CREW_ROLE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with null crew and expect status 400")
    void testCreateMovieWithNullCrew() throws Exception {
        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, null);

        assertTrue(error.getMessage().contains(INVALID_CREW_ROLE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with crew role id under 0 and expect status 400")
    void testCreateMovieWithCrewRoleIdUnder0() throws Exception {
        List<CrewRoleCreateDto> crewRoleCreateDtos = createOneRole(-1L, "ACTOR", "Character1");

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crewRoleCreateDtos);

        assertTrue(error.getMessage().contains(ID_GREATER_THAN_0));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with crew role id null and expect status 400")
    void testCreateMovieWithCrewRoleIdNull() throws Exception {
        List<CrewRoleCreateDto> crewRoleCreateDtos = createOneRole(null, "ACTOR", "Character1");

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crewRoleCreateDtos);

        assertTrue(error.getMessage().contains(ID_GREATER_THAN_0));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with crew role null and expect status 400")
    void testCreateMovieWithCrewRoleNull() throws Exception {
        List<CrewRoleCreateDto> crewRoleCreateDtos = createOneRole(1L, null, "Character1");

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crewRoleCreateDtos);

        assertTrue(error.getMessage().contains(INVALID_MOVIE_ROLE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with crew role empty and expect status 400")
    void testCreateMovieWithCrewRoleEmpty() throws Exception {
        List<CrewRoleCreateDto> crewRoleCreateDtos = createOneRole(1L, "", "Character1");

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crewRoleCreateDtos);

        assertTrue(error.getMessage().contains(INVALID_MOVIE_ROLE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with invalid crew role and expect status 400")
    void testCreateMovieWithInvalidCrewRole() throws Exception {
        List<CrewRoleCreateDto> crewRoleCreateDtos = createOneRole(1L, "INVALID", "Character1");

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crewRoleCreateDtos);

        assertTrue(error.getMessage().contains(INVALID_MOVIE_ROLE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create a movie with character name exceeding 255 characters and expect status 400")
    void testCreateMovieWithCharacterNameExceeding255Characters() throws Exception {
        List<CrewRoleCreateDto> crewRoleCreateDtos = createOneRole(1L, "ACTOR", "a".repeat(256));

        Error error = createMovieBadRequest(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crewRoleCreateDtos);

        assertTrue(error.getMessage().contains(INVALID_CHARACTER_NAME));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test get all movies and expect status 200")
    void testGetAllMovies() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());

        String response = mockMvc.perform(get(API_PATH))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(2, moviePublicDtos.size());
    }

    @Test
    @DisplayName("Test get all movies with pagination and expect status 200")
    void testGetAllMoviesWithPagination() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());

        String response = mockMvc.perform(get(API_PATH + "?page=0&size=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(1, moviePublicDtos.size());
    }

    @Test
    @DisplayName("Test get all movies by title and expect status 200")
    void testGetAllMoviesByTitle() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());
        createMovie("Different", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());

        String response = mockMvc.perform(get(API_PATH + "/search/" + TITLE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(2, moviePublicDtos.size());
    }

    @Test
    @DisplayName("Test get all movies by title with pagination and expect status 200")
    void testGetAllMoviesByTitleWithPagination() throws Exception {
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());
        createMovie("Different", RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());

        String response = mockMvc.perform(get(API_PATH + "/search/" + TITLE + "?page=0&size=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(1, moviePublicDtos.size());
    }

    @Test
    @DisplayName("Test get all movies by crew id and expect status 200")
    void testGetAllMoviesByCrewId() throws Exception {
        List<CrewRoleCreateDto> crew = createAllRoles();
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crew);
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, crew);
        CrewRoleCreateDto lastCrew = crew.remove(1);
        createMovie(TITLE + 3, RELEASE_DATE, DESCRIPTION, DURATION, crew);

        String response = mockMvc.perform(get(API_PATH + "/crew/" + lastCrew.movieCrewId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(2, moviePublicDtos.size());
    }

    @Test
    @DisplayName("Test get all movies by crew id with pagination and expect status 200")
    void testGetAllMoviesByCrewIdWithPagination() throws Exception {
        List<CrewRoleCreateDto> crew = createAllRoles();
        createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crew);
        createMovie(TITLE + 2, RELEASE_DATE, DESCRIPTION, DURATION, crew);
        CrewRoleCreateDto lastCrew = crew.remove(1);
        createMovie(TITLE + 3, RELEASE_DATE, DESCRIPTION, DURATION, crew);

        String response = mockMvc.perform(get(API_PATH + "/crew/" + lastCrew.movieCrewId() + "?page=0&size=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MoviePublicDto> moviePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MoviePublicDto.class));

        assertEquals(1, moviePublicDtos.size());
    }

    @Test
    @DisplayName("Test get movie by id and expect status 200")
    void testGetMovieById() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());

        String response = mockMvc.perform(get(API_PATH + "/" + moviePublicDto.id()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MoviePublicDto movie = mapper.readValue(response, MoviePublicDto.class);

        assertEquals(TITLE, movie.title());
        assertEquals(RELEASE_DATE, movie.releaseDate());
        assertEquals(DESCRIPTION, movie.description());
        assertEquals(DURATION, movie.duration());
        assertEquals(crew.size(), movie.crew().size());
    }

    @Test
    @DisplayName("Test get movie by id not found and expect status 404")
    void testGetMovieByIdNotFound() throws Exception {
        Error error = mapper.readValue(mockMvc.perform(get(API_PATH + "/1"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString(), Error.class);

        assertTrue(error.getMessage().contains(MOVIE_NOT_FOUND));
        assertEquals(404, error.getStatus());
    }

    @Test
    @DisplayName("Test update movie and expect status 200")
    void testUpdateMovie() throws Exception {
        List<CrewRoleCreateDto> crew = createAllRoles();
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, crew);
        String newTitle = "New Title";
        Date newReleaseDate = new Date();
        String newDescription = "New Description";
        Integer newDuration = 150;
        crew.remove(3);
        crew.remove(2);

        MovieCreateDto movieCreateDto = new MovieCreateDto(newTitle, newReleaseDate, newDescription, newDuration, crew);

        String response = mockMvc.perform(put(API_PATH + "/" + moviePublicDto.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCreateDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MoviePublicDto updatedMovie = mapper.readValue(response, MoviePublicDto.class);

        assertEquals(newTitle, updatedMovie.title());
        assertEquals(newReleaseDate, updatedMovie.releaseDate());
        assertEquals(newDescription, updatedMovie.description());
        assertEquals(newDuration, updatedMovie.duration());
        assertEquals(crew.size(), updatedMovie.crew().size());
    }

    @Test
    @DisplayName("Test update movie invalid id and expect status 404")
    void testUpdateMovieInvalidId() throws Exception {
        List<CrewRoleCreateDto> crew = createAllRoles();
        String newTitle = "New Title";
        Date newReleaseDate = new Date();
        String newDescription = "New Description";
        Integer newDuration = 150;
        crew.remove(3);
        crew.remove(2);

        MovieCreateDto movieCreateDto = new MovieCreateDto(newTitle, newReleaseDate, newDescription, newDuration, crew);

        String response = mockMvc.perform(put(API_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(MOVIE_NOT_FOUND + 1, error.getMessage());
        assertEquals(404, error.getStatus());
    }

    @Test
    @DisplayName("Test delete movie and expect status 204")
    void testDeleteMovie() throws Exception {
        MoviePublicDto moviePublicDto = createMovie(TITLE, RELEASE_DATE, DESCRIPTION, DURATION, createAllRoles());

        mockMvc.perform(delete(API_PATH + "/" + moviePublicDto.id()))
                .andExpect(status().isNoContent());

        assertEquals(0, movieRepository.count());
        assertEquals(0, movieCrewRoleRepository.count());
    }


}
