package org.somuga.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.Error;
import org.somuga.converter.ReviewConverter;
import org.somuga.dto.review.ReviewCreateDto;
import org.somuga.dto.review.ReviewPublicDto;
import org.somuga.dto.review.ReviewUpdateDto;
import org.somuga.entity.*;
import org.somuga.repository.*;
import org.somuga.testDtos.ReviewGameDto;
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
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
public class ReviewControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER_ID = "google-auth2|1234567890";
    private final String PRIVATE_API_PATH = "/api/v1/review/private";
    private final String PUBLIC_API_PATH = "/api/v1/review/public";
    private final String developerName = "Developer";
    private final String genreName = "genre";
    private final String platformName = "platforms";
    MockMvc mockMvc;
    private Developer developer;
    private Set<GameGenre> gameGenres = new HashSet<>();
    private Set<Platform> platforms = new HashSet();
    private Game game;
    private User user;
    @Autowired
    private UserRepository userTestRepository;
    @Autowired
    private ReviewRepository reviewTestRepository;
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
        reviewTestRepository.deleteAll();
        userTestRepository.deleteAll();
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
        user = createUser(USER_ID, "UserName", "email@example.com");
        game = createGame();
    }

    public Game createGame() {
        Game game = new Game();
        game.setTitle("Game");
        game.setReleaseDate(new Date());
        game.setDescription("Description");
        game.setMediaCreatorId(USER_ID);
        game.setPrice(0.0);
        game.setMediaUrl("mediaUrl");
        game.setImageUrl("imageUrl");
        game.setMediaType(org.somuga.enums.MediaType.GAME);
        return gameRepository.save(game);
    }

    public User createUser(String id, String userName, String email) {
        User user = new User(id, userName);
        return userTestRepository.save(user);
    }

    public ReviewPublicDto createReview(User user, Media media, int score, String review) {
        Review reviewEntity = new Review(score, review, user, media);
        return ReviewConverter.fromEntityToPublicDto(reviewTestRepository.save(reviewEntity));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review and expect status 201 and like")
    void testCreateGameReview() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), 5, "My Review");

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reviewDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ReviewGameDto review = mapper.readValue(response, ReviewGameDto.class);

        assertNotNull(review.id());
        assertEquals(USER_ID, review.user().id());
        assertEquals(reviewDto.mediaId(), review.media().id());
        assertEquals(user.getUserName(), review.user().userName());
        assertEquals(game.getTitle(), review.media().title());
        assertEquals(5, review.reviewScore());
        assertEquals("My Review", review.writtenReview());
    }


    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create review with incorrect id and expect status 404 and message")
    void testCreateReviewIncorrectId() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(99999999L, 5, "My Review");


        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reviewDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(MEDIA_NOT_FOUND + 99999999L, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(PRIVATE_API_PATH, error.getPath());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create review with incorrect validation and expect status 400 and message")
    void testCreateLikeValidation() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId() * -1, (int) (game.getId() * 11), "A".repeat(1100));

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reviewDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertTrue(error.getMessage().contains(ID_GREATER_THAN_0));
        assertTrue(error.getMessage().contains(MAX_REVIEW_CHARACTERS));
        assertTrue(error.getMessage().contains(INVALID_SCORE));
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(PRIVATE_API_PATH, error.getPath());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create duplicate review and expect status 400 and message")
    void testCreateReviewDuplicate() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), 5, "My Review");


        mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reviewDto)))
                .andExpect(status().isCreated());

        String response = mockMvc.perform(post(PRIVATE_API_PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reviewDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(ALREADY_REVIEWED, error.getMessage());
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(PRIVATE_API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test get review by id and expect status 200 and review")
    void testGetReviewById() throws Exception {
        ReviewPublicDto reviewDto = createReview(user, game, 5, "My Review");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + reviewDto.id()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ReviewGameDto review = mapper.readValue(response, ReviewGameDto.class);

        assertEquals(reviewDto.id(), review.id());
        assertEquals(user.getId(), review.user().id());
        assertEquals(game.getId(), review.media().id());
        assertEquals(5, review.reviewScore());
        assertEquals("My Review", review.writtenReview());
    }

    @Test
    @DisplayName("Test get review by id with no review and expect status 404 and message")
    void testGetReviewByIdNotFound() throws Exception {
        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + 9999999))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(REVIEW_NOT_FOUND + 9999999, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals("GET", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(PUBLIC_API_PATH + "/" + 9999999, error.getPath());
    }

    @Test
    @DisplayName("Test get all reviews from user and expect status 200 and list with reviews")
    void testGetAllReviewsFromUser() throws Exception {
        User user = createUser(USER_ID, "UserName2", "email2@example.com");
        for (int i = 0; i < 3; i++) {
            createReview(user, createGame(), 5, "My Review");
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("Test get all reviews from user paged and expect status 200 and list with reviews and pages")
    void testGetAllReviewsFromUserPaged() throws Exception {
        User user = createUser(USER_ID, "UserName2", "email2@example.com");
        for (int i = 0; i < 3; i++) {
            createReview(user, createGame(), 5, "My Review");
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/user/" + user.getId() + "?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get(PUBLIC_API_PATH + "/user/" + user.getId() + "?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Test get all reviews from user with no reviews and expect status 200 and empty list")
    void testGetAllReviewsFromUserWithNoUserReviews() throws Exception {
        User user = createUser(USER_ID, "UserName2", "email2@example.com");
        for (int i = 0; i < 3; i++) {
            createReview(user, createGame(), 5, "My Review");
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/user/" + 99999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }


    @Test
    @DisplayName("Test get all reviews from media and expect status 200 and list with reviews")
    void testGetAllReviewsFromMedia() throws Exception {
        Game game = createGame();
        for (int i = 0; i < 6; i++) {
            createReview(createUser(USER_ID, "Name" + i, "email" + i + "@example.com"), game, 5, "My Review");
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/media/" + game.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    @DisplayName("Test get all reviews from media paged and expect status 200 and list with reviews and pages")
    void testGetAllReviewsFromMediaPaged() throws Exception {
        Game game = createGame();
        for (int i = 0; i < 6; i++) {
            createReview(createUser(USER_ID, "Name" + i, "email" + i + "@example.com"), game, 5, "My Review");
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/media/" + game.getId() + "?page=0&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
        mockMvc.perform(get(PUBLIC_API_PATH + "/media/" + game.getId() + "?page=1&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

    @Test
    @DisplayName("Test get all reviews from media with no reviews and expect status 200 and empty list")
    void testGetAllReviewsFromMediaWithNoMediaReviews() throws Exception {
        Game game = createGame();
        for (int i = 0; i < 6; i++) {
            createReview(createUser(USER_ID, "Name" + i, "email" + i + "@example.com"), game, 5, "My Review");
        }

        mockMvc.perform(get(PUBLIC_API_PATH + "/media/" + 999999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review and expect status 200 and updated review")
    void testUpdateReview() throws Exception {
        ReviewPublicDto reviewDto = createReview(user, game, 5, "My Review");
        ReviewUpdateDto updateDto = new ReviewUpdateDto(6, "New Review");

        String response = mockMvc.perform(patch(PRIVATE_API_PATH + "/" + reviewDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ReviewGameDto review = mapper.readValue(response, ReviewGameDto.class);

        assertEquals(reviewDto.id(), review.id());
        assertEquals(6, review.reviewScore());
        assertEquals("New Review", review.writtenReview());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review with no review and expect status 404 and message")
    void testUpdateReviewNotFound() throws Exception {
        ReviewUpdateDto updateDto = new ReviewUpdateDto(6, "New Review");

        String response = mockMvc.perform(patch(PRIVATE_API_PATH + "/" + 9999999)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(PRIVATE_API_PATH + "/" + 9999999, error.getPath());
        assertEquals("PATCH", error.getMethod());
        assertEquals(REVIEW_NOT_FOUND + 9999999, error.getMessage());
        assertEquals(404, error.getStatus());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review with validation errors and expect status 400 and message")
    void testUpdateReviewValidation() throws Exception {
        ReviewPublicDto reviewDto = createReview(user, game, 5, "My Review");
        ReviewUpdateDto updateDto = new ReviewUpdateDto((int) (game.getId() * 20), "A".repeat(1100));

        String response = mockMvc.perform(patch(PRIVATE_API_PATH + "/" + reviewDto.id())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(PRIVATE_API_PATH + "/" + reviewDto.id(), error.getPath());
        assertEquals("PATCH", error.getMethod());
        assertTrue(error.getMessage().contains(INVALID_SCORE));
        assertTrue(error.getMessage().contains(MAX_REVIEW_CHARACTERS));
        assertEquals(400, error.getStatus());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete review and expect status 200 and deleted review")
    void testDeleteReview() throws Exception {
        ReviewPublicDto reviewDto = createReview(user, game, 5, "My Review");

        mockMvc.perform(delete(PRIVATE_API_PATH + "/" + reviewDto.id())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertEquals(0, reviewTestRepository.count());

    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete review with no review and expect status 404 and message")
    void testDeleteReviewNotFound() throws Exception {

        String response = mockMvc.perform(delete(PRIVATE_API_PATH + "/" + 9999999)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(REVIEW_NOT_FOUND + 9999999, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals("DELETE", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(PRIVATE_API_PATH + "/" + 9999999, error.getPath());
    }
}
