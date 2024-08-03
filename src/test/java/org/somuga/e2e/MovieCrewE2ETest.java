package org.somuga.e2e;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        MovieCrew movieCrew = MovieCrew.builder()
                .fullName(name)
                .birthDate(birthDate)
                .build();
        return MovieCrewConverter.fromEntityToPublicDto(movieCrewRepository.save(movieCrew));
    }


    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create movie crew and expect status 201")
    void testCreateMovieCrew() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, BIRTH_DATE);

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto movieCrewResponse = mapper.readValue(response, MovieCrewPublicDto.class);

        assertEquals(1, movieCrewRepository.count());
        assertNotNull(movieCrewResponse.id());
        MovieCrew movieCrew1 = movieCrewRepository.findById(movieCrewResponse.id()).orElse(null);
        assertNotNull(movieCrew1);
        assertEquals(movieCrewCreateDto.fullName(), movieCrew1.getFullName());
        assertEquals(movieCrewCreateDto.birthDate(), movieCrew1.getBirthDate());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create movie crew without authorization and expect status 403")
    void testCreateMovieCrewWithoutAuthorization() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, BIRTH_DATE);

        mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isForbidden());

        assertEquals(0, movieCrewRepository.count());
    }

    @Test
    @DisplayName("Test create movie crew without authentication and expect status 401")
    void testCreateMovieCrewWithoutAuthentication() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, BIRTH_DATE);

        mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isUnauthorized());

        assertEquals(0, movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create movie crew with null data and expect status 400")
    void testCreateMovieCrewWithInvalidData() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(null, null);

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.message().contains(INVALID_NAME));
        assertTrue(error.message().contains(INVALID_BIRTH_DATE));

        assertEquals(0, movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create movie crew with invalid birth date in the future and expect status 400")
    void testCreateMovieCrewWithInvalidBirthDate() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, new Date(System.currentTimeMillis() + 1000000));

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.message().contains(INVALID_BIRTH_DATE));

        assertEquals(0, movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test create movie crew with name exceeding 100 characters and expect status 400")
    void testCreateMovieCrewWithInvalidNameSize() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("a".repeat(101), BIRTH_DATE);

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.message().contains(INVALID_NAME_SIZE));

        assertEquals(0, movieCrewRepository.count());
    }

    @Test
    @DisplayName("Test get all movie crew and expect status 200")
    void testGetAllMovieCrew() throws Exception {
        createMovieCrew(NAME, BIRTH_DATE);
        createMovieCrew("Different", BIRTH_DATE);

        String response = mockMvc.perform(get(PUBLIC_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MovieCrewPublicDto> movieCrewResponse = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MovieCrewPublicDto.class));

        assertEquals(2, movieCrewResponse.size());
    }

    @Test
    @DisplayName("Test get all movie movieCrewCreateDto page and expect status 200")
    void testGetAllMovieCrewPage() throws Exception {
        createMovieCrew(NAME, BIRTH_DATE);
        createMovieCrew(NAME, BIRTH_DATE);
        createMovieCrew("Different", BIRTH_DATE);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MovieCrewPublicDto> movieCrewResponse = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MovieCrewPublicDto.class));

        assertEquals(2, movieCrewResponse.size());
    }

    @Test
    @DisplayName("Test get all movie crew with name and expect status 200")
    void testGetAllMovieCrewWithName() throws Exception {
        createMovieCrew(NAME, BIRTH_DATE);
        createMovieCrew(NAME, BIRTH_DATE);
        createMovieCrew("Different", BIRTH_DATE);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?name=" + NAME)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MovieCrewPublicDto> movieCrewResponse = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MovieCrewPublicDto.class));

        assertEquals(2, movieCrewResponse.size());
    }

    @Test
    @DisplayName("Test get all movie crew with name paged and expect status 200")
    void testGetAllMovieCrewWithNamePaged() throws Exception {
        createMovieCrew(NAME, BIRTH_DATE);
        createMovieCrew(NAME, BIRTH_DATE);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?name=" + NAME + "&page=0&size=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<MovieCrewPublicDto> movieCrewResponse = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, MovieCrewPublicDto.class));

        assertEquals(1, movieCrewResponse.size());
    }

    @Test
    @DisplayName("Test get movie crew by id and expect status 200")
    void testGetMovieCrewById() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME, BIRTH_DATE);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + movieCrewPublicDto.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto movieCrewResponse = mapper.readValue(response, MovieCrewPublicDto.class);

        assertEquals(movieCrewPublicDto, movieCrewResponse);
    }

    @Test
    @DisplayName("Test get movie crew by id and expect status 404")
    void testGetMovieCrewByIdNotFound() throws Exception {
        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(MOVIE_CREW_NOT_FOUND + 1, error.message());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test update movie crew and expect status 200")
    void testUpdateMovieCrew() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME, BIRTH_DATE);
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("Updated Name", new Date());

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/" + movieCrewPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MovieCrewPublicDto movieCrewResponse = mapper.readValue(response, MovieCrewPublicDto.class);
        MovieCrew movieCrew = movieCrewRepository.findById(movieCrewPublicDto.id()).orElse(null);
        assertNotNull(movieCrew);

        assertEquals(1, movieCrewRepository.count());
        assertEquals(movieCrewPublicDto.id(), movieCrewResponse.id());
        assertEquals(movieCrewCreateDto.fullName(), movieCrewResponse.name(), movieCrew.getFullName());
        assertEquals(movieCrewCreateDto.birthDate(), movieCrewResponse.birthDate(), movieCrew.getBirthDate().toString());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update movie crew without authorization and expect status 403")
    void testUpdateMovieCrewWithoutAuthorization() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME, BIRTH_DATE);
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("Updated Name", new Date());

        mockMvc.perform(put(ADMIN_API_PATH + "/" + movieCrewPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isForbidden());

        MovieCrew movieCrew = movieCrewRepository.findById(movieCrewPublicDto.id()).orElse(null);
        assertNotNull(movieCrew);

        assertEquals(NAME, movieCrew.getFullName());
        assertEquals(BIRTH_DATE, movieCrew.getBirthDate());
    }

    @Test
    @DisplayName("Test update movie crew without authentication and expect status 401")
    void testUpdateMovieCrewWithoutAuthentication() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME, BIRTH_DATE);
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto("Updated Name", new Date());

        mockMvc.perform(put(ADMIN_API_PATH + "/" + movieCrewPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isUnauthorized());

        MovieCrew movieCrew = movieCrewRepository.findById(movieCrewPublicDto.id()).orElse(null);
        assertNotNull(movieCrew);

        assertEquals(NAME, movieCrew.getFullName());
        assertEquals(BIRTH_DATE, movieCrew.getBirthDate());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test update movie crew with null data and expect status 400")
    void testUpdateMovieCrewWithInvalidData() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME, BIRTH_DATE);
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(null, null);

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/" + movieCrewPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.message().contains(INVALID_NAME));
        assertTrue(error.message().contains(INVALID_BIRTH_DATE));

        MovieCrew movieCrew = movieCrewRepository.findById(movieCrewPublicDto.id()).orElse(null);
        assertNotNull(movieCrew);

        assertEquals(NAME, movieCrew.getFullName());
        assertEquals(BIRTH_DATE, movieCrew.getBirthDate());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test update movie crew not found and expect status 404")
    void testUpdateMovieCrewNotFound() throws Exception {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(NAME, BIRTH_DATE);

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(movieCrewCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(MOVIE_CREW_NOT_FOUND + 1, error.message());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test delete movie crew and expect status 204")
    void testDeleteMovieCrew() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME, BIRTH_DATE);

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + movieCrewPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(0, movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete movie crew without authorization and expect status 403")
    void testDeleteMovieCrewWithoutAuthorization() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME, BIRTH_DATE);

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + movieCrewPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        assertEquals(1, movieCrewRepository.count());
    }

    @Test
    @DisplayName("Test delete movie crew without authentication and expect status 401")
    void testDeleteMovieCrewWithoutAuthentication() throws Exception {
        MovieCrewPublicDto movieCrewPublicDto = createMovieCrew(NAME, BIRTH_DATE);

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + movieCrewPublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        assertEquals(1, movieCrewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = {"ADMIN"})
    @DisplayName("Test delete movie crew not found and expect status 404")
    void testDeleteMovieCrewNotFound() throws Exception {
        mockMvc.perform(delete(ADMIN_API_PATH + "/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


}
