//package org.somuga.e2e;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.somuga.aspect.Error;
//import org.somuga.converter.GameGenreConverter;
//import org.somuga.dto.game_genre.GameGenreCreateDto;
//import org.somuga.dto.game_genre.GameGenrePublicDto;
//import org.somuga.entity.GameGenre;
//import org.somuga.repository.GameGenreRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.somuga.util.message.Messages.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@ContextConfiguration
//@ActiveProfiles("test")
//class GameGenreControllerTest {
//
//    private static final ObjectMapper mapper = new ObjectMapper();
//    private final String USER = "google-auth2|1234567890";
//    private final String PRIVATE_API_PATH = "/api/v1/game_genre/private";
//    private final String PUBLIC_API_PATH = "/api/v1/game_genre/public";
//
//    MockMvc mockMvc;
//    @Autowired
//    private GameGenreRepository gameGenreRepository;
//    @Autowired
//    private WebApplicationContext controller;
//    @MockBean
//    @SuppressWarnings("unused")
//    private JwtDecoder jwtDecoder;
//
//    @AfterEach
//    public void cleanUp() {
//        gameGenreRepository.deleteAll();
//    }
//
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(controller)
//                .apply(springSecurity())
//                .build();
//    }
//
//    public GameGenrePublicDto createGameGenre(String genreName) {
//        GameGenre gameGenre = new GameGenre(genreName);
//        return GameGenreConverter.fromEntityToPublicDto(gameGenreRepository.save(gameGenre));
//    }
//
//    @Test
//    @WithMockUser(username = USER)
//    @DisplayName("Create a game genre and expect a 201 status code")
//    void createGameGenre() throws Exception {
//        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action");
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        GameGenrePublicDto gameGenrePublicDto = mapper.readValue(response, GameGenrePublicDto.class);
//
//        assertEquals(gameGenreCreateDto.genreName(), gameGenrePublicDto.genreName());
//        assertNotNull(gameGenrePublicDto.id());
//    }
//
//    @Test
//    @DisplayName("Create a game genre without authentication and expect a 401 status code")
//    void createGameGenreWithoutAuthentication() throws Exception {
//        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action");
//
//        mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
//                .andExpect(status().isUnauthorized());
//    }
//
//
//    @Test
//    @WithMockUser(username = USER)
//    @DisplayName("Create a game genre with an empty genre fullName and expect a 400 status code")
//    void createGameGenreWithEmptyGenreName() throws Exception {
//        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("");
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertTrue(error.getMessage().contains(INVALID_GENRE_NAME));
//        assertEquals(400, error.getStatus());
//        assertEquals("POST", error.getMethod());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//    }
//
//    @Test
//    @WithMockUser(username = USER)
//    @DisplayName("Create a game genre with an invalid genre fullName and expect a 400 status code")
//    void createGameGenreWithInvalidGenreName() throws Exception {
//        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action!");
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertTrue(error.getMessage().contains(INVALID_GENRE_NAME));
//        assertEquals(400, error.getStatus());
//        assertEquals("POST", error.getMethod());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//    }
//
//    @Test
//    @WithMockUser(username = USER)
//    @DisplayName("Create a game genre with a genre fullName that already exists and expect a 400 status code")
//    void createGameGenreWithDuplicateGenreName() throws Exception {
//        createGameGenre("Action");
//
//        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action");
//
//        String response = mockMvc.perform(post(PRIVATE_API_PATH)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(gameGenreCreateDto)))
//                .andExpect(status().isBadRequest())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertTrue(error.getMessage().contains(GENRE_ALREADY_EXISTS + gameGenreCreateDto.genreName()));
//        assertEquals(400, error.getStatus());
//        assertEquals("POST", error.getMethod());
//        assertEquals(PRIVATE_API_PATH, error.getPath());
//    }
//
//    @Test
//    @DisplayName("Test get all game genres and expect a 200 status code")
//    void getAllGameGenres() throws Exception {
//        createGameGenre("Action");
//        createGameGenre("Adventure");
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        GameGenrePublicDto[] gameGenrePublicDtos = mapper.readValue(response, GameGenrePublicDto[].class);
//
//        assertEquals(2, gameGenrePublicDtos.length);
//    }
//
//    @Test
//    @DisplayName("Test get all game genres with pagination and expect a 200 status code")
//    void getAllGameGenresWithPagination() throws Exception {
//        createGameGenre("Action");
//        createGameGenre("Adventure");
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=1"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        GameGenrePublicDto[] gameGenrePublicDtos = mapper.readValue(response, GameGenrePublicDto[].class);
//
//        assertEquals(1, gameGenrePublicDtos.length);
//    }
//
//    @Test
//    @DisplayName("Test get by id and expect a 200 status code")
//    void getGameGenreById() throws Exception {
//        GameGenrePublicDto gameGenrePublicDto = createGameGenre("Action");
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + gameGenrePublicDto.id()))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        GameGenrePublicDto gameGenrePublicDtoResponse = mapper.readValue(response, GameGenrePublicDto.class);
//
//        assertEquals(gameGenrePublicDto.id(), gameGenrePublicDtoResponse.id());
//        assertEquals(gameGenrePublicDto.genreName(), gameGenrePublicDtoResponse.genreName());
//    }
//
//    @Test
//    @DisplayName("Test get by id with an invalid id and expect a 404 status code")
//    void getGameGenreByInvalidId() throws Exception {
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/9999999"))
//                .andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString();
//
//        Error error = mapper.readValue(response, Error.class);
//
//        assertTrue(error.getMessage().contains(GENRE_NOT_FOUND + 9999999));
//        assertEquals(404, error.getStatus());
//        assertEquals("GET", error.getMethod());
//        assertEquals(PUBLIC_API_PATH + "/9999999", error.getPath());
//    }
//
//    @Test
//    @DisplayName("Test search by fullName and expect a 200 status code")
//    void searchGameGenreByName() throws Exception {
//        createGameGenre("Action");
//        createGameGenre("Adventure");
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/action"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        GameGenrePublicDto[] gameGenrePublicDtos = mapper.readValue(response, GameGenrePublicDto[].class);
//
//        assertEquals(1, gameGenrePublicDtos.length);
//    }
//
//    @Test
//    @DisplayName("Test search by fullName with pagination and expect a 200 status code")
//    void searchGameGenreByNameWithPagination() throws Exception {
//        createGameGenre("Action");
//        createGameGenre("Adventure");
//        createGameGenre("Adventure2");
//        createGameGenre("Adventure3");
//
//        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/Adventure?page=0&size=2"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        GameGenrePublicDto[] gameGenrePublicDtos = mapper.readValue(response, GameGenrePublicDto[].class);
//
//        assertEquals(2, gameGenrePublicDtos.length);
//    }
//
//}
