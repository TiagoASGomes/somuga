package org.somuga.service;

import org.somuga.converter.ReviewConverter;
import org.somuga.dto.review.ReviewCreateDto;
import org.somuga.dto.review.ReviewPublicDto;
import org.somuga.dto.review.ReviewUpdateDto;
import org.somuga.entity.Media;
import org.somuga.entity.Review;
import org.somuga.entity.User;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.exception.review.AlreadyReviewedException;
import org.somuga.exception.review.ReviewNotFoundException;
import org.somuga.exception.user.UserNotFoundException;
import org.somuga.repository.ReviewRepository;
import org.somuga.service.interfaces.IMediaService;
import org.somuga.service.interfaces.IReviewService;
import org.somuga.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.somuga.message.Messages.ALREADY_REVIEWED;
import static org.somuga.message.Messages.REVIEW_NOT_FOUND;

@Service
public class ReviewService implements IReviewService {

    private final ReviewRepository reviewRepo;
    private final IUserService userService;
    private final IMediaService mediaService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepo, IUserService userService, IMediaService mediaService) {
        this.reviewRepo = reviewRepo;
        this.userService = userService;
        this.mediaService = mediaService;
    }


    @Override
    public ReviewPublicDto getById(Long id) throws ReviewNotFoundException {
        return ReviewConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public List<ReviewPublicDto> getAllByUserId(Long userId, Pageable page) {
        return ReviewConverter.fromEntityListToPublidDtoList(reviewRepo.findByUserId(userId, page).toList());
    }

    @Override
    public List<ReviewPublicDto> getAllByMediaId(Long mediaId, Pageable page) {
        return ReviewConverter.fromEntityListToPublidDtoList(reviewRepo.findByMediaId(mediaId, page).toList());
    }

    @Override
    public ReviewPublicDto create(ReviewCreateDto reviewDto) throws UserNotFoundException, AlreadyReviewedException, MediaNotFoundException {
        Optional<Review> duplicateReview = reviewRepo.findByMediaIdAndUserId(reviewDto.mediaId(), reviewDto.userId());
        if (duplicateReview.isPresent()) {
            throw new AlreadyReviewedException(ALREADY_REVIEWED);
        }
        User user = userService.findById(reviewDto.userId());
        Media media = mediaService.findById(reviewDto.mediaId());
        Review review = new Review(reviewDto.reviewScore(), reviewDto.writtenReview(), user, media);
        return ReviewConverter.fromEntityToPublicDto(reviewRepo.save(review));
    }

    @Override
    public ReviewPublicDto updateReview(Long id, ReviewUpdateDto reviewDto) throws ReviewNotFoundException {
        Review review = findById(id);
        review.setReviewScore(reviewDto.reviewScore());
        review.setWrittenReview(reviewDto.writtenReview());
        return ReviewConverter.fromEntityToPublicDto(reviewRepo.save(review));
    }

    @Override
    public void delete(Long id) throws ReviewNotFoundException {
        findById(id);
        //TODO verificar se é o user que criou
        reviewRepo.deleteById(id);
    }

    private Review findById(Long id) throws ReviewNotFoundException {
        return reviewRepo.findById(id).orElseThrow(() -> new ReviewNotFoundException(REVIEW_NOT_FOUND + id));
    }
}
