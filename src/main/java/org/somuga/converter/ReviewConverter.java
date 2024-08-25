package org.somuga.converter;

import org.somuga.dto.review.ReviewCreateDto;
import org.somuga.dto.review.ReviewListDto;
import org.somuga.dto.review.ReviewPublicDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.Media;
import org.somuga.entity.Review;
import org.somuga.entity.User;

import java.util.ArrayList;
import java.util.List;

public class ReviewConverter {

    private ReviewConverter() {
    }

    public static ReviewPublicDto fromEntityToPublicDto(Review review) {
        if (review == null) return null;
        if (review.getMedia() == null) return null;
        UserPublicDto user = UserConverter.fromEntityToPublicDto(review.getUser());
        return new ReviewPublicDto(
                review.getId(),
                user,
                review.getMedia().getId(),
                review.getReviewScore(),
                review.getWrittenReview());
    }

    public static ReviewListDto fromEntityListToPublicDtoList(List<Review> reviews, Long count) {
        if (reviews == null) return new ReviewListDto(new ArrayList<>(), 0L);
        List<ReviewPublicDto> reviewPublicDtos = reviews.stream()
                .map(ReviewConverter::fromEntityToPublicDto)
                .toList();
        return new ReviewListDto(reviewPublicDtos, count);
    }

    public static Review fromCreateDtoToEntity(ReviewCreateDto reviewDto, User user, Media media) {
        if (reviewDto == null) return null;
        return Review.builder()
                .user(user)
                .media(media)
                .reviewScore(reviewDto.reviewScore())
                .writtenReview(reviewDto.writtenReview())
                .build();
    }
}
