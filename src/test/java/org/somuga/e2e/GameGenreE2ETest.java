package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.aspect.Error;
import org.somuga.converter.GameGenreConverter;
import org.somuga.dto.game_genre.GameGenreCreateDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.entity.GameGenre;
import org.somuga.repository.GameGenreRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
class GameGenreE2ETest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER = "google-auth2|1234567890";
    private final String PUBLIC_API_PATH = "/api/v1/game/genre/public";
    private final String ADMIN_API_PATH = "/api/v1/game/genre/admin";

    MockMvc mockMvc;
    @Autowired
    private GameGenreRepository gameGenreRepository;
    @Autowired
    private WebApplicationContext controller;
    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @AfterEach
    public void cleanUp() {
        gameGenreRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
    }

    public GameGenrePublicDto createGameGenre(String genreName) {
        GameGenre gameGenre = GameGenre.builder().genre(genreName).build();
        return GameGenreConverter.fromEntityToPublicDto(gameGenreRepository.save(gameGenre));
    }

    private void assertErrors(Error error, int status, String path, String method) {
        assertEquals(status, error.getStatus());
        assertEquals(path, error.getPath());
        assertEquals(method, error.getMethod());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create a game genre and expect a 201 status code")
    void createGameGenre() throws Exception {
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action");

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        GameGenrePublicDto gameGenrePublicDto = mapper.readValue(response, GameGenrePublicDto.class);

        assertEquals(1, gameGenreRepository.count());
        assertNotNull(gameGenrePublicDto.id());

        GameGenre gameGenre = gameGenreRepository.findById(gameGenrePublicDto.id()).orElse(null);
        assertNotNull(gameGenre);
        assertEquals(gameGenreCreateDto.genreName(), gameGenrePublicDto.genreName(), gameGenre.getGenre());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create a game genre without authorization and expect a 403 status code")
    void createGameGenreWithoutAuthorization() throws Exception {
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action");

        mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isForbidden());

        assertEquals(0, gameGenreRepository.count());
    }

    @Test
    @DisplayName("Test create a game genre without authentication and expect a 401 status code")
    void createGameGenreWithoutAuthentication() throws Exception {
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action");

        mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isUnauthorized());

        assertEquals(0, gameGenreRepository.count());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create a game genre with an existing genre name and expect a 400 status code")
    void createGameGenreWithExistingGenreName() throws Exception {
        createGameGenre("Action");

        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action");

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(GENRE_ALREADY_EXISTS + gameGenreCreateDto.genreName(), error.getMessage());
        assertErrors(error, 400, ADMIN_API_PATH, "POST");
        assertEquals(1, gameGenreRepository.count());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create a game genre with an empty genre name and expect a 400 status code")
    void createGameGenreWithEmptyGenreName() throws Exception {
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("");

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_GENRE_NAME));
        assertErrors(error, 400, ADMIN_API_PATH, "POST");
        assertEquals(0, gameGenreRepository.count());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create a game genre with a invalid genre name and expect a 400 status code")
    void createGameGenreWithInvalidGenreName() throws Exception {
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action!");

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_GENRE_NAME));
        assertErrors(error, 400, ADMIN_API_PATH, "POST");
        assertEquals(0, gameGenreRepository.count());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test create a game genre with a genre name that is too long and expect a 400 status code")
    void createGameGenreWithLongGenreName() throws Exception {
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("ABCDEF".repeat(10));

        String response = mockMvc.perform(post(ADMIN_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_GENRE_NAME));
        assertErrors(error, 400, ADMIN_API_PATH, "POST");
        assertEquals(0, gameGenreRepository.count());
    }

    @Test
    @DisplayName("Test get all game genres and expect 200")
    void getAllGameGenres() throws Exception {
        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");

        String response = mockMvc.perform(get(PUBLIC_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameGenrePublicDto[] gameGenrePublicDtos = mapper.readValue(response, GameGenrePublicDto[].class);

        assertEquals(1, gameGenrePublicDtos.length);
        assertEquals(gameGenrePublicDto, gameGenrePublicDtos[0]);
    }

    @Test
    @DisplayName("Test get all game genres with a search query and expect 200")
    void getAllGameGenresWithSearchQuery() throws Exception {
        createGameGenre("Action");
        createGameGenre("Adventure");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?name=Action")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameGenrePublicDto[] gameGenrePublicDtos = mapper.readValue(response, GameGenrePublicDto[].class);

        assertEquals(1, gameGenrePublicDtos.length);
        assertEquals("Action", gameGenrePublicDtos[0].genreName());
    }

    @Test
    @DisplayName("Test get all game genres case insensitive with a search query and expect 200")
    void getAllGameGenresCaseInsensitiveWithSearchQuery() throws Exception {
        createGameGenre("Action");
        createGameGenre("Adventure");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?name=action")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameGenrePublicDto[] gameGenrePublicDtos = mapper.readValue(response, GameGenrePublicDto[].class);

        assertEquals(1, gameGenrePublicDtos.length);
        assertEquals("Action", gameGenrePublicDtos[0].genreName());
    }

    @Test
    @DisplayName("Test get all game genres with a search query that does not exist and expect 200")
    void getAllGameGenresWithSearchQueryThatDoesNotExist() throws Exception {
        createGameGenre("Action");
        createGameGenre("Adventure");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?name=Shooter")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameGenrePublicDto[] gameGenrePublicDtos = mapper.readValue(response, GameGenrePublicDto[].class);

        assertEquals(0, gameGenrePublicDtos.length);
    }

    @Test
    @DisplayName("Test get a game genre by id and expect 200")
    void getGameGenreById() throws Exception {
        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + gameGenrePublicDto.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameGenrePublicDto gameGenrePublicDtoResponse = mapper.readValue(response, GameGenrePublicDto.class);

        assertEquals(gameGenrePublicDto, gameGenrePublicDtoResponse);
    }

    @Test
    @DisplayName("Test get a game genre by id that does not exist and expect 404")
    void getGameGenreByIdThatDoesNotExist() throws Exception {
        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(GENRE_NOT_FOUND + 1, error.getMessage());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test update a game genre and expect a 200 status code")
    void updateGameGenre() throws Exception {
        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Adventure");

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/" + gameGenrePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameGenrePublicDto gameGenrePublicDtoResponse = mapper.readValue(response, GameGenrePublicDto.class);
        GameGenre gameGenre = gameGenreRepository.findById(gameGenrePublicDto.id()).orElse(null);
        assertNotNull(gameGenre);

        assertEquals(1, gameGenreRepository.count());
        assertEquals(gameGenrePublicDto.id(), gameGenrePublicDtoResponse.id());
        assertEquals(gameGenreCreateDto.genreName(), gameGenrePublicDtoResponse.genreName(), gameGenre.getGenre());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update a game genre without authorization and expect a 403 status code")
    void updateGameGenreWithoutAuthorization() throws Exception {
        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Adventure");

        mockMvc.perform(put(ADMIN_API_PATH + "/" + gameGenrePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Test update a game genre without authentication and expect a 401 status code")
    void updateGameGenreWithoutAuthentication() throws Exception {
        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Adventure");

        mockMvc.perform(put(ADMIN_API_PATH + "/" + gameGenrePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test update a game genre with an existing genre name and expect a 400 status code")
    void updateGameGenreWithExistingGenreName() throws Exception {
        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");
        createGameGenre("Adventure");
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Adventure");

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/" + gameGenrePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        GameGenre gameGenre = gameGenreRepository.findById(gameGenrePublicDto.id()).orElse(null);
        assertNotNull(gameGenre);

        assertEquals(GENRE_ALREADY_EXISTS + gameGenreCreateDto.genreName(), error.getMessage());
        assertErrors(error, 400, ADMIN_API_PATH + "/" + gameGenrePublicDto.id(), "PUT");
        assertNotEquals(gameGenreCreateDto.genreName(), gameGenre.getGenre());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test update a game genre with invalid name and expect a 400 status code")
    void updateGameGenreWithInvalidGenreName() throws Exception {
        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action!");

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/" + gameGenrePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);
        GameGenre gameGenre = gameGenreRepository.findById(gameGenrePublicDto.id()).orElse(null);
        assertNotNull(gameGenre);

        assertTrue(error.getMessage().contains(INVALID_GENRE_NAME));
        assertErrors(error, 400, ADMIN_API_PATH + "/" + gameGenrePublicDto.id(), "PUT");
        assertNotEquals(gameGenreCreateDto.genreName(), gameGenre.getGenre());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test update a game genre not found and expect a 404 status code")
    void updateGameGenreNotFound() throws Exception {
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action");

        String response = mockMvc.perform(put(ADMIN_API_PATH + "/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(GENRE_NOT_FOUND + 1, error.getMessage());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test delete a game genre and expect a 204 status code")
    void deleteGameGenre() throws Exception {
        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + gameGenrePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(0, gameGenreRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test delete a game genre without authorization and expect a 403 status code")
    void deleteGameGenreWithoutAuthorization() throws Exception {
        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + gameGenrePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        assertEquals(1, gameGenreRepository.count());
    }

    @Test
    @DisplayName("Test delete a game genre without authentication and expect a 401 status code")
    void deleteGameGenreWithoutAuthentication() throws Exception {
        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");

        mockMvc.perform(delete(ADMIN_API_PATH + "/" + gameGenrePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        assertEquals(1, gameGenreRepository.count());
    }

    @Test
    @WithMockUser(username = USER, authorities = {"ADMIN"})
    @DisplayName("Test delete a game genre not found and expect a 404 status code")
    void deleteGameGenreNotFound() throws Exception {
        String response = mockMvc.perform(delete(ADMIN_API_PATH + "/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(GENRE_NOT_FOUND + 1, error.getMessage());
    }

}
