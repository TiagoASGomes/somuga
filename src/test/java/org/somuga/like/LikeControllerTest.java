package org.somuga.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.Error;
import org.somuga.converter.LikeConverter;
import org.somuga.dto.like.LikeCreateDto;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.entity.*;
import org.somuga.repository.*;
import org.somuga.testDtos.LikeGameDto;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
public class LikeControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER_ID = "google-auth2|1234567890";
    private final String PRIVATE_API_PATH = "/api/v1/like/private";
    private final String PUBLIC_API_PATH = "/api/v1/like/public";
    private final String developerName = "Developer";
    private final String genreName = "genre";
    private final String platformName = "platforms";
    MockMvc mockMvc;
    private Game game;
    private User user;
    private Developer developer;
    private Set<GameGenre> gameGenres = new HashSet<>();
    private Set<Platform> platforms = new HashSet();
    @Autowired
    private UserRepository userTestRepository;
    @Autowired
    private LikeRepository likeTestRepository;
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
        likeTestRepository.deleteAll();
        userTestRepository.deleteAll();
        gameRepository.deleteAll();
        platformRepository.deleteAll();
        gameGenreRepository.deleteAll();
        developerRepository.deleteAll();

    }

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
        developer = developerRepository.save(new Developer(developerName, List.of(), USER_ID));
        gameGenres.add(gameGenreRepository.save(new GameGenre(genreName)));
        platforms.add(platformRepository.save(new Platform(platformName)));
        user = createUser(USER_ID, "UserName", "email@example.com");
        game = createGame();
    }

    public Game createGame() {
        Game game = new Game();
        game.setTitle("Game");
        game.setReleaseDate(new Date());
        game.setDeveloper(developer);
        game.setGenres(gameGenres);
        game.setPlatforms(platforms);
        game.setMediaType(org.somuga.enums.MediaType.GAME);
        return gameRepository.save(game);
    }

    public User createUser(String id, String userName, String email) {
        User user = new User(id, userName, email);
        return userTestRepository.save(user);
    }

    public LikePublicDto createLike(User user, Media media) {
        Like like = new Like(user, media);
        return LikeConverter.fromEntityToPublicDto(likeTestRepository.save(like));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game like and expect status 201 and like")
    void testCreateGameLike() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(game.getId());

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        LikeGameDto like = mapper.readValue(response, LikeGameDto.class);

        assertNotNull(like.id());
        assertEquals(USER_ID, like.user().id());
        assertEquals(likeDto.mediaId(), like.media().id());
        assertEquals(user.getUserName(), like.user().userName());
        assertEquals(game.getTitle(), like.media().title());
    }


    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create like with incorrect id and expect status 404 and message")
    void testCreateLikeIncorrectId() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(999999999L);


        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(MEDIA_NOT_FOUND + 999999999L, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(PRIVATE_API_PATH, error.getPath());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create duplicate like and expect status 400 and message")
    void testCreateLikeDuplicate() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(game.getId());
        createLike(user, game);

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(ALREADY_LIKED, error.getMessage());
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(PRIVATE_API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test create like unauthorized and expect status 401 and message")
    void testCreateLikeUnauthorized() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(game.getId());

        mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test get all likes from user and expect status 200 and list with likes")
    void testGetAllLikesFromUser() throws Exception {
        User user = createUser(USER_ID + 1, "UserName2", "email2@example.com");
        for (int i = 0; i < 3; i++) {
            createLike(user, createGame());
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("Test get all likes from user paged and expect status 200 and list with likes and pages")
    void testGetAllLikesFromUserPaged() throws Exception {
        User user = createUser(USER_ID + 1, "UserName2", "email2@example.com");
        for (int i = 0; i < 5; i++) {
            createLike(user, createGame());
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/user/" + user.getId() + "?page=0&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
        mockMvc.perform(get(PUBLIC_API_PATH + "/user/" + user.getId() + "?page=1&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Test get all likes from user with no likes and expect status 200 and empty list")
    void testGetAllLikesFromUserWithNoUserLikes() throws Exception {
        User user = createUser(USER_ID + 1, "UserName2", "email2@example.com");
        for (int i = 0; i < 3; i++) {
            createLike(user, createGame());
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/user/" + 99999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }


    @Test
    @DisplayName("Test get all likes from media and expect status 200 and list with likes")
    void testGetAllLikesFromMedia() throws Exception {
        Media media = createGame();
        for (int i = 0; i < 6; i++) {
            createLike(createUser(USER_ID + i, "Name" + i, "email" + i + "@example.com"), media);
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/media/" + media.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    @DisplayName("Test get all likes from media paged and expect status 200 and list with likes and pages")
    void testGetAllLikesFromMediaPaged() throws Exception {
        Media media = createGame();
        for (int i = 0; i < 6; i++) {
            createLike(createUser(USER_ID + i, "Name" + i, "email" + i + "@example.com"), media);
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/media/" + media.getId() + "?page=0&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
        mockMvc.perform(get(PUBLIC_API_PATH + "/media/" + media.getId() + "?page=1&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

    @Test
    @DisplayName("Test get all likes from media with no likes and expect status 200 and empty list")
    void testGetAllLikesFromMediaWithNoMediaLikes() throws Exception {
        Media media = createGame();
        for (int i = 0; i < 6; i++) {
            createLike(createUser(USER_ID + i, "Name" + i, "email" + i + "@example.com"), media);
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/media/" + 999999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete like and expect status 200 and deleted like")
    void testDeleteLike() throws Exception {
        LikePublicDto like = createLike(user, game);

        mockMvc.perform(delete(PRIVATE_API_PATH + "/" + like.id())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(PUBLIC_API_PATH + "/user/" + user.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Test delete unauthorized like and expect status 401 and message")
    void testDeleteLikeUnauthorized() throws Exception {
        LikePublicDto like = createLike(user, game);

        mockMvc.perform(delete(PRIVATE_API_PATH + "/" + like.id())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete like with no like and expect status 404 and message")
    void testDeleteLikeNotFound() throws Exception {

        String response = mockMvc.perform(delete(PRIVATE_API_PATH + "/" + 9999999)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(LIKE_NOT_FOUND + 9999999, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals("DELETE", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(PRIVATE_API_PATH + "/" + 9999999, error.getPath());
    }

}
