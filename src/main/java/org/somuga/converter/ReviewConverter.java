package org.somuga.converter;

import org.somuga.dto.review.ReviewPublicDto;
import org.somuga.dto.user.UserPublicDto;
import org.somuga.entity.Review;

import java.util.List;

public class ReviewConverter {

    public static ReviewPublicDto fromEntityToPublicDto(Review review) {
        UserPublicDto user = UserConverter.fromEntityToPublicDto(review.getUser());
        return new ReviewPublicDto(
                review.getId(),
                user,
                review.getMedia().getId(),
                review.getReviewScore(),
                review.getWrittenReview());
    }

    public static List<ReviewPublicDto> fromEntityListToPublidDtoList(List<Review> reviews) {
        return reviews.stream()
                .map(ReviewConverter::fromEntityToPublicDto)
                .toList();
    }

}
