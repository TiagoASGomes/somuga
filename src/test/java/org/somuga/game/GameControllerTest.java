package org.somuga.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
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

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GameControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_PATH = "/api/v1/game";
    private final List<String> platforms = List.of("PC", "PS4", "Xbox One");
    private final List<String> genres = List.of("Action", "Adventure", "RPG");
    private final String developer = "CD Projekt Red";
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
        Date releaseDate = new Date();
        GameCreateDto gameCreateDto = new GameCreateDto("Cyberpunk 2077", releaseDate, developer, genres, platforms, 59.99, "A futuristic game");

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        GamePublicDto gamePublicDto = mapper.readValue(response, GamePublicDto.class);

        assertNotNull(gamePublicDto.id());
        assertEquals("Cyberpunk 2077", gamePublicDto.title());
        assertTrue(gamePublicDto.developer().developerName().equalsIgnoreCase(developer));
        assertEquals(59.99, gamePublicDto.price());
        assertEquals("A futuristic game", gamePublicDto.description());
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


}
