package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.ErrorDto;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GameLikePublicDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.dto.platform.PlatformPublicDto;
import org.somuga.entity.*;
import org.somuga.enums.MediaType;
import org.somuga.repository.*;
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

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.testUtils.Utils.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
class GameE2ETest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER = "google-auth2|1234567890";
    private final String PUBLIC_API_PATH = "/api/v1/game/public";
    private final String PRIVATE_API_PATH = "/api/v1/game/private";
    private final String title = "Cyberpunk 2077";
    private final String description = "A futuristic game";
    private final Date releaseDate = new Date();
    private final String mediaUrl = "https://media.com";
    private final String imageUrl = "https://image.com";
    private final double price = 59.99;
    MockMvc mockMvc;
    private Long developer;
    private List<Long> platforms;
    private List<Long> genres;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LikeRepository likeRepository;
    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @BeforeAll
    public static void setUpMapper() {
        mapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    public void cleanUp() {
        likeRepository.deleteAll();
        userRepository.deleteAll();
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
        developer = createDeveloper("CD Projekt Red", List.of("https://twitter.com/CDPROJEKTRED"));
        platforms = List.of(createPlatform("PC"), createPlatform("PS4"));
        genres = List.of(createGenre("Action"), createGenre("RPG"));
    }

    private Long createGenre(String action) {
        GameGenre genre = GameGenre.builder()
                .genre(action)
                .build();
        return gameGenreRepository.save(genre).getId();
    }

    private Long createPlatform(String platform) {
        Platform plat = Platform.builder()
                .platformName(platform)
                .build();
        return platformRepository.save(plat).getId();
    }

    private Long createDeveloper(String developerName, List<String> socials) {
        Developer dev = Developer.builder()
                .developerName(developerName)
                .socials(socials)
                .build();
        return developerRepository.save(dev).getId();
    }

    private GamePublicDto createGame(GameCreateDto gameDto) throws Exception {

        String response = postRequest(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(gameDto), mockMvc);

        return mapper.readValue(response, GamePublicDto.class);
    }

    private void createLike(Long gameId, String userId) {
        Like like = Like.builder()
                .media(Game.builder().id(gameId).build())
                .user(User.builder().id(userId).build())
                .build();

        likeRepository.save(like);
    }

    private void createUser(String userId) {
        User user = User.builder()
                .id(userId)
                .userName("TestUser")
                .build();

        userRepository.save(user);
    }

    private void assertGame(GamePublicDto game, Game gameEntity, String title, String description, Date releaseDate, double price, String mediaUrl, String imageUrl, Long developer, List<Long> platforms, List<Long> genres, String mediaCreatorId, MediaType mediaType) {
        assertEquals(title, game.title());
        assertEquals(title, gameEntity.getTitle());
        assertEquals(description, game.description());
        assertEquals(description, gameEntity.getDescription());
        assertEquals(releaseDate, game.releaseDate());
        assertEquals(releaseDate, gameEntity.getReleaseDate());
        assertEquals(price, game.price());
        assertEquals(price, gameEntity.getPrice());
        assertEquals(mediaUrl, game.mediaUrl());
        assertEquals(mediaUrl, gameEntity.getMediaUrl());
        assertEquals(imageUrl, game.imageUrl());
        assertEquals(imageUrl, gameEntity.getImageUrl());
        assertEquals(developer, game.developer().id());
        assertEquals(developer, gameEntity.getDeveloper().getId());
        assertEquals(platforms.size(), game.platforms().size());
        assertEquals(platforms.size(), gameEntity.getPlatforms().size());
        assertTrue(game.platforms().stream().map(PlatformPublicDto::id).allMatch(platforms::contains));
        assertTrue(gameEntity.getPlatforms().stream().map(Platform::getId).allMatch(platforms::contains));
        assertEquals(genres.size(), game.genres().size());
        assertEquals(genres.size(), gameEntity.getGenres().size());
        assertTrue(game.genres().stream().map(GameGenrePublicDto::id).allMatch(genres::contains));
        assertTrue(gameEntity.getGenres().stream().map(GameGenre::getId).allMatch(genres::contains));
        assertEquals(mediaCreatorId, gameEntity.getMediaCreatorId());
        assertEquals(mediaType, gameEntity.getMediaType());
        assertEquals(mediaType, gameEntity.getMediaType());
        assertEquals(mediaCreatorId, gameEntity.getMediaCreatorId());
    }


    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game and expect 201")
    void testCreateGame() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                title,
                releaseDate,
                developer,
                genres,
                platforms,
                price,
                description,
                mediaUrl,
                imageUrl
        );

        String response = postRequest(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        GamePublicDto game = mapper.readValue(response, GamePublicDto.class);

        assertEquals(1, gameRepository.count());
        Game gameEntity = gameRepository.findById(game.id()).orElse(null);
        assertNotNull(gameEntity);

        assertGame(game, gameEntity, title, description, releaseDate, price, mediaUrl, imageUrl, developer, platforms, genres, USER, MediaType.GAME);
    }

    @Test
    @DisplayName("Test create game unauthenticated and expect 401")
    void testCreateGameUnauthenticated() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                title,
                releaseDate,
                developer,
                genres,
                platforms,
                price,
                description,
                mediaUrl,
                imageUrl
        );

        postRequest(PRIVATE_API_PATH, status().isUnauthorized(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with title exceeding 255 characters and expect 400")
    void testCreateGameTitleExceeding255Characters() throws Exception {
        String longTitle = "a".repeat(256);
        GameCreateDto gameCreateDto = new GameCreateDto(
                longTitle,
                releaseDate,
                developer,
                genres,
                platforms,
                price,
                description,
                mediaUrl,
                imageUrl
        );

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, gameRepository.count());
        assertTrue(errorDto.message().contains(MAX_TITLE_CHARACTERS));
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with description exceeding 1000 characters and expect 400")
    void testCreateGameDescriptionExceeding1000Characters() throws Exception {
        String longDescription = "a".repeat(1001);
        GameCreateDto gameCreateDto = new GameCreateDto(
                title,
                releaseDate,
                developer,
                genres,
                platforms,
                price,
                longDescription,
                mediaUrl,
                imageUrl
        );

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, gameRepository.count());
        assertTrue(errorDto.message().contains(MAX_DESCRIPTION_CHARACTERS));
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with price over 1000 and expect 400")
    void testCreateGamePriceOver1000() throws Exception {
        double highPrice = 1001.0;
        GameCreateDto gameCreateDto = new GameCreateDto(
                title,
                releaseDate,
                developer,
                genres,
                platforms,
                highPrice,
                description,
                mediaUrl,
                imageUrl
        );

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, gameRepository.count());
        assertTrue(errorDto.message().contains(INVALID_PRICE));
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with price under 0 and expect 400")
    void testCreateGamePriceUnder0() throws Exception {
        double lowPrice = -1.0;
        GameCreateDto gameCreateDto = new GameCreateDto(
                title,
                releaseDate,
                developer,
                genres,
                platforms,
                lowPrice,
                description,
                mediaUrl,
                imageUrl
        );

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, gameRepository.count());
        assertTrue(errorDto.message().contains(INVALID_PRICE));
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with empty genres and expect 400")
    void testCreateGameEmptyGenres() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                title,
                releaseDate,
                developer,
                List.of(),
                platforms,
                price,
                description,
                mediaUrl,
                imageUrl
        );

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, gameRepository.count());
        assertTrue(errorDto.message().contains(INVALID_GENRES));
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game with empty platforms and expect 400")
    void testCreateGameEmptyPlatforms() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                title,
                releaseDate,
                developer,
                genres,
                List.of(),
                price,
                description,
                mediaUrl,
                imageUrl
        );

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, gameRepository.count());
        assertTrue(errorDto.message().contains(INVALID_PLATFORMS));
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game developer id not found and expect 404")
    void testCreateGameNoDeveloper() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                title,
                releaseDate,
                0L,
                genres,
                platforms,
                price,
                description,
                mediaUrl,
                imageUrl
        );

        String response = postRequest(PRIVATE_API_PATH, status().isNotFound(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, gameRepository.count());
        assertEquals(DEVELOPER_NOT_FOUND + "0", errorDto.message());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game platform id not found and expect 404")
    void testCreateGameNoPlatforms() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                title,
                releaseDate,
                developer,
                genres,
                List.of(0L),
                price,
                description,
                mediaUrl,
                imageUrl
        );

        String response = postRequest(PRIVATE_API_PATH, status().isNotFound(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, gameRepository.count());
        assertEquals(PLATFORM_NOT_FOUND + "0", errorDto.message());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test create game genre id not found and expect 404")
    void testCreateGameNoGenres() throws Exception {
        GameCreateDto gameCreateDto = new GameCreateDto(
                title,
                releaseDate,
                developer,
                List.of(0L),
                platforms,
                price,
                description,
                mediaUrl,
                imageUrl
        );

        String response = postRequest(PRIVATE_API_PATH, status().isNotFound(), mapper.writeValueAsString(gameCreateDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, gameRepository.count());
        assertEquals(GENRE_NOT_FOUND + "0", errorDto.message());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games and expect 200")
    void testGetAllGames() throws Exception {
        createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 3", new Date(), developer, genres, platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));

        String response = getRequest(PUBLIC_API_PATH, status().isOk(), mockMvc);

        List<GamePublicDto> games = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, games.size());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games paged and expect 200")
    void testGetAllGamesPaged() throws Exception {
        createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 3", new Date(), developer, genres, platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));

        String response = getRequest(PUBLIC_API_PATH + "?page=0&size=1", status().isOk(), mockMvc);

        List<GamePublicDto> games = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(1, games.size());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games with title search criteria and expect 200")
    void testGetAllGamesWithTitle() throws Exception {
        createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 3", new Date(), developer, genres, platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 2", new Date(), developer, genres, platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));

        String response = getRequest(PUBLIC_API_PATH + "?title=The Witcher", status().isOk(), mockMvc);

        List<GamePublicDto> games = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, games.size());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games with platform search criteria and expect 200")
    void testGetAllGamesWithPlatform() throws Exception {
        createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 3", new Date(), developer, genres, platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 2", new Date(), developer, genres, platforms.subList(0, 1), 59.99, "A fantasy game", "https://media.com", "https://image.com"));

        String response = getRequest(PUBLIC_API_PATH + "?platform=PS4", status().isOk(), mockMvc);

        List<GamePublicDto> games = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, games.size());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games with multiple platform search criteria and expect 200")
    void testGetAllGamesWithMultiplePlatforms() throws Exception {
        Long platform3 = createPlatform("XBOX");
        createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 3", new Date(), developer, genres, platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 2", new Date(), developer, genres, List.of(platforms.get(0), platform3), 59.99, "A fantasy game", "https://media.com", "https://image.com"));

        String response = getRequest(PUBLIC_API_PATH + "?platform=PC&platform=XBOX", status().isOk(), mockMvc);

        List<GamePublicDto> games = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(1, games.size());
    }


    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games with genre search criteria and expect 200")
    void testGetAllGamesWithGenre() throws Exception {
        createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 3", new Date(), developer, genres, platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 2", new Date(), developer, genres.subList(0, 1), platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));

        String response = getRequest(PUBLIC_API_PATH + "?genre=RPG", status().isOk(), mockMvc);

        List<GamePublicDto> games = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, games.size());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games with multiple genre search criteria and expect 200")
    void testGetAllGamesWithMultipleGenres() throws Exception {
        Long genre3 = createGenre("Adventure");
        createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 3", new Date(), developer, genres, platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 2", new Date(), developer, List.of(genres.get(0), genre3), platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));

        String response = getRequest(PUBLIC_API_PATH + "?genre=Adventure&genre=Action", status().isOk(), mockMvc);

        List<GamePublicDto> games = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(1, games.size());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games with developer search criteria and expect 200")
    void testGetAllGamesWithDeveloper() throws Exception {
        Long developer2 = createDeveloper("Teste", List.of("https://twitter.com/teste"));
        createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 3", new Date(), developer, genres, platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("The Witcher 2", new Date(), developer2, genres, platforms, 59.99, "A fantasy game", "https://media.com", "https://image.com"));

        String response = getRequest(PUBLIC_API_PATH + "?developer=CD Projekt Red", status().isOk(), mockMvc);

        List<GamePublicDto> games = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(2, games.size());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get all games with multiple search criteria and expect 200")
    void testGetAllGamesWithMultipleCriteria() throws Exception {
        Long developer2 = createDeveloper("Teste", List.of("https://twitter.com/teste"));
        Long platform3 = createPlatform("XBOX");
        Long genre3 = createGenre("Adventure");
        createGame(new GameCreateDto("Game 1", new Date(), developer, genres, List.of(platform3), 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("Game 2", new Date(), developer, List.of(genre3), platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("Game 3", new Date(), developer2, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createGame(new GameCreateDto("Different", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        GamePublicDto game = createGame(new GameCreateDto("Game 4", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));

        String response = getRequest(PUBLIC_API_PATH + "?title=Game&developer=CD Projekt Red&platform=PC&genre=Action", status().isOk(), mockMvc);

        List<GamePublicDto> games = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, GamePublicDto.class));

        assertEquals(1, games.size());
        assertEquals(game.id(), games.get(0).id());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get game by id and expect 200")
    void testGetGameById() throws Exception {
        GamePublicDto game = createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));

        String response = getRequest(PUBLIC_API_PATH + "/" + game.id(), status().isOk(), mockMvc);

        GameLikePublicDto gameResponse = mapper.readValue(response, GameLikePublicDto.class);

        assertEquals(game.id(), gameResponse.game().id());
        assertEquals(game.title(), gameResponse.game().title());
        assertEquals(game.description(), gameResponse.game().description());
        assertEquals(game.releaseDate(), gameResponse.game().releaseDate());
        assertEquals(game.price(), gameResponse.game().price());
        assertEquals(game.mediaUrl(), gameResponse.game().mediaUrl());
        assertEquals(game.imageUrl(), gameResponse.game().imageUrl());
        assertEquals(game.developer().id(), gameResponse.game().developer().id());
        assertEquals(game.platforms().size(), gameResponse.game().platforms().size());
        assertEquals(game.genres().size(), gameResponse.game().genres().size());
        assertFalse(gameResponse.liked());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get game by id not found and expect 404")
    void testGetGameByIdNotFound() throws Exception {
        String response = getRequest(PUBLIC_API_PATH + "/0", status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(GAME_NOT_FOUND + "0", errorDto.message());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get game by id with liked and expect 200 with liked true")
    void testGetGameByIdWithLiked() throws Exception {
        GamePublicDto game = createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createUser(USER);
        createLike(game.id(), USER);

        String response = getRequest(PUBLIC_API_PATH + "/" + game.id(), status().isOk(), mockMvc);

        GameLikePublicDto gameResponse = mapper.readValue(response, GameLikePublicDto.class);

        assertEquals(game.id(), gameResponse.game().id());
        assertEquals(game.title(), gameResponse.game().title());
        assertEquals(game.description(), gameResponse.game().description());
        assertEquals(game.releaseDate(), gameResponse.game().releaseDate());
        assertEquals(game.price(), gameResponse.game().price());
        assertEquals(game.mediaUrl(), gameResponse.game().mediaUrl());
        assertEquals(game.imageUrl(), gameResponse.game().imageUrl());
        assertEquals(game.developer().id(), gameResponse.game().developer().id());
        assertEquals(game.platforms().size(), gameResponse.game().platforms().size());
        assertEquals(game.genres().size(), gameResponse.game().genres().size());
        assertTrue(gameResponse.liked());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test get by id  with liked from another user and expect 200 with liked false")
    void testGetGameByIdWithLikedFromAnotherUser() throws Exception {
        GamePublicDto game = createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));
        createUser("anotherUser");
        createLike(game.id(), "anotherUser");

        String response = getRequest(PUBLIC_API_PATH + "/" + game.id(), status().isOk(), mockMvc);

        GameLikePublicDto gameResponse = mapper.readValue(response, GameLikePublicDto.class);

        assertEquals(game.id(), gameResponse.game().id());
        assertEquals(game.title(), gameResponse.game().title());
        assertEquals(game.description(), gameResponse.game().description());
        assertEquals(game.releaseDate(), gameResponse.game().releaseDate());
        assertEquals(game.price(), gameResponse.game().price());
        assertEquals(game.mediaUrl(), gameResponse.game().mediaUrl());
        assertEquals(game.imageUrl(), gameResponse.game().imageUrl());
        assertEquals(game.developer().id(), gameResponse.game().developer().id());
        assertEquals(game.platforms().size(), gameResponse.game().platforms().size());
        assertEquals(game.genres().size(), gameResponse.game().genres().size());
        assertFalse(gameResponse.liked());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game and expect 200")
    void testUpdateGame() throws Exception {
        Long newDeveloperId = createDeveloper("Teste", List.of("https://twitter.com/teste"));
        Long newPlatformId = createPlatform("XBOX");
        Long newGenreId = createGenre("Adventure");
        GamePublicDto game = createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));

        GameCreateDto gameUpdate = new GameCreateDto(
                "Updated",
                new Date(),
                newDeveloperId,
                List.of(newGenreId),
                List.of(newPlatformId),
                49.99,
                "Updated description",
                "https://updated.com",
                "https://updated.com"
        );

        Long developerCount = developerRepository.count();
        Long platformCount = platformRepository.count();
        Long genreCount = gameGenreRepository.count();

        String response = putRequest(PRIVATE_API_PATH + "/" + game.id(), status().isOk(), mapper.writeValueAsString(gameUpdate), mockMvc);

        GamePublicDto gameResponse = mapper.readValue(response, GamePublicDto.class);
        Game gameEntity = gameRepository.findById(game.id()).orElse(null);
        assertEquals(1, gameRepository.count());
        assertNotNull(gameEntity);

        assertGame(gameResponse, gameEntity, "Updated", "Updated description", gameUpdate.releaseDate(), 49.99, "https://updated.com", "https://updated.com", newDeveloperId, List.of(newPlatformId), List.of(newGenreId), USER, MediaType.GAME);

        assertEquals(developerCount, developerRepository.count());
        assertEquals(platformCount, platformRepository.count());
        assertEquals(genreCount, gameGenreRepository.count());
    }

    @Test
    @DisplayName("Test update game unauthenticated and expect 401")
    void testUpdateGameUnauthenticated() throws Exception {
        putRequest(PRIVATE_API_PATH + "/0", status().isUnauthorized(), "", mockMvc);

        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game create by another user and expect 403")
    void testUpdateGameByAnotherUser() throws Exception {
        Game game = Game.builder()
                .title(title)
                .mediaCreatorId("anotherUser")
                .mediaType(MediaType.GAME)
                .description(description)
                .releaseDate(new Date())
                .price(price)
                .mediaUrl(mediaUrl)
                .averageRating(0.0)
                .build();
        gameRepository.saveAndFlush(game);

        GameCreateDto gameUpdate = new GameCreateDto(
                "Updated",
                new Date(),
                developer,
                genres,
                platforms,
                49.99,
                "Updated description",
                "https://updated.com",
                "https://updated.com"
        );

        String response = putRequest(PRIVATE_API_PATH + "/" + game.getId(), status().isForbidden(), mapper.writeValueAsString(gameUpdate), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(1, gameRepository.count());
        assertEquals(UNAUTHORIZED_UPDATE, errorDto.message());

        assertEquals(1, gameRepository.count());
        Game gameEntity = gameRepository.findById(game.getId()).orElse(null);
        assertNotNull(gameEntity);

        assertNotEquals(gameUpdate.title(), gameEntity.getTitle());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test update game not found and expect 404")
    void testUpdateGameNotFound() throws Exception {
        GameCreateDto gameUpdate = new GameCreateDto(
                "Updated",
                new Date(),
                developer,
                genres,
                platforms,
                49.99,
                "Updated description",
                "https://updated.com",
                "https://updated.com"
        );

        String response = putRequest(PRIVATE_API_PATH + "/0", status().isNotFound(), mapper.writeValueAsString(gameUpdate), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, gameRepository.count());
        assertEquals(GAME_NOT_FOUND + "0", errorDto.message());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test delete game and expect 204")
    void testDeleteGame() throws Exception {
        GamePublicDto game = createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));

        deleteRequest(PRIVATE_API_PATH + "/" + game.id(), status().isNoContent(), mockMvc);

        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test delete game create by another user and expect 403")
    void testDeleteGameByAnotherUser() throws Exception {
        Game game = Game.builder()
                .title(title)
                .mediaCreatorId("anotherUser")
                .mediaType(MediaType.GAME)
                .description(description)
                .releaseDate(new Date())
                .price(price)
                .mediaUrl(mediaUrl)
                .averageRating(0.0)
                .build();
        gameRepository.saveAndFlush(game);

        deleteRequest(PRIVATE_API_PATH + "/" + game.getId(), status().isForbidden(), mockMvc);

        assertEquals(1, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER)
    @DisplayName("Test delete game not found and expect 404")
    void testDeleteGameNotFound() throws Exception {
        String response = deleteRequest(PRIVATE_API_PATH + "/0", status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(GAME_NOT_FOUND + "0", errorDto.message());

        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER, authorities = "ADMIN")
    @DisplayName("Test delete game as admin and expect 204")
    void testDeleteGameAsAdmin() throws Exception {
        GamePublicDto game = createGame(new GameCreateDto("Cyberpunk 2077", new Date(), developer, genres, platforms, 59.99, "A futuristic game", "https://media.com", "https://image.com"));

        deleteRequest(PRIVATE_API_PATH + "/" + game.id(), status().isNoContent(), mockMvc);

        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER, authorities = "ADMIN")
    @DisplayName("Test delete game not found as admin and expect 404")
    void testDeleteGameNotFoundAsAdmin() throws Exception {
        String response = deleteRequest(PRIVATE_API_PATH + "/0", status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(GAME_NOT_FOUND + "0", errorDto.message());

        assertEquals(0, gameRepository.count());
    }

    @Test
    @WithMockUser(username = USER, authorities = "ADMIN")
    @DisplayName("Test delete game create by another user as admin and expect 204")
    void testDeleteGameByAnotherUserAsAdmin() throws Exception {
        Game game = Game.builder()
                .title(title)
                .mediaCreatorId("anotherUser")
                .mediaType(MediaType.GAME)
                .description(description)
                .releaseDate(new Date())
                .price(price)
                .mediaUrl(mediaUrl)
                .averageRating(0.0)
                .build();
        gameRepository.saveAndFlush(game);

        deleteRequest(PRIVATE_API_PATH + "/" + game.getId(), status().isNoContent(), mockMvc);

        assertEquals(0, gameRepository.count());
    }

    @Test
    @DisplayName("Test admin delete unauthenticated and expect 401")
    void testAdminDeleteUnauthenticated() throws Exception {
        deleteRequest(PRIVATE_API_PATH + "/0", status().isUnauthorized(), mockMvc);
    }

}
