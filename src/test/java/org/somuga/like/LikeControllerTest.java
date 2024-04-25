package org.somuga.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.Error;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.dto.like.LikeCreateDto;
import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.Developer;
import org.somuga.entity.GameGenre;
import org.somuga.entity.Platform;
import org.somuga.repository.*;
import org.somuga.testDtos.LikeGameDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.message.Messages.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LikeControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_PATH = "/api/v1/like";
    private final String developerName = "Developer";
    private final String genreName = "genre";
    private final String platformName = "platforms";
    private GamePublicDto game1;
    private UserPublicDto user;
    @Autowired
    private MockMvc mockMvc;
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
        developerRepository.save(new Developer(developerName.toLowerCase()));
        gameGenreRepository.save(new GameGenre(genreName.toLowerCase()));
        platformRepository.save(new Platform(platformName.toLowerCase()));
        user = createUser("UserName", "email@example.com");
        game1 = createGame();
    }

    public GamePublicDto createGame() throws Exception {

        GameCreateDto gameDto = new GameCreateDto("Title", new Date(), developerName, List.of(genreName), List.of(platformName), 10.0, "Description");

        String response = mockMvc.perform(post("/api/v1/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(gameDto)))
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, GamePublicDto.class);
    }

    public UserPublicDto createUser(String userName, String email) throws Exception {
        UserCreateDto userDto = new UserCreateDto(userName, email);

        String response = mockMvc.perform(post("/api/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(response, UserPublicDto.class);
    }

    public void createLike(Long userId, Long mediaId) throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(userId, mediaId);
        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Test create game like and expect status 201 and like")
    void testCreateGameLike() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(user.id(), game1.id());

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        LikeGameDto like = mapper.readValue(response, LikeGameDto.class);

        assertNotNull(like.id());
        assertEquals(likeDto.userId(), like.user().id());
        assertEquals(likeDto.mediaId(), like.media().id());
        assertEquals(user.userName(), like.user().userName());
        assertEquals(game1.title(), like.media().title());
    }


    @Test
    @DisplayName("Test create like with incorrect id and expect status 404 and message")
    void testCreateLikeIncorrectId() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(user.id(), 999999999L);


        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(MEDIA_NOT_FOUND + 999999999L, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test create like with incorrect validation and expect status 400 and message")
    void testCreateLikeValidation() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(user.id() * -1, game1.id() * -1);


        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(ID_GREATER_THAN_0));
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test create duplicate like and expect status 400 and message")
    void testCreateLikeDuplicate() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(user.id(), game1.id());


        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isCreated());

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(ALREADY_LIKED, error.getMessage());
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test get all likes from user and expect status 200 and list with likes")
    void testGetAllLikesFromUser() throws Exception {
        long userId = createUser("UserName2", "email2@example.com").id();
        for (int i = 0; i < 3; i++) {
            createLike(userId, createGame().id());
        }

        mockMvc.perform(get(API_PATH + "/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("Test get all likes from user paged and expect status 200 and list with likes and pages")
    void testGetAllLikesFromUserPaged() throws Exception {
        long userId = createUser("UserName2", "email2@example.com").id();
        for (int i = 0; i < 5; i++) {
            createLike(userId, createGame().id());
        }

        mockMvc.perform(get(API_PATH + "/user/" + userId + "?page=0&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
        mockMvc.perform(get(API_PATH + "/user/" + userId + "?page=1&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Test get all likes from user with no likes and expect status 200 and empty list")
    void testGetAllLikesFromUserWithNoUserLikes() throws Exception {
        long userId = createUser("UserName2", "email2@example.com").id();
        for (int i = 0; i < 3; i++) {
            createLike(userId, createGame().id());
        }

        mockMvc.perform(get(API_PATH + "/user/" + 99999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }


    @Test
    @DisplayName("Test get all likes from media and expect status 200 and list with likes")
    void testGetAllLikesFromMedia() throws Exception {
        long mediaId = createGame().id();
        for (int i = 0; i < 6; i++) {
            createLike(createUser("Name" + i, "email" + i + "@example.com").id(), mediaId);
        }

        mockMvc.perform(get(API_PATH + "/media/" + mediaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    @DisplayName("Test get all likes from media paged and expect status 200 and list with likes and pages")
    void testGetAllLikesFromMediaPaged() throws Exception {
        long mediaId = createGame().id();
        for (int i = 0; i < 6; i++) {
            createLike(createUser("Name" + i, "email" + i + "@example.com").id(), mediaId);
        }

        mockMvc.perform(get(API_PATH + "/media/" + mediaId + "?page=0&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
        mockMvc.perform(get(API_PATH + "/media/" + mediaId + "?page=1&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

    @Test
    @DisplayName("Test get all likes from media with no likes and expect status 200 and empty list")
    void testGetAllLikesFromMediaWithNoMediaLikes() throws Exception {
        long mediaId = createGame().id();
        for (int i = 0; i < 6; i++) {
            createLike(createUser("Name" + i, "email" + i + "@example.com").id(), mediaId);
        }

        mockMvc.perform(get(API_PATH + "/media/" + 999999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Test delete like and expect status 200 and deleted like")
    void testDeleteLike() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(user.id(), game1.id());

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(likeDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long likeId = mapper.readValue(response, LikeGameDto.class).id();

        mockMvc.perform(delete(API_PATH + "/" + likeId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(API_PATH + "/user/" + user.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    @DisplayName("Test delete like with no like and expect status 404 and message")
    void testDeleteLikeNotFound() throws Exception {

        String response = mockMvc.perform(delete(API_PATH + "/" + 9999999))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(LIKE_NOT_FOUND + 9999999, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals("DELETE", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(API_PATH + "/" + 9999999, error.getPath());
    }

}
