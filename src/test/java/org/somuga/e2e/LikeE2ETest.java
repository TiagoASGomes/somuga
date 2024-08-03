package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.Error;
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
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

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
        user = createUser(USER_ID, "UserName");
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
                .price(0.0)
                .build();
        return gameRepository.save(game);
    }

    public User createUser(String id, String userName) {
        User user = User.builder()
                .id(id)
                .userName(userName)
                .joinDate(new Date())
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

        assertEquals(MEDIA_NOT_FOUND + 999999999L, error.message());
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

        assertEquals(ALREADY_LIKED, error.message());
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
        User user = createUser(USER_ID + 1, "UserName2");
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
        User user = createUser(USER_ID + 1, "UserName2");
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
        User user = createUser(USER_ID + 1, "UserName2");
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
            createLike(createUser(USER_ID + i, "Name" + i), media);
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
            createLike(createUser(USER_ID + i, "Name" + i), media);
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
            createLike(createUser(USER_ID + i, "Name" + i), media);
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

        assertEquals(LIKE_NOT_FOUND + 9999999, error.message());
    }

}