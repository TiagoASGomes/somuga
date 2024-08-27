package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.aspect.ErrorDto;
import org.somuga.converter.MovieCrewConverter;
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewListDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.entity.MovieCrew;
import org.somuga.repository.MovieCrewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.testUtils.Utils.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
class MovieCrewE2ETest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER_ID = "google-auth2|1234567890";
    private final String PUBLIC_API_PATH = "/api/v1/movie/crew/public";
    private final String ADMIN_API_PATH = "/api/v1/movie/crew/admin";
    private final String NAME = "Test Name";
    MockMvc mockMvc;
    @Autowired
    private MovieCrewRepository movieCrewRepository;
    @Autowired
    private WebApplicationContext controller;
    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @AfterEach
    public void cleanUp() {
        movieCrewRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
    }

    public MovieCrewPublicDto createMovieCrew(String name) {
        MovieCrew movieCrew = MovieCrew.builder()
                .fullName(name)
                .build();
        return MovieCrewConverter.fromEntityToPublicDto(movieCrewRepository.save(movieCrew));
    }


    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create movie crew and expect status 201")
    void testCreateMovieCrew() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME);

        String response = postRequest(ADMIN_API_PATH, status().isCreated(), mapper.writeValueAsString(movieCrewCreateDto), mockMvc);

        MovieCrewPublicDto movieCrewResponse = mapper.readValue(response, MovieCrewPublicDto.class);

        assertEquals(1, movieCrewRepository.count());
        assertNotNull(movieCrewResponse.id());
        MovieCrew movieCrew1 = movieCrewRepository.findById(movieCrewResponse.id()).orElse(null);
        assertNotNull(movieCrew1);
        assertEquals(movieCrewCreateDto.fullName(), movieCrew1.getFullName());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create movie crew without authorization and expect status 403")
    void testCreateMovieCrewWithoutAuthorization() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME);

        postRequest(ADMIN_API_PATH, status().isForbidden(), mapper.writeValueAsString(movieCrewCreateDto), mockMvc);

        assertEquals(0, movieCrewRepository.count());
    }

    @Test
    @DisplayName("Test create movie crew without authentication and expect status 401")
    void testCreateMovieCrewWithoutAuthentication() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME);

        postRequest(ADMIN_API_PATH, status().isUnauthorized(), mapper.writeValueAsString(movieCrewCreateDto), mockMvc);

        assertEquals(0, movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create movie crew with null data and expect status 400")
    void testCreateMovieCrewWithInvalidData() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(null);

        String response = postRequest(ADMIN_API_PATH, status().isBadRequest(), mapper.writeValueAsString(movieCrewCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertTrue(errorDto.message().contains(INVALID_NAME));

        assertEquals(0, movieCrewRepository.count());
    }


    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create movie crew with name exceeding 100 characters and expect status 400")
    void testCreateMovieCrewWithInvalidNameSize() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("a".repeat(101));

        String response = postRequest(ADMIN_API_PATH, status().isBadRequest(), mapper.writeValueAsString(movieCrewCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertTrue(errorDto.message().contains(INVALID_NAME_SIZE));

        assertEquals(0, movieCrewRepository.count());
    }

    @Test
    @DisplayName("Test get all movie crew and expect status 200")
    void testGetAllMovieCrew() throws Exception {
        createMovieCrew(NAME);
        createMovieCrew("Different");

        String response = getRequest(PUBLIC_API_PATH, status().isOk(), mockMvc);

        MovieCrewListDto movieCrewResponse = mapper.readValue(response, MovieCrewListDto.class);

        assertEquals(2, movieCrewResponse.movieCrews().size());
        assertEquals(2, movieCrewResponse.count());
    }

    @Test
    @DisplayName("Test get all movie movieCrewCreateDto page and expect status 200")
    void testGetAllMovieCrewPage() throws Exception {
        createMovieCrew(NAME);
        createMovieCrew(NAME);
        createMovieCrew("Different");

        String response = getRequest(PUBLIC_API_PATH + "?page=0&size=2", status().isOk(), mockMvc);

        MovieCrewListDto movieCrewResponse = mapper.readValue(response, MovieCrewListDto.class);

        assertEquals(3, movieCrewResponse.count());
        assertEquals(2, movieCrewResponse.movieCrews().size());
    }

    @Test
    @DisplayName("Test get all movie crew with name and expect status 200")
    void testGetAllMovieCrewWithName() throws Exception {
        createMovieCrew(NAME);
        createMovieCrew(NAME);
        createMovieCrew("Different");

        String response = getRequest(PUBLIC_API_PATH + "?name=" + NAME, status().isOk(), mockMvc);

        MovieCrewListDto movieCrewResponse = mapper.readValue(response, MovieCrewListDto.class);

        assertEquals(2, movieCrewResponse.movieCrews().size());
        assertEquals(2, movieCrewResponse.count());
    }

    @Test
    @DisplayName("Test get all movie crew with name paged and expect status 200")
    void testGetAllMovieCrewWithNamePaged() throws Exception {
        createMovieCrew(NAME);
        createMovieCrew(NAME);

        String response = getRequest(PUBLIC_API_PATH + "?name=" + NAME + "&page=0&size=1", status().isOk(), mockMvc);

        MovieCrewListDto movieCrewResponse = mapper.readValue(response, MovieCrewListDto.class);

        assertEquals(1, movieCrewResponse.movieCrews().size());
        assertEquals(2, movieCrewResponse.count());
    }

    @Test
    @DisplayName("Test get movie crew by id and expect status 200")
    void testGetMovieCrewById() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME);

        String response = getRequest(PUBLIC_API_PATH + "/" + movieCrewPublicDto.id(), status().isOk(), mockMvc);

        MovieCrewPublicDto movieCrewResponse = mapper.readValue(response, MovieCrewPublicDto.class);

        assertEquals(movieCrewPublicDto, movieCrewResponse);
    }

    @Test
    @DisplayName("Test get movie crew by id and expect status 404")
    void testGetMovieCrewByIdNotFound() throws Exception {
        String response = getRequest(PUBLIC_API_PATH + "/1", status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(MOVIE_CREW_NOT_FOUND + 1, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test update movie crew and expect status 200")
    void testUpdateMovieCrew() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME);
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("Updated Name");

        String response = putRequest(ADMIN_API_PATH + "/" + movieCrewPublicDto.id(), status().isOk(), mapper.writeValueAsString(movieCrewCreateDto), mockMvc);

        MovieCrewPublicDto movieCrewResponse = mapper.readValue(response, MovieCrewPublicDto.class);
        MovieCrew movieCrew = movieCrewRepository.findById(movieCrewPublicDto.id()).orElse(null);
        assertNotNull(movieCrew);

        assertEquals(1, movieCrewRepository.count());
        assertEquals(movieCrewPublicDto.id(), movieCrewResponse.id());
        assertEquals(movieCrewCreateDto.fullName(), movieCrewResponse.name(), movieCrew.getFullName());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update movie crew without authorization and expect status 403")
    void testUpdateMovieCrewWithoutAuthorization() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME);
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("Updated Name");

        putRequest(ADMIN_API_PATH + "/" + movieCrewPublicDto.id(), status().isForbidden(), mapper.writeValueAsString(movieCrewCreateDto), mockMvc);

        MovieCrew movieCrew = movieCrewRepository.findById(movieCrewPublicDto.id()).orElse(null);
        assertNotNull(movieCrew);

        assertEquals(NAME, movieCrew.getFullName());
    }

    @Test
    @DisplayName("Test update movie crew without authentication and expect status 401")
    void testUpdateMovieCrewWithoutAuthentication() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME);
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("Updated Name");

        putRequest(ADMIN_API_PATH + "/" + movieCrewPublicDto.id(), status().isUnauthorized(), mapper.writeValueAsString(movieCrewCreateDto), mockMvc);

        MovieCrew movieCrew = movieCrewRepository.findById(movieCrewPublicDto.id()).orElse(null);
        assertNotNull(movieCrew);

        assertEquals(NAME, movieCrew.getFullName());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test update movie crew with null data and expect status 400")
    void testUpdateMovieCrewWithInvalidData() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME);
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(null);

        String response = putRequest(ADMIN_API_PATH + "/" + movieCrewPublicDto.id(), status().isBadRequest(), mapper.writeValueAsString(movieCrewCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertTrue(errorDto.message().contains(INVALID_NAME));

        MovieCrew movieCrew = movieCrewRepository.findById(movieCrewPublicDto.id()).orElse(null);
        assertNotNull(movieCrew);

        assertEquals(NAME, movieCrew.getFullName());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test update movie crew not found and expect status 404")
    void testUpdateMovieCrewNotFound() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME);

        String response = putRequest(ADMIN_API_PATH + "/1", status().isNotFound(), mapper.writeValueAsString(movieCrewCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(MOVIE_CREW_NOT_FOUND + 1, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test delete movie crew and expect status 204")
    void testDeleteMovieCrew() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME);

        deleteRequest(ADMIN_API_PATH + "/" + movieCrewPublicDto.id(), status().isNoContent(), mockMvc);

        assertEquals(0, movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete movie crew without authorization and expect status 403")
    void testDeleteMovieCrewWithoutAuthorization() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME);

        deleteRequest(ADMIN_API_PATH + "/" + movieCrewPublicDto.id(), status().isForbidden(), mockMvc);

        assertEquals(1, movieCrewRepository.count());
    }

    @Test
    @DisplayName("Test delete movie crew without authentication and expect status 401")
    void testDeleteMovieCrewWithoutAuthentication() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME);

        deleteRequest(ADMIN_API_PATH + "/" + movieCrewPublicDto.id(), status().isUnauthorized(), mockMvc);

        assertEquals(1, movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test delete movie crew not found and expect status 404")
    void testDeleteMovieCrewNotFound() throws Exception {
        deleteRequest(ADMIN_API_PATH + "/1", status().isNotFound(), mockMvc);

    }


}
