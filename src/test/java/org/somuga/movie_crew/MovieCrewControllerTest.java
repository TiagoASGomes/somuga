package org.somuga.movie_crew;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.Error;
import org.somuga.converter.MovieCrewConverter;
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.entity.MovieCrew;
import org.somuga.repository.MovieCrewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
class MovieCrewControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER_ID = "google-auth2|1234567890";
    private final String PRIVATE_API_PATH = "/api/v1/movie_crew/private";
    private final String PUBLIC_API_PATH = "/api/v1/movie_crew/public";
    private final String NAME = "Test Name";
    private final Date BIRTH_DATE = new Date();
    MockMvc mockMvc;
    @Autowired
    private MovieCrewRepository movieCrewRepository;
    @Autowired
    private WebApplicationContext controller;
    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @BeforeAll
    public static void setUpMapper() {
        mapper.registerModule(new JavaTimeModule());
    }

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

    public MovieCrewPublicDto createMovieCrew(String name, Date birthDate) {
        MovieCrew movieCrew = new MovieCrew(name, birthDate);
        movieCrew.setCrewCreatorId(USER_ID);
        return MovieCrewConverter.fromEntityToPublicDto(movieCrewRepository.save(movieCrew));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create movie crew and expect status 201")
    void testCreateMovieCrew() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, BIRTH_DATE);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto movieCrew = mapper.readValue(response, MovieCrewPublicDto.class);

        assertEquals(NAME, movieCrew.name());
        assertEquals(BIRTH_DATE, movieCrew.birthDate());
        assertNotNull(movieCrew.id());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create movie crew with empty name and expect status 400")
    void testCreateMovieCrewEmptyName() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("", BIRTH_DATE);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_NAME));
        assertEquals(400, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create movie crew with null name and expect status 400")
    void testCreateMovieCrewNullName() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(null, BIRTH_DATE);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_NAME));
        assertEquals(400, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create movie crew with name over 101 characters and expect status 400")
    void testCreateMovieCrewNameOver50Characters() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("T".repeat(101), BIRTH_DATE);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_NAME_SIZE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create movie crew with null birth date and expect status 400")
    void testCreateMovieCrewNullBirthDate() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, null);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_BIRTH_DATE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create movie crew with future birth date and expect status 400")
    void testCreateMovieCrewFutureBirthDate() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, new Date(System.currentTimeMillis() + 10000));

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
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

        String response = mockMvc.perform(get(PUBLIC_API_PATH))
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

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=1"))
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

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/" + NAME))
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

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/" + NAME + "?page=0&size=1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto[] movieCrews = mapper.readValue(response, MovieCrewPublicDto[].class);

        assertEquals(1, movieCrews.length);
    }

    @Test
    @DisplayName("Test get movie crew by id and expect status 200")
    void testGetMovieCrewById() throws Exception {
        MovieCrewPublicDto movieCrew = createMovieCrew(NAME, BIRTH_DATE);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + movieCrew.id()))
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
        mockMvc.perform(get(PUBLIC_API_PATH + "/999999"))
                .andExpect(status().isNotFound());
    }

}
