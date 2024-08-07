package org.somuga.service;

import org.somuga.converter.ReviewConverter;
import org.somuga.dto.review.ReviewCreateDto;
import org.somuga.dto.review.ReviewPublicDto;
import org.somuga.dto.review.ReviewUpdateDto;
import org.somuga.entity.Media;
import org.somuga.entity.Review;
import org.somuga.entity.User;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.exception.review.AlreadyReviewedException;
import org.somuga.exception.review.ReviewNotFoundException;
import org.somuga.exception.user.UserNotFoundException;
import org.somuga.filters.SearchCriteria;
import org.somuga.filters.review.ReviewSpecificationBuilder;
import org.somuga.repository.ReviewRepository;
import org.somuga.service.interfaces.IMediaService;
import org.somuga.service.interfaces.IReviewService;
import org.somuga.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.somuga.util.message.Messages.*;

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
    public List<ReviewPublicDto> getAll(String userId, Long mediaId, Pageable page) {
        List<SearchCriteria> params = createSearchCriteria(userId, mediaId);
        ReviewSpecificationBuilder builder = new ReviewSpecificationBuilder();
        params.forEach(builder::with);
        List<Review> reviews = reviewRepo.findAll(builder.build(), page).toList();
        return ReviewConverter.fromEntityListToPublicDtoList(reviews);
    }

    private List<SearchCriteria> createSearchCriteria(String userId, Long mediaId) {
        List<SearchCriteria> params = new ArrayList<>();
        if (userId != null) {
            params.add(new SearchCriteria("userId", userId));
        }
        if (mediaId != null) {
            params.add(new SearchCriteria("mediaId", String.valueOf(mediaId)));
        }
        return params;
    }

    @Override
    public ReviewPublicDto create(ReviewCreateDto reviewDto) throws UserNotFoundException, AlreadyReviewedException, MediaNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        Optional<Review> duplicateReview = reviewRepo.findByMediaIdAndUserId(reviewDto.mediaId(), userId);
        if (duplicateReview.isPresent()) {
            throw new AlreadyReviewedException(ALREADY_REVIEWED);
        }
        User user = userService.findById(userId);
        Media media = mediaService.findById(reviewDto.mediaId());
        Review review = ReviewConverter.fromCreateDtoToEntity(reviewDto, user, media);
        return ReviewConverter.fromEntityToPublicDto(reviewRepo.save(review));
    }

    @Override
    public ReviewPublicDto updateReview(Long id, ReviewUpdateDto reviewDto) throws ReviewNotFoundException, InvalidPermissionException {
        Review review = findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!review.getUser().getId().equals(auth.getName())) {
            throw new InvalidPermissionException(UNAUTHORIZED_UPDATE);
        }
        review.setReviewScore(reviewDto.reviewScore());
        review.setWrittenReview(reviewDto.writtenReview());
        return ReviewConverter.fromEntityToPublicDto(reviewRepo.save(review));
    }

    @Override
    public void delete(Long id) throws ReviewNotFoundException, InvalidPermissionException {
        Review review = findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ADMIN")) && !review.getUser().getId().equals(auth.getName())) {
            throw new InvalidPermissionException(UNAUTHORIZED_DELETE);
        }
        reviewRepo.deleteById(id);
    }

    private Review findById(Long id) throws ReviewNotFoundException {
        return reviewRepo.findById(id).orElseThrow(() -> new ReviewNotFoundException(REVIEW_NOT_FOUND + id));
    }
}
