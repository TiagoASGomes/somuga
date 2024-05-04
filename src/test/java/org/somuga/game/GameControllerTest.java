package org.somuga.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.Error;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Developer;
import org.somuga.entity.Game;
import org.somuga.entity.GameGenre;
import org.somuga.entity.Platform;
import org.somuga.repository.DeveloperRepository;
import org.somuga.repository.GameGenreRepository;
import org.somuga.repository.GameRepository;
import org.somuga.repository.PlatformRepository;
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

import java.util.ArrayList;
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
class GameControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER = "google-auth2|1234567890";
    private final String PUBLIC_API_PATH = "/api/v1/game/public";
    private final String PRIVATE_API_PATH = "/api/v1/game/private";
    private final List<String> platforms = new ArrayList<>(List.of("PC", "PS4", "Xbox One"));
    private final List<String> genres = new ArrayList<>(List.of("Action", "Adventure", "RPG"));
    private final String developer = "CD Projekt Red";
    private final String title = "Cyberpunk 2077";
    private final String description = "A futuristic game";
    private final Date releaseDate = new Date();
    private final String mediaUrl = "https://media.com";
    private final String imageUrl = "https://image.com";
    private final double price = 59.99;
    MockMvc mockMvc;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private GameGenreRepository gameGenreRepository;
    @Autowired
    private DeveloperRepository developerRepository;
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
        gameRepository.deleteAll();
        platformRepository.deleteAll();
        gameGenreRepository.deleteAll();
        developerRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
        createDeveloper(developer);
        genres.forEach(this::createGenre);
        platforms.forEach(this::createPlatform);
    }

    public void createPlatform(String platformName) {
        Platform platform = new Platform();
        platform.setPlatformName(platformName.toLowerCase());
        platformRepository.save(platform);
    }

    public void createGenre(String genreName) {
        GameGenre gameGenre = new GameGenre();
        gameGenre.setGenre(genreName.toLowerCase());
        gameGenreRepository.save(gameGenre);
    }

    public void createDeveloper(String developerName) {
        Developer developer = new Developer();
        developer.setDeveloperName(developerName.toLowerCase());
        developerRepository.save(developer);
    }

    public GamePublicDto createGame(String title, String description, Date releaseDate, double price, String developerName, List<String> genreName, List<String> platformNames, String mediaUrl, String imageUrl) throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developerName, genreName, platformNames, price, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, GamePublicDto.class);
    }

    public Error createGameBadRequest(String title, String description, Date releaseDate, double price, String developerName, List<String> genreName, List<String> platformNames, String mediaUrl, String imageUrl) throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developerName, genreName, platformNames, price, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, Error.class);
    }

    public Error createGameNotFound(String title, String description, Date releaseDate, double price, String developerName, List<String> genreName, List<String> platformNames, String mediaUrl, String imageUrl) throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developerName, genreName, platformNames, price, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, Error.class);
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game and expect 201")
    void testCreateGame() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        GamePublicDto gamePublicDto = mapper.readValue(response, GamePublicDto.class);

        assertNotNull(gamePublicDto.id());
        assertEquals(title, gamePublicDto.title());
        assertTrue(gamePublicDto.developer().developerName().equalsIgnoreCase(developer));
        assertEquals(price, gamePublicDto.price());
        assertEquals(description, gamePublicDto.description());
        platforms.forEach(platform -> {
            boolean found = gamePublicDto.platforms().stream().anyMatch(p -> p.platformName().equalsIgnoreCase(platform));
            assertTrue(found);
        });
        genres.forEach(genre -> {
            boolean found = gamePublicDto.genres().stream().anyMatch(g -> g.genreName().equalsIgnoreCase(genre));
            assertTrue(found);
        });
        assertEquals(releaseDate, gamePublicDto.releaseDate());
        assertEquals(0, gamePublicDto.reviews());
        assertEquals(0, gamePublicDto.likes());
        assertEquals(mediaUrl, gamePublicDto.mediaUrl());
        assertEquals(imageUrl, gamePublicDto.imageUrl());
        assertEquals(1, gameRepository.count());
    }

    @Test
    @DisplayName("Test create game unauthorized and expect 401")
    void testCreateGameUnauthorized() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, description, mediaUrl, imageUrl);

        mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isUnauthorized());

        assertEquals(0, gameRepository.count());

    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with invalid developer fullName and expect 404")
    void testCreateGameWithInvalidDeveloperName() throws Exception {
        String developerName = "Invalid Developer";

        Error error = createGameNotFound(title, description, releaseDate, price, developerName, genres, platforms, mediaUrl, imageUrl);

        assertEquals(DEVELOPER_NOT_FOUND_NAME + developerName, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with invalid genre fullName and expect 404")
    void testCreateGameWithInvalidGenreName() throws Exception {
        String invalidGenre = "Invalid Genre";
        genres.add(invalidGenre);

        Error error = createGameNotFound(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        assertEquals(GENRE_NOT_FOUND_NAME + invalidGenre, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with invalid platform fullName and expect 404")
    void testCreateGameWithInvalidPlatformName() throws Exception {
        String invalidPlatform = "Invalid Platform";
        platforms.add(invalidPlatform);

        Error error = createGameNotFound(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        assertEquals(PLATFORM_NOT_FOUND_NAME + invalidPlatform, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with invalid title and expect 400")
    void testCreateGameWithInvalidTitle() throws Exception {
        String invalidTitle = "";

        Error error = createGameBadRequest(invalidTitle, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        assertTrue(error.getMessage().contains(INVALID_TITLE));
        assertEquals(400, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with invalid description and expect 400")
    void testCreateGameWithInvalidDescription() throws Exception {
        String invalidDescription = "";

        Error error = createGameBadRequest(title, invalidDescription, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        assertTrue(error.getMessage().contains(INVALID_DESCRIPTION));
        assertEquals(400, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with invalid price and expect 400")
    void testCreateGameWithInvalidPrice() throws Exception {
        double invalidPrice = -1;

        Error error = createGameBadRequest(title, description, releaseDate, invalidPrice, developer, genres, platforms, mediaUrl, imageUrl);

        assertTrue(error.getMessage().contains(INVALID_PRICE));
        assertEquals(400, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with invalid release date and expect 400")
    void testCreateGameWithInvalidReleaseDate() throws Exception {
        Date invalidReleaseDate = new Date(System.currentTimeMillis() + 100000);

        Error error = createGameBadRequest(title, description, invalidReleaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        assertTrue(error.getMessage().contains(INVALID_RELEASE_DATE));
        assertEquals(400, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with invalid media url and expect 400")
    void testCreateGameWithInvalidMediaUrl() throws Exception {
        String invalidMediaUrl = "";

        Error error = createGameBadRequest(title, description, releaseDate, price, developer, genres, platforms, invalidMediaUrl, imageUrl);

        assertTrue(error.getMessage().contains(INVALID_MEDIA_URL));
        assertEquals(400, error.getStatus());
        assertEquals(0, gameRepository.count());
    }


    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games and expect 200")
    void testGetAllGames() throws Exception {
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        }

        String response = mockMvc.perform(get(PUBLIC_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(3, gamePublicDtos.size());
        assertEquals(3, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games with pagination and expect 200")
    void testGetAllGamesWithPagination() throws Exception {
        for (int i = 0; i < 4; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        }

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, gamePublicDtos.size());
        assertEquals(4, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get games by platform and expect 200")
    void testGetGamesByPlatform() throws Exception {
        createGame(title, description, releaseDate, price, developer, genres, List.of(platforms.get(1)), mediaUrl, imageUrl);
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        }

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/platform/" + platforms.get(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(3, gamePublicDtos.size());
        assertEquals(4, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get games by platform with pagination and expect 200")
    void testGetGamesByPlatformWithPagination() throws Exception {
        createGame(title, description, releaseDate, price, developer, genres, List.of(platforms.get(1)), mediaUrl, imageUrl);
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        }

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/platform/" + platforms.get(0) + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, gamePublicDtos.size());
        assertEquals(4, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get games by genre and expect 200")
    void testGetGamesByGenre() throws Exception {
        createGame(title, description, releaseDate, price, developer, List.of(genres.get(1)), platforms, mediaUrl, imageUrl);
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        }

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/genre/" + genres.get(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(3, gamePublicDtos.size());
        assertEquals(4, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get games by genre with pagination and expect 200")
    void testGetGamesByGenreWithPagination() throws Exception {
        createGame(title, description, releaseDate, price, developer, List.of(genres.get(1)), platforms, mediaUrl, imageUrl);
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        }

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/genre/" + genres.get(0) + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, gamePublicDtos.size());
        assertEquals(4, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get games by developer and expect 200")
    void testGetGamesByDeveloper() throws Exception {
        String developer2 = "Ubisoft";
        createDeveloper(developer2);
        createGame(title, description, releaseDate, price, developer2, genres, platforms, mediaUrl, imageUrl);
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        }

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/developer/" + developer)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(3, gamePublicDtos.size());
        assertEquals(4, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get games by developer with pagination and expect 200")
    void testGetGamesByDeveloperWithPagination() throws Exception {
        String developer2 = "Ubisoft";
        createDeveloper(developer2);
        createGame(title, description, releaseDate, price, developer2, genres, platforms, mediaUrl, imageUrl);
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        }

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/developer/" + developer + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, gamePublicDtos.size());
        assertEquals(4, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test search games by fullName and expect 200")
    void testSearchGamesByName() throws Exception {
        createGame("DifferentTitle", description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        for (int i = 0; i < 3; i++) {
            createGame(title + i, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        }

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/" + title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(3, gamePublicDtos.size());
        assertEquals(4, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test search games by fullName with pagination and expect 200")
    void testSearchGamesByNameWithPagination() throws Exception {
        createGame("DifferentTitle", description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        for (int i = 0; i < 3; i++) {
            createGame(title + i, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);
        }

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/search/" + title + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, gamePublicDtos.size());
        assertEquals(4, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get game by id and expect 200")
    void testGetGameById() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + gamePublicDto.id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GamePublicDto gamePublicDtoResponse = mapper.readValue(response, GamePublicDto.class);

        assertEquals(gamePublicDto.id(), gamePublicDtoResponse.id());
        assertEquals(gamePublicDto.title(), gamePublicDtoResponse.title());
        assertEquals(gamePublicDto.developer().developerName(), gamePublicDtoResponse.developer().developerName());
        assertEquals(gamePublicDto.price(), gamePublicDtoResponse.price());
        assertEquals(gamePublicDto.description(), gamePublicDtoResponse.description());
        assertEquals(gamePublicDto.releaseDate(), gamePublicDtoResponse.releaseDate());
        assertEquals(gamePublicDto.reviews(), gamePublicDtoResponse.reviews());
        assertEquals(gamePublicDto.likes(), gamePublicDtoResponse.likes());
        gamePublicDtoResponse.platforms().forEach(platform -> {
            boolean found = gamePublicDto.platforms().stream().anyMatch(p -> p.platformName().equalsIgnoreCase(platform.platformName()));
            assertTrue(found);
        });
        gamePublicDtoResponse.genres().forEach(genre -> {
            boolean found = gamePublicDto.genres().stream().anyMatch(g -> g.genreName().equalsIgnoreCase(genre.genreName()));
            assertTrue(found);
        });
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get game by invalid id and expect 404")
    void testGetGameByInvalidId() throws Exception {
        String invalidId = "9999999";
        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(GAME_NOT_FOUND + invalidId, error.getMessage());
        assertEquals(404, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game and expect 200")
    void testUpdateGame() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        String newTitle = "New Title";
        String newDescription = "New Description";
        Date newReleaseDate = new Date();
        double newPrice = 49.99;
        String newDeveloper = "Ubisoft";
        createDeveloper(newDeveloper);
        List<String> newPlatforms = List.of("PC");
        List<String> newGenres = List.of("Action");

        GameCreateDto gameCreateDto = new GameCreateDto(newTitle, newReleaseDate, newDeveloper, newGenres, newPlatforms, newPrice, newDescription, mediaUrl, imageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + gamePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GamePublicDto gamePublicDtoResponse = mapper.readValue(response, GamePublicDto.class);

        assertEquals(gamePublicDto.id(), gamePublicDtoResponse.id());
        assertEquals(newTitle, gamePublicDtoResponse.title());
        assertEquals(newDeveloper.toLowerCase(), gamePublicDtoResponse.developer().developerName());
        assertEquals(newPrice, gamePublicDtoResponse.price());
        assertEquals(newDescription, gamePublicDtoResponse.description());
        assertEquals(newPlatforms.get(0).toLowerCase(), gamePublicDtoResponse.platforms().get(0).platformName());
        assertEquals(newGenres.get(0).toLowerCase(), gamePublicDtoResponse.genres().get(0).genreName());
        assertEquals(newReleaseDate, gamePublicDtoResponse.releaseDate());
        assertEquals(0, gamePublicDtoResponse.reviews());
        assertEquals(0, gamePublicDtoResponse.likes());
    }

    @Test
    @DisplayName("Test update game unauthorized and expect 401")
    void testUpdateGameUnauthorized() throws Exception {

        String newTitle = "New Title";
        String newDescription = "New Description";
        Date newReleaseDate = new Date();
        double newPrice = 49.99;
        String newDeveloper = "Ubisoft";
        createDeveloper(newDeveloper);
        List<String> newPlatforms = List.of("PC");
        List<String> newGenres = List.of("Action");

        GameCreateDto gameCreateDto = new GameCreateDto(newTitle, newReleaseDate, newDeveloper, newGenres, newPlatforms, newPrice, newDescription, mediaUrl, imageUrl);

        mockMvc.perform(put(PRIVATE_API_PATH + "/" + 1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game with invalid developer fullName and expect 404")
    void testUpdateGameWithInvalidDeveloperName() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        String newDeveloper = "Invalid Developer";
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, newDeveloper, genres, platforms, price, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + gamePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(DEVELOPER_NOT_FOUND_NAME + newDeveloper, error.getMessage());
        assertEquals(404, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game with invalid genre fullName and expect 404")
    void testUpdateGameWithInvalidGenreName() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        String invalidGenre = "Invalid Genre";
        genres.add(invalidGenre);
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + gamePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(GENRE_NOT_FOUND_NAME + invalidGenre, error.getMessage());
        assertEquals(404, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game with invalid platform fullName and expect 404")
    void testUpdateGameWithInvalidPlatformName() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        String invalidPlatform = "Invalid Platform";
        platforms.add(invalidPlatform);
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + gamePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(PLATFORM_NOT_FOUND_NAME + invalidPlatform, error.getMessage());
        assertEquals(404, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game with invalid title and expect 400")
    void testUpdateGameWithInvalidTitle() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        String invalidTitle = "";
        GameCreateDto gameCreateDto = new GameCreateDto(invalidTitle, releaseDate, developer, genres, platforms, price, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + gamePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_TITLE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game with invalid description and expect 400")
    void testUpdateGameWithInvalidDescription() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        String invalidDescription = "";
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, invalidDescription, mediaUrl, imageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + gamePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_DESCRIPTION));
        assertEquals(400, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game with invalid price and expect 400")
    void testUpdateGameWithInvalidPrice() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        double invalidPrice = -1;
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, invalidPrice, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + gamePublicDto.id())
                        .with(csrf())

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_PRICE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game with invalid release date and expect 400")
    void testUpdateGameWithInvalidReleaseDate() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        Date invalidReleaseDate = new Date(System.currentTimeMillis() + 10000);
        GameCreateDto gameCreateDto = new GameCreateDto(title, invalidReleaseDate, developer, genres, platforms, price, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + gamePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_RELEASE_DATE));
        assertEquals(400, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game with invalid id and expect 404")
    void testUpdateGameWithInvalidId() throws Exception {
        String invalidId = "9999999";
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, description, mediaUrl, imageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + invalidId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(GAME_NOT_FOUND + invalidId, error.getMessage());
        assertEquals(404, error.getStatus());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game with invalid media url and expect 400")
    void testUpdateGameWithInvalidMediaUrl() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        String invalidMediaUrl = "";
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, description, invalidMediaUrl, imageUrl);

        String response = mockMvc.perform(put(PRIVATE_API_PATH + "/" + gamePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_MEDIA_URL));
        assertEquals(400, error.getStatus());
    }

    @Test
    @WithMockUser(username = "different-user|1234567890")
    @DisplayName("Test update game with different user and expect 403")
    void testUpdateGameWithDifferentUser() throws Exception {
        Game game = new Game();
        game.setMediaCreatorId(USER);
        gameRepository.save(game);

        String newTitle = "New Title";
        String newDescription = "New Description";
        Date newReleaseDate = new Date();
        double newPrice = 49.99;
        String newDeveloper = "Ubisoft";
        createDeveloper(newDeveloper);
        List<String> newPlatforms = List.of("PC");
        List<String> newGenres = List.of("Action");

        GameCreateDto gameCreateDto = new GameCreateDto(newTitle, newReleaseDate, newDeveloper, newGenres, newPlatforms, newPrice, newDescription, mediaUrl, imageUrl);

        mockMvc.perform(put(PRIVATE_API_PATH + "/" + game.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test delete game and expect 204")
    void testDeleteGame() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms, mediaUrl, imageUrl);

        mockMvc.perform(delete(PRIVATE_API_PATH + "/" + gamePublicDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test delete game with invalid id and expect 404")
    void testDeleteGameWithInvalidId() throws Exception {
        String invalidId = "9999999";

        String response = mockMvc.perform(delete(PRIVATE_API_PATH + "/" + invalidId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(GAME_NOT_FOUND + invalidId, error.getMessage());
        assertEquals(404, error.getStatus());
    }


}
