package org.somuga.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.somuga.aspect.Error;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.dto.review.ReviewCreateDto;
import org.somuga.dto.review.ReviewUpdateDto;
import org.somuga.dto.user.UserCreateDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.Developer;
import org.somuga.entity.GameGenre;
import org.somuga.entity.Platform;
import org.somuga.repository.*;
import org.somuga.testDtos.ReviewGameDto;
import org.somuga.testDtos.ReviewMovieDto;
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
import static org.somuga.util.message.Messages.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReviewControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_PATH = "/api/v1/review";
    private final String developerName = "Developer";
    private final String genreName = "genre";
    private final String platformName = "platforms";
    private GamePublicDto game;
    private UserPublicDto user;
    @Autowired
    private MockMvc mockMvc;
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
    public void setUp() throws Exception {
        developerRepository.save(new Developer(developerName, null));
        gameGenreRepository.save(new GameGenre(genreName.toLowerCase()));
        platformRepository.save(new Platform(platformName.toLowerCase()));
        user = createUser("UserName", "email@example.com");
        game = createGame();
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

    public long createReview(Long userId, Long mediaId, org.somuga.enums.MediaType type) throws Exception {
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(userId, mediaId, 5, "My Review");
        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reviewCreateDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        if (type.equals(org.somuga.enums.MediaType.GAME)) {
            return mapper.readValue(response, ReviewGameDto.class).id();
        }
        return mapper.readValue(response, ReviewMovieDto.class).id();

    }

    @Test
    @DisplayName("Test create game review and expect status 201 and like")
    void testCreateGameReview() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(user.id(), game.id(), 5, "My Review");


        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reviewDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ReviewGameDto review = mapper.readValue(response, ReviewGameDto.class);

        assertNotNull(review.id());
        assertEquals(reviewDto.userId(), review.user().id());
        assertEquals(reviewDto.mediaId(), review.media().id());
        assertEquals(user.userName(), review.user().userName());
        assertEquals(game.title(), review.media().title());
        assertEquals(5, review.reviewScore());
        assertEquals("My Review", review.writtenReview());
    }


    @Test
    @DisplayName("Test create review with incorrect id and expect status 404 and message")
    void testCreateReviewIncorrectId() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(user.id(), 99999999L, 5, "My Review");


        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reviewDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(MEDIA_NOT_FOUND + 99999999L, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test create review with incorrect validation and expect status 400 and message")
    void testCreateLikeValidation() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(user.id() * -1, game.id() * -1, (int) (user.id() * 20), "A".repeat(1100));

        String response = mockMvc.perform(post(API_PATH)
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
        assertEquals(API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test create duplicate review and expect status 400 and message")
    void testCreateReviewDuplicate() throws Exception {
        ReviewCreateDto reviewDto = new ReviewCreateDto(user.id(), game.id(), 5, "My Review");


        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reviewDto)))
                .andExpect(status().isCreated());

        String response = mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(reviewDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(ALREADY_REVIEWED, error.getMessage());
        assertEquals(400, error.getStatus());
        assertEquals("POST", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(API_PATH, error.getPath());
    }

    @Test
    @DisplayName("Test get review by id and expect status 200 and review")
    void testGetReviewById() throws Exception {
        long id = createReview(user.id(), game.id(), org.somuga.enums.MediaType.GAME);

        String response = mockMvc.perform(get(API_PATH + "/" + id))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ReviewGameDto review = mapper.readValue(response, ReviewGameDto.class);

        assertEquals(id, review.id());
        assertEquals(user.id(), review.user().id());
        assertEquals(game.id(), review.media().id());
        assertEquals(5, review.reviewScore());
        assertEquals("My Review", review.writtenReview());
    }

    @Test
    @DisplayName("Test get review by id with no review and expect status 404 and message")
    void testGetReviewByIdNotFound() throws Exception {
        String response = mockMvc.perform(get(API_PATH + "/" + 9999999))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(REVIEW_NOT_FOUND + 9999999, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals("GET", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(API_PATH + "/" + 9999999, error.getPath());
    }

    @Test
    @DisplayName("Test get all reviews from user and expect status 200 and list with reviews")
    void testGetAllReviewsFromUser() throws Exception {
        long userId = createUser("UserName2", "email2@example.com").id();
        for (int i = 0; i < 3; i++) {
            createReview(userId, createGame().id(), org.somuga.enums.MediaType.GAME);
        }

        mockMvc.perform(get(API_PATH + "/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("Test get all reviews from user paged and expect status 200 and list with reviews and pages")
    void testGetAllReviewsFromUserPaged() throws Exception {
        long userId = createUser("UserName2", "email2@example.com").id();
        for (int i = 0; i < 3; i++) {
            createReview(userId, createGame().id(), org.somuga.enums.MediaType.GAME);
        }

        mockMvc.perform(get(API_PATH + "/user/" + userId + "?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get(API_PATH + "/user/" + userId + "?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Test get all reviews from user with no reviews and expect status 200 and empty list")
    void testGetAllReviewsFromUserWithNoUserReviews() throws Exception {
        long userId = createUser("UserName2", "email2@example.com").id();
        for (int i = 0; i < 3; i++) {
            createReview(userId, createGame().id(), org.somuga.enums.MediaType.GAME);
        }

        mockMvc.perform(get(API_PATH + "/user/" + 99999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }


    @Test
    @DisplayName("Test get all reviews from media and expect status 200 and list with reviews")
    void testGetAllReviewsFromMedia() throws Exception {
        long mediaId = createGame().id();
        for (int i = 0; i < 6; i++) {
            createReview(createUser("Name" + i, "email" + i + "@example.com").id(), mediaId, org.somuga.enums.MediaType.GAME);
        }

        mockMvc.perform(get(API_PATH + "/media/" + mediaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @Test
    @DisplayName("Test get all reviews from media paged and expect status 200 and list with reviews and pages")
    void testGetAllReviewsFromMediaPaged() throws Exception {
        long mediaId = createGame().id();
        for (int i = 0; i < 6; i++) {
            createReview(createUser("Name" + i, "email" + i + "@example.com").id(), mediaId, org.somuga.enums.MediaType.GAME);
        }

        mockMvc.perform(get(API_PATH + "/media/" + mediaId + "?page=0&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
        mockMvc.perform(get(API_PATH + "/media/" + mediaId + "?page=1&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

    @Test
    @DisplayName("Test get all reviews from media with no reviews and expect status 200 and empty list")
    void testGetAllReviewsFromMediaWithNoMediaReviews() throws Exception {
        long mediaId = createGame().id();
        for (int i = 0; i < 6; i++) {
            createReview(createUser("Name" + i, "email" + i + "@example.com").id(), mediaId, org.somuga.enums.MediaType.GAME);
        }

        mockMvc.perform(get(API_PATH + "/media/" + 999999))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Test update review and expect status 200 and updated review")
    void testUpdateReview() throws Exception {
        long reviewId = createReview(user.id(), game.id(), org.somuga.enums.MediaType.GAME);
        ReviewUpdateDto updateDto = new ReviewUpdateDto(6, "New Review");

        String response = mockMvc.perform(patch(API_PATH + "/" + reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ReviewGameDto review = mapper.readValue(response, ReviewGameDto.class);

        assertEquals(reviewId, review.id());
        assertEquals(6, review.reviewScore());
        assertEquals("New Review", review.writtenReview());
    }

    @Test
    @DisplayName("Test update review with no review and expect status 404 and message")
    void testUpdateReviewNotFound() throws Exception {
        ReviewUpdateDto updateDto = new ReviewUpdateDto(6, "New Review");

        String response = mockMvc.perform(patch(API_PATH + "/" + 9999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(API_PATH + "/" + 9999999, error.getPath());
        assertEquals("PATCH", error.getMethod());
        assertEquals(REVIEW_NOT_FOUND + 9999999, error.getMessage());
        assertEquals(404, error.getStatus());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Test update review with validation errors and expect status 400 and message")
    void testUpdateReviewValidation() throws Exception {
        long reviewId = createReview(user.id(), game.id(), org.somuga.enums.MediaType.GAME);
        ReviewUpdateDto updateDto = new ReviewUpdateDto((int) (user.id() * 20), "A".repeat(1100));

        String response = mockMvc.perform(patch(API_PATH + "/" + reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(API_PATH + "/" + reviewId, error.getPath());
        assertEquals("PATCH", error.getMethod());
        assertTrue(error.getMessage().contains(INVALID_SCORE));
        assertTrue(error.getMessage().contains(MAX_REVIEW_CHARACTERS));
        assertEquals(400, error.getStatus());
        assertNotNull(error.getTimestamp());
    }

    @Test
    @DisplayName("Test delete review and expect status 200 and deleted review")
    void testDeleteReview() throws Exception {
        long reviewId = createReview(user.id(), game.id(), org.somuga.enums.MediaType.GAME);

        mockMvc.perform(delete(API_PATH + "/" + reviewId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(API_PATH + "/user/" + user.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    @DisplayName("Test delete review with no review and expect status 404 and message")
    void testDeleteReviewNotFound() throws Exception {

        String response = mockMvc.perform(delete(API_PATH + "/" + 9999999))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        Error error = mapper.readValue(response, Error.class);

        assertEquals(REVIEW_NOT_FOUND + 9999999, error.getMessage());
        assertEquals(404, error.getStatus());
        assertEquals("DELETE", error.getMethod());
        assertNotNull(error.getTimestamp());
        assertEquals(API_PATH + "/" + 9999999, error.getPath());
    }
}
