package org.somuga.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.ErrorDto;
import org.somuga.converter.ReviewConverter;
import org.somuga.dto.review.ReviewCreateDto;
import org.somuga.dto.review.ReviewListDto;
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
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.somuga.testUtils.Utils.*;
import static org.somuga.util.message.Messages.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
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
        reviewRepository.deleteAll();
        userRepository.deleteAll();
        gameRepository.deleteAll();
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
                .email(email)
                .joinDate(new Date())
                .active(true)
                .build();
        return userRepository.save(user);
    }

    public ReviewPublicDto createReview(User user, Media media, int score, String review) {
        Review reviewEntity = Review.builder()
                .user(user)
                .media(media)
                .reviewScore(score)
                .writtenReview(review)
                .build();
        return ReviewConverter.fromEntityToPublicDto(reviewRepository.save(reviewEntity));
    }

    private void assertReviewEquals(ReviewPublicDto review, String userId, Long mediaId, int score, String reviewText) {
        Review reviewEntity = reviewRepository.findById(review.id()).orElse(null);

        assertNotNull(reviewEntity);
        assertEquals(reviewEntity.getId(), review.id());
        assertEquals(userId, review.user().id());
        assertEquals(userId, reviewEntity.getUser().getId());
        assertEquals(mediaId, review.mediaId());
        assertEquals(mediaId, reviewEntity.getMedia().getId());
        assertEquals(score, review.reviewScore());
        assertEquals(score, reviewEntity.getReviewScore());
        assertEquals(reviewText, review.writtenReview());
        assertEquals(reviewText, reviewEntity.getWrittenReview());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review and expect status 201 and like")
    void testCreateGameReview() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), 5, "My Review");

        String response = postRequest(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto), mockMvc);

        ReviewPublicDto review = mapper.readValue(response, ReviewPublicDto.class);

        assertEquals(1, reviewRepository.count());
        assertReviewEquals(review, USER_ID, reviewDto.mediaId(), reviewDto.reviewScore(), reviewDto.writtenReview());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review null review and expect status 200")
    void testCreateGameReviewNullReview() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), 5, null);

        String response = postRequest(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto), mockMvc);

        ReviewPublicDto review = mapper.readValue(response, ReviewPublicDto.class);

        assertEquals(1, reviewRepository.count());
        assertReviewEquals(review, USER_ID, reviewDto.mediaId(), reviewDto.reviewScore(), null);
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review with review over 1024 characters and expect status 400")
    void testCreateGameReviewOver1024Characters() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), 5, "a".repeat(1025));

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, reviewRepository.count());
        assertTrue(error.message().contains(MAX_REVIEW_CHARACTERS));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review with score 0 and expect status 400")
    void testCreateGameReviewScore0() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), 0, "My Review");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, reviewRepository.count());
        assertTrue(error.message().contains(INVALID_SCORE));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review with score 11 and expect status 400")
    void testCreateGameReviewScore11() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), 11, "My Review");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, reviewRepository.count());
        assertTrue(error.message().contains(INVALID_SCORE));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review with score null and expect status 400")
    void testCreateGameReviewScoreNull() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), null, "My Review");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, reviewRepository.count());
        assertTrue(error.message().contains(INVALID_SCORE));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review with mediaId null and expect status 400")
    void testCreateGameReviewMediaIdNull() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(null, 5, "My Review");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, reviewRepository.count());
        assertTrue(error.message().contains(INVALID_ID));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review with mediaId 0 and expect status 400")
    void testCreateGameReviewMediaId0() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(0L, 5, "My Review");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, reviewRepository.count());
        assertTrue(error.message().contains(INVALID_ID));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review empty body and expect status 400")
    void testCreateGameReviewEmptyBody() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(null, null, null);

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, reviewRepository.count());
        assertTrue(error.message().contains(INVALID_ID));
        assertTrue(error.message().contains(INVALID_SCORE));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review with user already reviewed and expect status 400")
    void testCreateGameReviewAlreadyReviewed() throws Exception {
        createReview(user, game, 5, "My Review");

        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), 7, "My Review");

        String response = postRequest(PRIVATE_API_PATH, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        Review review = reviewRepository.findByMediaIdAndUserId(game.getId(), USER_ID).orElse(null);
        assertNotNull(review);
        assertEquals(5, review.getReviewScore());

        assertEquals(1, reviewRepository.count());
        assertEquals(ALREADY_REVIEWED, error.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test create game review with media not found and expect status 404")
    void testCreateGameReviewMediaNotFound() throws Exception {
        long id = game.getId() + 1;
        ReviewCreateDto reviewDto = new ReviewCreateDto(id, 5, "My Review");

        String response = postRequest(PRIVATE_API_PATH, status().isNotFound(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, reviewRepository.count());
        assertEquals(MEDIA_NOT_FOUND + id, error.message());
    }

    @Test
    @WithMockUser(username = "new-user")
    @DisplayName("Test create game review with user not found and expect status 404")
    void testCreateGameReviewUserNotFound() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), 5, "My Review");

        String response = postRequest(PRIVATE_API_PATH, status().isNotFound(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(0, reviewRepository.count());
        assertEquals(USER_NOT_FOUND + "new-user", error.message());
    }

    @Test
    @DisplayName("Test create game review and expect correct average rating")
    void testCreateGameReviewAverageRating() throws Exception {
        ReviewCreateDto reviewDto1 = new ReviewCreateDto(game.getId(), 7, "My Review");
        ReviewCreateDto reviewDto2 = new ReviewCreateDto(game.getId(), 1, "My Review");
        ReviewCreateDto reviewDto3 = new ReviewCreateDto(game.getId(), 2, "My Review");

        createUser("user2", "user2", "email2@example.com");
        createUser("user3", "user3", "email3@example.com");

        postRequestWithUser(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto1), mockMvc, user(USER_ID));
        postRequestWithUser(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto2), mockMvc, user("user2"));
        postRequestWithUser(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto3), mockMvc, user("user3"));

        Game updatedGame = gameRepository.findById(game.getId()).orElse(null);
        assertNotNull(updatedGame);

        Double averageRating = (7.0 + 1.0 + 2.0) / 3.0;
        averageRating = Math.round(averageRating * 10.0) / 10.0;
        assertEquals(averageRating, updatedGame.getAverageRating());
    }

    @Test
    @DisplayName("Test create review unauthenticated and expect status 401")
    void testCreateGameReviewUnauthenticated() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(game.getId(), 5, "My Review");

        postRequest(PRIVATE_API_PATH, status().isUnauthorized(), mapper.writeValueAsString(reviewDto), mockMvc);

        assertEquals(0, reviewRepository.count());
    }

    @Test
    @DisplayName("Test get all reviews and expect status 200")
    void testGetAllReviews() throws Exception {
        String user2Id = createUser("user2", "user2", "email2@example.com").getId();
        createReview(user, game, 5, "My Review");
        createReview(userRepository.findById(user2Id).get(), game, 7, "My Review");

        String response = getRequest(PUBLIC_API_PATH, status().isOk(), mockMvc);

        ReviewListDto reviews = mapper.readValue(response, ReviewListDto.class);

        assertEquals(reviewRepository.count(), reviews.reviews().size());
        assertEquals(reviewRepository.count(), reviews.count());
    }

    @Test
    @DisplayName("Test get all reviews paged and expect status 200")
    void testGetAllReviewsPaged() throws Exception {
        String user2Id = createUser("user2", "user2", "email2@example.com").getId();
        createReview(user, game, 5, "My Review");
        createReview(userRepository.findById(user2Id).get(), game, 7, "My Review");

        String response = getRequest(PUBLIC_API_PATH + "?page=0&size=1", status().isOk(), mockMvc);

        ReviewListDto reviews = mapper.readValue(response, ReviewListDto.class);

        assertEquals(1, reviews.reviews().size());
        assertEquals(reviewRepository.count(), reviews.count());
    }

    @Test
    @DisplayName("Test get all reviews with userId and expect status 200")
    void testGetAllReviewsWithUserId() throws Exception {
        String user2Id = createUser("user2", "user2", "email2@example.com").getId();
        long game2Id = createGame().getId();
        createReview(user, game, 5, "My Review");
        createReview(user, gameRepository.findById(game2Id).get(), 5, "My Review");
        createReview(userRepository.findById(user2Id).get(), game, 7, "My Review");

        String response = getRequest(PUBLIC_API_PATH + "?userId=" + USER_ID, status().isOk(), mockMvc);

        ReviewListDto reviews = mapper.readValue(response, ReviewListDto.class);

        assertEquals(2, reviews.reviews().size());
        assertEquals(2, reviews.count());
        assertTrue(reviews.reviews().stream().allMatch(review -> review.user().id().equals(USER_ID)));
    }

    @Test
    @DisplayName("Test get all reviews with userId paged and expect status 200")
    void testGetAllReviewsWithUserIdPaged() throws Exception {
        String user2Id = createUser("user2", "user2", "email2@example.com").getId();
        long game2Id = createGame().getId();
        createReview(user, game, 5, "My Review");
        createReview(user, gameRepository.findById(game2Id).get(), 5, "My Review");
        createReview(userRepository.findById(user2Id).get(), game, 7, "My Review");

        String response = getRequest(PUBLIC_API_PATH + "?userId=" + USER_ID + "&page=0&size=1", status().isOk(), mockMvc);

        ReviewListDto reviews = mapper.readValue(response, ReviewListDto.class);

        assertEquals(1, reviews.reviews().size());
        assertEquals(2, reviews.count());
        assertTrue(reviews.reviews().stream().allMatch(review -> review.user().id().equals(USER_ID)));
    }

    @Test
    @DisplayName("Test get all reviews with mediaId and expect status 200")
    void testGetAllReviewsWithMediaId() throws Exception {
        String user2Id = createUser("user2", "user2", "email2@example.com").getId();
        long game2Id = createGame().getId();
        createReview(user, game, 5, "My Review");
        createReview(userRepository.findById(user2Id).get(), game, 7, "My Review");
        createReview(user, gameRepository.findById(game2Id).get(), 5, "My Review");

        String response = getRequest(PUBLIC_API_PATH + "?mediaId=" + game.getId(), status().isOk(), mockMvc);

        ReviewListDto reviews = mapper.readValue(response, ReviewListDto.class);

        assertEquals(2, reviews.reviews().size());
        assertEquals(2, reviews.count());
        assertTrue(reviews.reviews().stream().allMatch(review -> review.mediaId().equals(game.getId())));
    }

    @Test
    @DisplayName("Test get all reviews with mediaId paged and expect status 200")
    void testGetAllReviewsWithMediaIdPaged() throws Exception {
        String user2Id = createUser("user2", "user2", "email2@example.com").getId();
        long game2Id = createGame().getId();
        createReview(user, game, 5, "My Review");
        createReview(userRepository.findById(user2Id).get(), game, 7, "My Review");
        createReview(user, gameRepository.findById(game2Id).get(), 5, "My Review");

        String response = getRequest(PUBLIC_API_PATH + "?mediaId=" + game.getId() + "&page=0&size=1", status().isOk(), mockMvc);

        ReviewListDto reviews = mapper.readValue(response, ReviewListDto.class);

        assertEquals(1, reviews.reviews().size());
        assertEquals(2, reviews.count());
        assertTrue(reviews.reviews().stream().allMatch(review -> review.mediaId().equals(game.getId())));
    }

    @Test
    @DisplayName("Test get all reviews with userId and mediaId and expect status 200")
    void testGetAllReviewsWithUserIdAndMediaId() throws Exception {
        String user2Id = createUser("user2", "user2", "email2@example.com").getId();
        long game2Id = createGame().getId();
        createReview(user, game, 5, "My Review");
        createReview(userRepository.findById(user2Id).get(), game, 7, "My Review");
        createReview(user, gameRepository.findById(game2Id).get(), 5, "My Review");

        String response = getRequest(PUBLIC_API_PATH + "?userId=" + USER_ID + "&mediaId=" + game.getId(), status().isOk(), mockMvc);

        ReviewListDto reviews = mapper.readValue(response, ReviewListDto.class);

        assertEquals(1, reviews.reviews().size());
        assertEquals(USER_ID, reviews.reviews().get(0).user().id());
        assertEquals(game.getId(), reviews.reviews().get(0).mediaId());
        assertEquals(1, reviews.count());
    }

    @Test
    @DisplayName("Test get review by ID and expect status 200")
    void testGetReviewById() throws Exception {
        long review = createReview(user, game, 5, "My Review").id();

        String response = getRequest(PUBLIC_API_PATH + "/" + review, status().isOk(), mockMvc);

        ReviewPublicDto reviewResponse = mapper.readValue(response, ReviewPublicDto.class);

        assertReviewEquals(reviewResponse, USER_ID, game.getId(), 5, "My Review");
    }

    @Test
    @DisplayName("Test get review by ID not found and expect status 404")
    void testGetReviewByIdNotFound() throws Exception {
        long review = createReview(user, game, 5, "My Review").id();

        String response = getRequest(PUBLIC_API_PATH + "/" + (review + 1), status().isNotFound(), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(REVIEW_NOT_FOUND + (review + 1), error.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review and expect status 200")
    void testUpdateReview() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();
        ReviewUpdateDto reviewDto = new ReviewUpdateDto(7, "My Updated Review");

        String response = patchRequest(PRIVATE_API_PATH + "/" + reviewId, status().isOk(), mapper.writeValueAsString(reviewDto), mockMvc);

        ReviewPublicDto review = mapper.readValue(response, ReviewPublicDto.class);

        assertEquals(1, reviewRepository.count());
        assertReviewEquals(review, USER_ID, game.getId(), reviewDto.reviewScore(), reviewDto.writtenReview());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review null review and expect status 200")
    void testUpdateReviewNullReview() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();
        ReviewUpdateDto reviewDto = new ReviewUpdateDto(7, null);

        String response = patchRequest(PRIVATE_API_PATH + "/" + reviewId, status().isOk(), mapper.writeValueAsString(reviewDto), mockMvc);

        ReviewPublicDto review = mapper.readValue(response, ReviewPublicDto.class);

        assertReviewEquals(review, USER_ID, game.getId(), reviewDto.reviewScore(), null);
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review with review over 1024 characters and expect status 400")
    void testUpdateReviewOver1024Characters() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();
        ReviewUpdateDto reviewDto = new ReviewUpdateDto(7, "a".repeat(1025));

        String response = patchRequest(PRIVATE_API_PATH + "/" + reviewId, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        Review review = reviewRepository.findById(reviewId).orElse(null);
        assertNotNull(review);
        assertEquals(5, review.getReviewScore());

        assertTrue(error.message().contains(MAX_REVIEW_CHARACTERS));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review with score 0 and expect status 400")
    void testUpdateReviewScore0() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();
        ReviewUpdateDto reviewDto = new ReviewUpdateDto(0, "My Review");

        String response = patchRequest(PRIVATE_API_PATH + "/" + reviewId, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        Review review = reviewRepository.findById(reviewId).orElse(null);
        assertNotNull(review);
        assertEquals(5, review.getReviewScore());

        assertTrue(error.message().contains(INVALID_SCORE));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review with score 11 and expect status 400")
    void testUpdateReviewScore11() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();
        ReviewUpdateDto reviewDto = new ReviewUpdateDto(11, "My Review");

        String response = patchRequest(PRIVATE_API_PATH + "/" + reviewId, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        Review review = reviewRepository.findById(reviewId).orElse(null);
        assertNotNull(review);
        assertEquals(5, review.getReviewScore());

        assertTrue(error.message().contains(INVALID_SCORE));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review with score null and expect status 400")
    void testUpdateReviewScoreNull() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();
        ReviewUpdateDto reviewDto = new ReviewUpdateDto(null, "My Review");

        String response = patchRequest(PRIVATE_API_PATH + "/" + reviewId, status().isBadRequest(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        Review review = reviewRepository.findById(reviewId).orElse(null);
        assertNotNull(review);
        assertEquals(5, review.getReviewScore());

        assertTrue(error.message().contains(INVALID_SCORE));
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review with review not found and expect status 404")
    void testUpdateReviewNotFound() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();
        ReviewUpdateDto reviewDto = new ReviewUpdateDto(7, "My Updated Review");

        String response = patchRequest(PRIVATE_API_PATH + "/" + (reviewId + 1), status().isNotFound(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        Review review = reviewRepository.findById(reviewId).orElse(null);
        assertNotNull(review);
        assertEquals(5, review.getReviewScore());

        assertEquals(REVIEW_NOT_FOUND + (reviewId + 1), error.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test update review with user not matching and expect status 403")
    void testUpdateReviewUserNotMatching() throws Exception {
        User user2 = createUser("user2", "user2", "email2@example.com");
        long reviewId = createReview(user2, game, 5, "My Review").id();
        ReviewUpdateDto reviewDto = new ReviewUpdateDto(7, "My Updated Review");

        String response = patchRequest(PRIVATE_API_PATH + "/" + reviewId, status().isForbidden(), mapper.writeValueAsString(reviewDto), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        Review review = reviewRepository.findById(reviewId).orElse(null);
        assertNotNull(review);
        assertEquals(5, review.getReviewScore());

        assertEquals(UNAUTHORIZED_UPDATE, error.message());
    }

    @Test
    @DisplayName("Test update review unathenticated and expect status 401")
    void testUpdateReviewUnauthenticated() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();
        ReviewUpdateDto reviewDto = new ReviewUpdateDto(7, "My Updated Review");

        patchRequest(PRIVATE_API_PATH + "/" + reviewId, status().isUnauthorized(), mapper.writeValueAsString(reviewDto), mockMvc);

        Review review = reviewRepository.findById(reviewId).orElse(null);
        assertNotNull(review);
        assertEquals(5, review.getReviewScore());
    }

    @Test
    @DisplayName("Test update review and expect correct game avg rating")
    void testUpdateReviewAverageRating() throws Exception {
        ReviewCreateDto reviewDto1 = new ReviewCreateDto(game.getId(), 7, "My Review");
        ReviewCreateDto reviewDto2 = new ReviewCreateDto(game.getId(), 1, "My Review");
        ReviewCreateDto reviewDto3 = new ReviewCreateDto(game.getId(), 2, "My Review");

        createUser("user2", "user2", "email2@example.com");
        createUser("user3", "user3", "email3@example.com");

        String response = postRequestWithUser(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto1), mockMvc, user(USER_ID));
        postRequestWithUser(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto2), mockMvc, user("user2"));
        postRequestWithUser(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto3), mockMvc, user("user3"));

        Long reviewId = mapper.readValue(response, ReviewPublicDto.class).id();

        ReviewUpdateDto reviewUpdateDto = new ReviewUpdateDto(10, "My Updated Review");

        patchRequestWithUser(PRIVATE_API_PATH + "/" + reviewId, status().isOk(), mapper.writeValueAsString(reviewUpdateDto), mockMvc, user(USER_ID));

        Game updatedGame = gameRepository.findById(game.getId()).orElse(null);
        assertNotNull(updatedGame);

        Double averageRating = (10.0 + 1.0 + 2.0) / 3.0;
        averageRating = Math.round(averageRating * 10.0) / 10.0;

        assertEquals(averageRating, updatedGame.getAverageRating());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete review and expect status 204")
    void testDeleteReview() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();

        deleteRequest(PRIVATE_API_PATH + "/" + reviewId, status().isNoContent(), mockMvc);

        assertEquals(0, reviewRepository.count());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete review not found and expect status 404")
    void testDeleteReviewNotFound() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();

        String response = deleteRequest(PRIVATE_API_PATH + "/" + (reviewId + 1), status().isNotFound(), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(1, reviewRepository.count());
        assertEquals(REVIEW_NOT_FOUND + (reviewId + 1), error.message());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete review with user not matching and expect status 403")
    void testDeleteReviewUserNotMatching() throws Exception {
        User user2 = createUser("user2", "user2", "email2@example.com");
        long reviewId = createReview(user2, game, 5, "My Review").id();

        String response = deleteRequest(PRIVATE_API_PATH + "/" + reviewId, status().isForbidden(), mockMvc);

        ErrorDto error = mapper.readValue(response, ErrorDto.class);

        assertEquals(1, reviewRepository.count());
        assertEquals(UNAUTHORIZED_DELETE, error.message());
    }

    @Test
    @WithMockUser(username = USER_ID, authorities = "ADMIN")
    @DisplayName("Test delete review from another user with admin and expect status 204")
    void testDeleteReviewAdmin() throws Exception {
        User user2 = createUser("user2", "user2", "email2@example.com");
        long reviewId = createReview(user2, game, 5, "My Review").id();

        deleteRequest(PRIVATE_API_PATH + "/" + reviewId, status().isNoContent(), mockMvc);

        assertEquals(0, reviewRepository.count());
    }

    @Test
    @DisplayName("Test delete review unauthenticated and expect status 401")
    void testDeleteReviewUnauthenticated() throws Exception {
        long reviewId = createReview(user, game, 5, "My Review").id();

        deleteRequest(PRIVATE_API_PATH + "/" + reviewId, status().isUnauthorized(), mockMvc);

        assertEquals(1, reviewRepository.count());
    }

    @Test
    @DisplayName("Test delete review and expect correct game avg rating")
    void testDeleteReviewAverageRating() throws Exception {
        ReviewCreateDto reviewDto1 = new ReviewCreateDto(game.getId(), 7, "My Review");
        ReviewCreateDto reviewDto2 = new ReviewCreateDto(game.getId(), 1, "My Review");
        ReviewCreateDto reviewDto3 = new ReviewCreateDto(game.getId(), 2, "My Review");

        createUser("user2", "user2", "email2@example.com");
        createUser("user3", "user3", "email3@example.com");

        String response = postRequestWithUser(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto1), mockMvc, user(USER_ID));
        postRequestWithUser(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto2), mockMvc, user("user2"));
        postRequestWithUser(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto3), mockMvc, user("user3"));

        Long reviewId = mapper.readValue(response, ReviewPublicDto.class).id();

        deleteRequestWithUser(PRIVATE_API_PATH + "/" + reviewId, status().isNoContent(), mockMvc, user(USER_ID));

        Game updatedGame = gameRepository.findById(game.getId()).orElse(null);
        assertNotNull(updatedGame);

        Double averageRating = (1.0 + 2.0) / 2.0;
        averageRating = Math.round(averageRating * 10.0) / 10.0;

        assertEquals(averageRating, updatedGame.getAverageRating());
    }

    @Test
    @WithMockUser(username = USER_ID)
    @DisplayName("Test delete last review and expect correct game avg rating")
    void testDeleteLastReviewAverageRating() throws Exception {
        ReviewCreateDto reviewDto1 = new ReviewCreateDto(game.getId(), 7, "My Review");

        String response = postRequestWithUser(PRIVATE_API_PATH, status().isCreated(), mapper.writeValueAsString(reviewDto1), mockMvc, user(USER_ID));

        Long reviewId = mapper.readValue(response, ReviewPublicDto.class).id();

        deleteRequestWithUser(PRIVATE_API_PATH + "/" + reviewId, status().isNoContent(), mockMvc, user(USER_ID));

        Game updatedGame = gameRepository.findById(game.getId()).orElse(null);
        assertNotNull(updatedGame);

        assertEquals(0.0, updatedGame.getAverageRating());
    }
}
