package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.ErrorDto;
import org.somuga.converter.LikeConverter;
import org.somuga.dto.like.LikeCreateDto;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.entity.Game;
import org.somuga.entity.Like;
import org.somuga.entity.Media;
import org.somuga.entity.User;
import org.somuga.repository.GameRepository;
import org.somuga.repository.LikeRepository;
import org.somuga.repository.UserRepository;
import org.somuga.testUtils.LikeGameDto;
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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.somuga.testUtils.Utils.deleteRequest;
import static org.somuga.testUtils.Utils.postRequest;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
public class LikeE2ETest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER_ID = "google-auth2|1234567890";
    private final String PRIVATE_API_PATH = "/api/v1/like/private";
    private final String PUBLIC_API_PATH = "/api/v1/like/public";
    MockMvc mockMvc;
    private Game game;
    private User user;
    @Autowired
    private UserRepository userTestRepository;
    @Autowired
    private LikeRepository likeTestRepository;
    @Autowired
    private GameRepository gameRepository;
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

    }

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(controller)
                .apply(springSecurity())
                .build();
        user = createUser(USER_ID, "UserName", "email@example.com");
        game = createGame();
    }

    public Game createGame() {
        Game game = Game.builder()
                .title("Game")
                .description("Description")
                .releaseDate(new Date())
                .mediaUrl("https://example.com")
                .mediaType(org.somuga.enums.MediaType.GAME)
                .mediaCreatorId(USER_ID)
                .imageUrl("https://example.com")
                .averageRating(0.0)
                .build();
        return gameRepository.save(game);
    }

    public User createUser(String id, String userName, String email) {
        User user = User.builder()
                .id(id)
                .userName(userName)
                .joinDate(new Date())
                .email(email)
                .active(true)
                .build();
        return userTestRepository.save(user);
    }

    public LikePublicDto createLike(User user, Media media) {
        Like like = Like.builder()
                .user(user)
                .media(media)
                .build();
        return LikeConverter.fromEntityToPublicDto(likeTestRepository.save(like));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game like and expect status 201 and like")
    void testCreateGameLike() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(game.getId());

        String response = postRequest(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(likeDto), mockMvc);

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


        String response = postRequest(PRIVATE_API_PATH, status().isNotFound(), mapper.writeValueAsString(likeDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(MEDIA_NOT_FOUND + 999999999L, errorDto.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create duplicate like and expect status 400 and message")
    void testCreateLikeDuplicate() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(game.getId());
        createLike(user, game);

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(likeDto), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(ALREADY_LIKED, errorDto.message());
    }

    @Test
    @DisplayName("Test create like unauthorized and expect status 401 and message")
    void testCreateLikeUnauthorized() throws Exception {
        LikeCreateDto likeDto = new LikeCreateDto(game.getId());

        postRequest(PRIVATE_API_PATH, status().isUnauthorized(), mapper.writeValueAsString(likeDto), mockMvc);
    }

    @Test
    @DisplayName("Test get all likes from user and expect status 200 and list with likes")
    void testGetAllLikesFromUser() throws Exception {
        User user = createUser(USER_ID + 1, "UserName2", "email2@example.com");
        for (int i = 0; i < 3; i++) {
            createLike(user, createGame());
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "?userId=" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(3)))
                .andExpect(jsonPath("$.count", equalTo(3)));
    }

    @Test
    @DisplayName("Test get all likes from user paged and expect status 200 and list with likes and pages")
    void testGetAllLikesFromUserPaged() throws Exception {
        User user = createUser(USER_ID + 1, "UserName2", "email2@example.com");
        for (int i = 0; i < 5; i++) {
            createLike(user, createGame());
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=4&userId=" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(4)))
                .andExpect(jsonPath("$.count", equalTo(5)));
        mockMvc.perform(get(PUBLIC_API_PATH + "?page=1&size=4&userId=" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(1)))
                .andExpect(jsonPath("$.count", equalTo(5)));
    }

    @Test
    @DisplayName("Test get all likes from user with no likes and expect status 200 and empty list")
    void testGetAllLikesFromUserWithNoUserLikes() throws Exception {
        User user = createUser(USER_ID + 1, "UserName2", "email2@example.com");
        for (int i = 0; i < 3; i++) {
            createLike(user, createGame());
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "?userId=" + 999999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(0)))
                .andExpect(jsonPath("$.count", equalTo(0)));

    }


    @Test
    @DisplayName("Test get all likes from media and expect status 200 and list with likes")
    void testGetAllLikesFromMedia() throws Exception {
        Media media = createGame();
        for (int i = 0; i < 6; i++) {
            createLike(createUser(USER_ID + i, "Name" + i, "email" + i + "@example.com"), media);
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "?mediaId=" + media.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(6)))
                .andExpect(jsonPath("$.count", equalTo(6)));
    }

    @Test
    @DisplayName("Test get all likes from media paged and expect status 200 and list with likes and pages")
    void testGetAllLikesFromMediaPaged() throws Exception {
        Media media = createGame();
        for (int i = 0; i < 6; i++) {
            createLike(createUser(USER_ID + i, "Name" + i, "email" + i + "@example.com"), media);
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "?page=0&size=4&mediaId=" + media.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(4)))
                .andExpect(jsonPath("$.count", equalTo(6)));
        mockMvc.perform(get(PUBLIC_API_PATH + "?page=1&size=4&mediaId=" + media.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(2)))
                .andExpect(jsonPath("$.count", equalTo(6)));

    }

    @Test
    @DisplayName("Test get all likes from media with no likes and expect status 200 and empty list")
    void testGetAllLikesFromMediaWithNoMediaLikes() throws Exception {
        Media media = createGame();
        for (int i = 0; i < 6; i++) {
            createLike(createUser(USER_ID + i, "Name" + i, "email" + i + "@example.com"), media);
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "?mediaId=" + 999999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes", hasSize(0)))
                .andExpect(jsonPath("$.count", equalTo(0)));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete like and expect status 200 and deleted like")
    void testDeleteLike() throws Exception {
        LikePublicDto like = createLike(user, game);

        deleteRequest(PRIVATE_API_PATH + "/" + like.id(), status().isNoContent(), mockMvc);

        assertEquals(0, likeTestRepository.count());
    }

    @Test
    @DisplayName("Test delete unauthorized like and expect status 401 and message")
    void testDeleteLikeUnauthorized() throws Exception {
        LikePublicDto like = createLike(user, game);

        deleteRequest(PRIVATE_API_PATH + "/" + like.id(), status().isUnauthorized(), mockMvc);
    }


    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete like with no like and expect status 404 and message")
    void testDeleteLikeNotFound() throws Exception {

        String response = deleteRequest(PRIVATE_API_PATH + "/" + 9999999, status().isNotFound(), mockMvc);

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(LIKE_NOT_FOUND + 9999999, errorDto.message());
    }

}
