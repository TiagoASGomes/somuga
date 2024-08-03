package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.ErrorDto;
import org.somuga.converter.ReviewConverter;
import org.somuga.dto.review.ReviewCreateDto;
import org.somuga.dto.review.ReviewPublicDto;
import org.somuga.dto.review.ReviewUpdateDto;
import org.somuga.entity.Game;
import org.somuga.entity.Media;
import org.somuga.entity.Review;
import org.somuga.entity.User;
import org.somuga.repository.GameRepository;
import org.somuga.repository.ReviewRepository;
import org.somuga.repository.UserRepository;
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
public class ReviewE2ETest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String USER_ID = "google-auth2|1234567890";
    private final String PRIVATE_API_PATH = "/api/v1/review/private";
    private final String PUBLIC_API_PATH = "/api/v1/review/public";
    MockMvc mockMvc;
    private Game game;
    private User user;
    @Autowired
    private UserRepository userTestRepository;
    @Autowired
    private ReviewRepository reviewTestRepository;
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
        reviewTestRepository.deleteAll();
        userTestRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @BeforeEach
    public void setUp() {
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

    public ReviewPublicDto createReview(User user, Media media, int score, String review) {
        Review reviewEntity = Review.builder()
                .user(user)
                .media(media)
                .reviewScore(score)
                .writtenReview(review)
                .build();
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

        ReviewPublicDto review = mapper.readValue(response, ReviewPublicDto.class);

        assertNotNull(review.id());
        assertEquals(USER_ID, review.user().id());
        assertEquals(reviewDto.mediaId(), review.mediaId());
        assertEquals(user.getUserName(), review.user().userName());
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

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(MEDIA_NOT_FOUND + 99999999L, errorDto.message());
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

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertTrue(errorDto.message().contains(ID_GREATER_THAN_0));
        assertTrue(errorDto.message().contains(MAX_REVIEW_CHARACTERS));
        assertTrue(errorDto.message().contains(INVALID_SCORE));
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

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(ALREADY_REVIEWED, errorDto.message());
    }

    @Test
    @DisplayName("Test get review by id and expect status 200 and review")
    void testGetReviewById() throws Exception {
        ReviewPublicDto reviewDto = createReview(user, game, 5, "My Review");

        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + reviewDto.id()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ReviewPublicDto review = mapper.readValue(response, ReviewPublicDto.class);

        assertEquals(reviewDto.id(), review.id());
        assertEquals(user.getId(), review.user().id());
        assertEquals(game.getId(), review.mediaId());
        assertEquals(5, review.reviewScore());
        assertEquals("My Review", review.writtenReview());
    }

    @Test
    @DisplayName("Test get review by id with no review and expect status 404 and message")
    void testGetReviewByIdNotFound() throws Exception {
        String response = mockMvc.perform(get(PUBLIC_API_PATH + "/" + 9999999))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(REVIEW_NOT_FOUND + 9999999, errorDto.message());
    }

    @Test
    @DisplayName("Test get all reviews from user and expect status 200 and list with reviews")
    void testGetAllReviewsFromUser() throws Exception {
        User user = createUser(USER_ID, "UserName2");
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
        User user = createUser(USER_ID, "UserName2");
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
        User user = createUser(USER_ID, "UserName2");
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
            createReview(createUser(USER_ID, "Name" + i), game, 5, "My Review");
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
            createReview(createUser(USER_ID, "Name" + i), game, 5, "My Review");
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
            createReview(createUser(USER_ID, "Name" + i), game, 5, "My Review");
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

        ReviewPublicDto review = mapper.readValue(response, ReviewPublicDto.class);

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

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(REVIEW_NOT_FOUND + 9999999, errorDto.message());
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

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertTrue(errorDto.message().contains(INVALID_SCORE));
        assertTrue(errorDto.message().contains(MAX_REVIEW_CHARACTERS));
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

        ErrorDto errorDto = mapper.readValue(response, ErrorDto.class);

        assertEquals(REVIEW_NOT_FOUND + 9999999, errorDto.message());
    }
}
