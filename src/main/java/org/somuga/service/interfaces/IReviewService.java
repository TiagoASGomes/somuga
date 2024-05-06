package org.somuga.service.interfaces;

import org.somuga.dto.review.ReviewCreateDto;
import org.somuga.dto.review.ReviewPublicDto;
import org.somuga.dto.review.ReviewUpdateDto;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.exception.review.AlreadyReviewedException;
import org.somuga.exception.review.ReviewNotFoundException;
import org.somuga.exception.user.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IReviewService {
    ReviewPublicDto getById(Long id) throws ReviewNotFoundException;

    List<ReviewPublicDto> getAllByUserId(String userId, Pageable page);

    List<ReviewPublicDto> getAllByMediaId(Long mediaId, Pageable page);

    ReviewPublicDto create(ReviewCreateDto review) throws UserNotFoundException, AlreadyReviewedException, MediaNotFoundException;

    ReviewPublicDto updateReview(Long id, ReviewUpdateDto review) throws ReviewNotFoundException, InvalidPermissionException;

    void delete(Long id) throws ReviewNotFoundException, InvalidPermissionException;
}
