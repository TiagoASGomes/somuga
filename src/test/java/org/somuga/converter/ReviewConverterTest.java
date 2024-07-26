package org.somuga.converter;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.somuga.dto.review.ReviewCreateDto;
import org.somuga.dto.review.ReviewPublicDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.Game;
import org.somuga.entity.Review;
import org.somuga.entity.User;
import org.somuga.enums.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@ActiveProfiles("test")
class ReviewConverterTest {

    private static MockedStatic<UserConverter> userConverterMockedStatic;

    private final User user = User.builder()
            .id("1")
            .userName("user")
            .build();

    private final UserPublicDto userPublicDto = new UserPublicDto("1", "user", new Date());

    private final Game game = Game.builder()
            .id(1L)
            .title("game")
            .mediaType(MediaType.GAME)
            .build();

    @BeforeAll
    static void setUp() {
        userConverterMockedStatic = mockStatic(UserConverter.class);
    }
    
    @AfterAll
    static void tearDownAll() {
        userConverterMockedStatic.close();
    }

    @AfterEach
    void tearDown() {
        userConverterMockedStatic.reset();
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should convert Review entity to ReviewPublicDto")
    void fromEntityToPublicDto() {
        Review review = Review.builder()
                .id(1L)
                .user(user)
                .media(game)
                .reviewScore(5)
                .writtenReview("Great movie")
                .build();

        userConverterMockedStatic.when(() -> UserConverter.fromEntityToPublicDto(user)).thenReturn(userPublicDto);

        ReviewPublicDto reviewPublicDto = ReviewConverter.fromEntityToPublicDto(review);

        assertEquals(review.getId(), reviewPublicDto.id());
        assertEquals(review.getUser().getId(), reviewPublicDto.user().id());
        assertEquals(review.getMedia().getId(), reviewPublicDto.mediaId());
        assertEquals(review.getReviewScore(), reviewPublicDto.reviewScore());
        assertEquals(review.getWrittenReview(), reviewPublicDto.writtenReview());
        userConverterMockedStatic.verify(() -> UserConverter.fromEntityToPublicDto(user));
        userConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when Review entity is null")
    void fromEntityToPublicDtoNull() {
        assertNull(ReviewConverter.fromEntityToPublicDto(null));
        userConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when Review entity media is null")
    void fromEntityToPublicDtoNullMedia() {
        Review review = Review.builder()
                .id(1L)
                .user(user)
                .reviewScore(5)
                .writtenReview("Great movie")
                .build();

        assertNull(ReviewConverter.fromEntityToPublicDto(review));
        userConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should convert list of Review entities to list of ReviewPublicDto")
    void fromEntityListToPublicDtoList() {
        List<Review> reviews = List.of(
                Review.builder()
                        .id(1L)
                        .user(user)
                        .media(game)
                        .reviewScore(5)
                        .writtenReview("Great movie")
                        .build(),
                Review.builder()
                        .id(2L)
                        .user(user)
                        .media(game)
                        .reviewScore(4)
                        .writtenReview("Good movie")
                        .build()
        );

        userConverterMockedStatic.when(() -> UserConverter.fromEntityToPublicDto(user)).thenReturn(userPublicDto);

        List<ReviewPublicDto> reviewPublicDtos = ReviewConverter.fromEntityListToPublicDtoList(reviews);

        assertEquals(reviews.size(), reviewPublicDtos.size());
        for (int i = 0; i < reviews.size(); i++) {
            assertEquals(reviews.get(i).getId(), reviewPublicDtos.get(i).id());
            assertEquals(reviews.get(i).getUser().getId(), reviewPublicDtos.get(i).user().id());
            assertEquals(reviews.get(i).getMedia().getId(), reviewPublicDtos.get(i).mediaId());
            assertEquals(reviews.get(i).getReviewScore(), reviewPublicDtos.get(i).reviewScore());
            assertEquals(reviews.get(i).getWrittenReview(), reviewPublicDtos.get(i).writtenReview());
        }
        userConverterMockedStatic.verify(() -> UserConverter.fromEntityToPublicDto(user), Mockito.times(reviews.size()));
        userConverterMockedStatic.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when input list is empty")
    void fromEntityListToPublicDtoListEmpty() {
        List<ReviewPublicDto> reviewPublicDtos = ReviewConverter.fromEntityListToPublicDtoList(List.of());
        assertEquals(0, reviewPublicDtos.size());
        userConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when input list is null")
    void fromEntityListToPublicDtoListNull() {
        List<ReviewPublicDto> reviewPublicDtos = ReviewConverter.fromEntityListToPublicDtoList(null);
        assertEquals(0, reviewPublicDtos.size());
        userConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert ReviewCreateDto to Review entity")
    void fromCreateDtoToEntity() {
        ReviewCreateDto reviewCreateDto = new ReviewCreateDto(1L, 5, "Great movie");

        Review review = ReviewConverter.fromCreateDtoToEntity(reviewCreateDto, user, game);

        assertEquals(user, review.getUser());
        assertEquals(game, review.getMedia());
        assertEquals(reviewCreateDto.reviewScore(), review.getReviewScore());
        assertEquals(reviewCreateDto.writtenReview(), review.getWrittenReview());
        assertNull(review.getId());
        userConverterMockedStatic.verifyNoInteractions();
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return null when ReviewCreateDto is null")
    void fromCreateDtoToEntityNull() {
        assertNull(ReviewConverter.fromCreateDtoToEntity(null, user, game));
        userConverterMockedStatic.verifyNoInteractions();
    }
}