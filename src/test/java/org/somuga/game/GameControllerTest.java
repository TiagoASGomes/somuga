package org.somuga.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.Error;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Developer;
import org.somuga.entity.GameGenre;
import org.somuga.entity.Platform;
import org.somuga.repository.DeveloperRepository;
import org.somuga.repository.GameGenreRepository;
import org.somuga.repository.GameRepository;
import org.somuga.repository.PlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.message.Messages.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_PATH = "/api/v1/game";
    private final List<String> platforms = new ArrayList<>(List.of("PC", "PS4", "Xbox One"));
    private final List<String> genres = new ArrayList<>(List.of("Action", "Adventure", "RPG"));
    private final String developer = "CD Projekt Red";
    private final String title = "Cyberpunk 2077";
    private final String description = "A futuristic game";
    private final Date releaseDate = new Date();
    private final double price = 59.99;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private GameGenreRepository gameGenreRepository;
    @Autowired
    private DeveloperRepository developerRepository;

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

    public GamePublicDto createGame(String title, String description, Date releaseDate, double price, String developerName, List<String> genreName, List<String> platformNames) throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developerName, genreName, platformNames, price, description);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readValue(response, GamePublicDto.class);
    }

    @Test
    @DisplayName("Test create game and expect 201")
    void testCreateGame() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, description);

        String response = mockMvc.perform(post(API_PATH)
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
    }

    @Test
    @DisplayName("Test create game with invalid developer name and expect 404")
    void testCreateGameWithInvalidDeveloperName() throws Exception {
        String developerName = "Invalid Developer";
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developerName, genres, platforms, price, description);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(DEVELOPER_NOT_FOUND_NAME + developerName, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @DisplayName("Test create game with invalid genre name and expect 404")
    void testCreateGameWithInvalidGenreName() throws Exception {
        String invalidGenre = "Invalid Genre";
        genres.add(invalidGenre);
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, description);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(GENRE_NOT_FOUND_NAME + invalidGenre, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @DisplayName("Test create game with invalid platform name and expect 404")
    void testCreateGameWithInvalidPlatformName() throws Exception {
        String invalidPlatform = "Invalid Platform";
        platforms.add(invalidPlatform);
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, description);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(PLATFORM_NOT_FOUND_NAME + invalidPlatform, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @DisplayName("Test create game with invalid title and expect 400")
    void testCreateGameWithInvalidTitle() throws Exception {
        String invalidTitle = "";
        GameCreateDto gameCreateDto = new GameCreateDto(invalidTitle, releaseDate, developer, genres, platforms, price, description);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_TITLE));
        assertEquals(400, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @DisplayName("Test create game with invalid description and expect 400")
    void testCreateGameWithInvalidDescription() throws Exception {
        String invalidDescription = "";
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, price, invalidDescription);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_DESCRIPTION));
        assertEquals(400, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @DisplayName("Test create game with invalid price and expect 400")
    void testCreateGameWithInvalidPrice() throws Exception {
        double invalidPrice = -1;
        GameCreateDto gameCreateDto = new GameCreateDto(title, releaseDate, developer, genres, platforms, invalidPrice, description);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_PRICE));
        assertEquals(400, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @DisplayName("Test create game with invalid release date and expect 400")
    void testCreateGameWithInvalidReleaseDate() throws Exception {
        Date invalidReleaseDate = new Date(System.currentTimeMillis() + 10000);
        GameCreateDto gameCreateDto = new GameCreateDto(title, invalidReleaseDate, developer, genres, platforms, price, description);

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(INVALID_RELEASE_DATE));
        assertEquals(400, error.getStatus());
        assertEquals(0, gameRepository.count());
    }

    @Test
    @DisplayName("Test get all games and expect 200")
    void testGetAllGames() throws Exception {
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms);
        }

        String response = mockMvc.perform(get(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(3, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test get all games with pagination and expect 200")
    void testGetAllGamesWithPagination() throws Exception {
        for (int i = 0; i < 4; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms);
        }

        String response = mockMvc.perform(get(API_PATH + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test get games by platform and expect 200")
    void testGetGamesByPlatform() throws Exception {
        createGame(title, description, releaseDate, price, developer, genres, List.of(platforms.get(1)));
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms);
        }

        String response = mockMvc.perform(get(API_PATH + "/platform/" + platforms.get(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(3, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test get games by platform with pagination and expect 200")
    void testGetGamesByPlatformWithPagination() throws Exception {
        createGame(title, description, releaseDate, price, developer, genres, List.of(platforms.get(1)));
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms);
        }

        String response = mockMvc.perform(get(API_PATH + "/platform/" + platforms.get(0) + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test get games by genre and expect 200")
    void testGetGamesByGenre() throws Exception {
        createGame(title, description, releaseDate, price, developer, List.of(genres.get(1)), platforms);
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms);
        }

        String response = mockMvc.perform(get(API_PATH + "/genre/" + genres.get(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(3, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test get games by genre with pagination and expect 200")
    void testGetGamesByGenreWithPagination() throws Exception {
        createGame(title, description, releaseDate, price, developer, List.of(genres.get(1)), platforms);
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms);
        }

        String response = mockMvc.perform(get(API_PATH + "/genre/" + genres.get(0) + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test get games by developer and expect 200")
    void testGetGamesByDeveloper() throws Exception {
        String developer2 = "Ubisoft";
        createDeveloper(developer2);
        createGame(title, description, releaseDate, price, developer2, genres, platforms);
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms);
        }

        String response = mockMvc.perform(get(API_PATH + "/developer/" + developer)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(3, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test get games by developer with pagination and expect 200")
    void testGetGamesByDeveloperWithPagination() throws Exception {
        String developer2 = "Ubisoft";
        createDeveloper(developer2);
        createGame(title, description, releaseDate, price, developer2, genres, platforms);
        for (int i = 0; i < 3; i++) {
            createGame(title, description, releaseDate, price, developer, genres, platforms);
        }

        String response = mockMvc.perform(get(API_PATH + "/developer/" + developer + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test search games by name and expect 200")
    void testSearchGamesByName() throws Exception {
        createGame("DifferentTitle", description, releaseDate, price, developer, genres, platforms);
        for (int i = 0; i < 3; i++) {
            createGame(title + i, description, releaseDate, price, developer, genres, platforms);
        }

        String response = mockMvc.perform(get(API_PATH + "/search/" + title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(3, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test search games by name with pagination and expect 200")
    void testSearchGamesByNameWithPagination() throws Exception {
        createGame("DifferentTitle", description, releaseDate, price, developer, genres, platforms);
        for (int i = 0; i < 3; i++) {
            createGame(title + i, description, releaseDate, price, developer, genres, platforms);
        }

        String response = mockMvc.perform(get(API_PATH + "/search/" + title + "?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<GamePublicDto> gamePublicDtos = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test get game by id and expect 200")
    void testGetGameById() throws Exception {
        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms);

        String response = mockMvc.perform(get(API_PATH + "/" + gamePublicDto.id())
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
    @DisplayName("Test get game by invalid id and expect 404")
    void testGetGameByInvalidId() throws Exception {
        String invalidId = "9999999";
        String response = mockMvc.perform(get(API_PATH + "/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(GAME_NOT_FOUND + invalidId, error.getMessage());
        assertEquals(404, error.getStatus());
    }

    @Test
    @DisplayName("Test update game and expect 200")
    void testUpdateGame() throws Exception {
//        GamePublicDto gamePublicDto = createGame(title, description, releaseDate, price, developer, genres, platforms);
//
//        String newTitle = "New Title";
//        String newDescription = "New Description";
//        Date newReleaseDate = new Date();
//        double newPrice = 49.99;
//        List<String> newPlatforms = List.of("PC", "PS5");
//        List<String> newGenres = List.of("Action", "Adventure");
//
//        GameCreateDto gameCreateDto = new GameCreateDto(newTitle, newReleaseDate, developer, newGenres, newPlatforms, newPrice, newDescription);
//
//        String response = mockMvc.perform(post(API_PATH + "/" + gamePublicDto.id())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(gameCreateDto)))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        GamePublicDto gamePublicDtoResponse = mapper.readValue(response, GamePublicDto.class);
//
//        assertEquals(gamePublicDto.id(), gamePublicDtoResponse.id());
//        assertEquals(newTitle, gamePublicDtoResponse.title());
//        assertEquals(developer, gamePublicDtoResponse.developer().developerName());
//        assertEquals(newPrice, gamePublicDtoResponse.price());
//        assertEquals(newDescription, gamePublicDtoResponse.description());
//        newPlatforms.forEach(platform -> {
//            boolean found = gamePublicDtoResponse.platforms().stream().anyMatch(p -> p.platformName().equalsIgnoreCase(platform));
//            assertTrue(found);
//        });
//        newGenres.forEach(genre -> {
//            boolean found = gamePublicDtoResponse.genres().stream().anyMatch(g -> g.genreName().equalsIgnoreCase(genre));
//            assertTrue(found);
//        });
//        assertEquals(newReleaseDate, gamePublicDtoResponse.releaseDate());
//        assertEquals(0, gamePublicDtoResponse.reviews());
//        assertEquals(0, gamePublicDtoResponse.likes());
    }

}
