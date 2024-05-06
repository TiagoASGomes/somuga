package org.somuga.controller;

import jakarta.validation.Valid;
import org.somuga.dto.review.ReviewCreateDto;
import org.somuga.dto.review.ReviewPublicDto;
import org.somuga.dto.review.ReviewUpdateDto;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.exception.review.AlreadyReviewedException;
import org.somuga.exception.review.ReviewNotFoundException;
import org.somuga.exception.user.UserNotFoundException;
import org.somuga.service.interfaces.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/review")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final IReviewService reviewService;

    @Autowired
    public ReviewController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ReviewPublicDto> getById(@PathVariable Long id) throws ReviewNotFoundException {
        return new ResponseEntity<>(reviewService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/public/user/{userId}")
    public ResponseEntity<List<ReviewPublicDto>> getAllByUserId(@PathVariable String userId, Pageable page) {
        return new ResponseEntity<>(reviewService.getAllByUserId(userId, page), HttpStatus.OK);
    }

    @GetMapping("/public/media/{mediaId}")
    public ResponseEntity<List<ReviewPublicDto>> getAllByMediaId(@PathVariable Long mediaId, Pageable page) {
        return new ResponseEntity<>(reviewService.getAllByMediaId(mediaId, page), HttpStatus.OK);
    }

    @PostMapping("/private")
    public ResponseEntity<ReviewPublicDto> create(@Valid @RequestBody ReviewCreateDto review) throws UserNotFoundException, AlreadyReviewedException, MediaNotFoundException {
        return new ResponseEntity<>(reviewService.create(review), HttpStatus.CREATED);
    }

    @PatchMapping("/private/{id}")
    public ResponseEntity<ReviewPublicDto> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewUpdateDto review) throws ReviewNotFoundException, InvalidPermissionException {
        return new ResponseEntity<>(reviewService.updateReview(id, review), HttpStatus.OK);
    }

    @DeleteMapping("/private/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ReviewNotFoundException, InvalidPermissionException {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) throws ReviewNotFoundException, InvalidPermissionException {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
