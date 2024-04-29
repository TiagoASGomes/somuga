package org.somuga.movie_crew;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.aspect.Error;
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.repository.MovieCrewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.message.Messages.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovieCrewControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_PATH = "/api/v1/movie_crew";
    private final String NAME = "Test Name";
    private final Date BIRTH_DATE = new Date();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MovieCrewRepository movieCrewRepository;

    @BeforeAll
    public static void setUpMapper() {
        mapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    public void cleanUp() {
        movieCrewRepository.deleteAll();
    }

    public MovieCrewPublicDto createMovieCrew(String name, Date birthDate) throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(name, birthDate);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, MovieCrewPublicDto.class);
    }

    @Test
    @DisplayName("Test create movie crew and expect status 201")
    void testCreateMovieCrew() throws Exception {
        MovieCrewPublicDto movieCrew = createMovieCrew(NAME, BIRTH_DATE);

        assertEquals(NAME, movieCrew.name());
        assertEquals(BIRTH_DATE, movieCrew.birthDate());
        assertNotNull(movieCrew.id());
    }

    @Test
    @DisplayName("Test create movie crew with empty name and expect status 400")
    void testCreateMovieCrewEmptyName() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("", BIRTH_DATE);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_NAME));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create movie crew with null name and expect status 400")
    void testCreateMovieCrewNullName() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(null, BIRTH_DATE);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_NAME));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create movie crew with name over 50 characters and expect status 400")
    void testCreateMovieCrewNameOver50Characters() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("T".repeat(51), BIRTH_DATE);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_NAME_SIZE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create movie crew with null birth date and expect status 400")
    void testCreateMovieCrewNullBirthDate() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, null);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_BIRTH_DATE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test create movie crew with future birth date and expect status 400")
    void testCreateMovieCrewFutureBirthDate() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, new Date(System.currentTimeMillis() + 10000));

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_BIRTH_DATE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Test get all movie crew and expect status 200")
    void testGetAllMovieCrew() throws Exception {
        createMovieCrew(NAME, BIRTH_DATE);
        createMovieCrew(NAME + "a", BIRTH_DATE);

        String response = mockMvc.perform(get(API_PATH))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto[] movieCrews = mapper.readValue(response, MovieCrewPublicDto[].class);

        assertEquals(2, movieCrews.length);
    }

    @Test
    @DisplayName("Test get all movie crew with pagination and expect status 200")
    void testGetAllMovieCrewWithPagination() throws Exception {
        createMovieCrew(NAME, BIRTH_DATE);
        createMovieCrew(NAME + "a", BIRTH_DATE);

        String response = mockMvc.perform(get(API_PATH + "?page=0&size=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto[] movieCrews = mapper.readValue(response, MovieCrewPublicDto[].class);

        assertEquals(1, movieCrews.length);
    }

    @Test
    @DisplayName("Test search movie crew by name and expect status 200")
    void testSearchMovieCrewByName() throws Exception {
        createMovieCrew(NAME, BIRTH_DATE);
        createMovieCrew(NAME + "a", BIRTH_DATE);
        createMovieCrew("Different", BIRTH_DATE);

        String response = mockMvc.perform(get(API_PATH + "/search/" + NAME))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto[] movieCrews = mapper.readValue(response, MovieCrewPublicDto[].class);

        assertEquals(2, movieCrews.length);
    }

    @Test
    @DisplayName("Test search movie crew by name with pagination and expect status 200")
    void testSearchMovieCrewByNameWithPagination() throws Exception {
        createMovieCrew(NAME, BIRTH_DATE);
        createMovieCrew(NAME + "a", BIRTH_DATE);
        createMovieCrew("Different", BIRTH_DATE);

        String response = mockMvc.perform(get(API_PATH + "/search/" + NAME + "?page=0&size=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto[] movieCrews = mapper.readValue(response, MovieCrewPublicDto[].class);

        assertEquals(1, movieCrews.length);
    }

    @Test
    @DisplayName("Test get movie crew by id and expect status 200")
    void testGetMovieCrewById() throws Exception {
        MovieCrewPublicDto movieCrew = createMovieCrew(NAME, BIRTH_DATE);

        String response = mockMvc.perform(get(API_PATH + "/" + movieCrew.id()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto movieCrewResponse = mapper.readValue(response, MovieCrewPublicDto.class);

        assertEquals(movieCrew.id(), movieCrewResponse.id());
        assertEquals(movieCrew.name(), movieCrewResponse.name());
        assertEquals(movieCrew.birthDate(), movieCrewResponse.birthDate());
    }

    @Test
    @DisplayName("Test get movie crew by id and expect status 404")
    void testGetMovieCrewByIdNotFound() throws Exception {
        mockMvc.perform(get(API_PATH + "/999999"))
                .andExpect(status().isNotFound());
    }

}
