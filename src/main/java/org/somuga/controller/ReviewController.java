package org.somuga.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.somuga.aspect.ErrorDto;
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
@Tag(name = "Review", description = "The review API")
public class ReviewController {

    private final IReviewService reviewService;

    @Autowired
    public ReviewController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Get a review by ID",
            description = "Returns a single review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Review found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewPublicDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Review not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "Review ID", example = "1", required = true)
    @GetMapping("/public/{id}")
    public ResponseEntity<ReviewPublicDto> getById(@PathVariable Long id) throws ReviewNotFoundException {
        return new ResponseEntity<>(reviewService.getById(id), HttpStatus.OK);
    }

    @Operation(summary = "Get all reviews",
            description = "Returns a list of reviews, can be filtered by user ID or media ID")
    @ApiResponse(responseCode = "200",
            description = "List of reviews",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ReviewPublicDto.class)))})
    @Parameter(name = "userId", description = "User ID", example = "auth0|1234567890")
    @Parameter(name = "mediaId", description = "Media ID", example = "1")
    @Parameter(name = "page", description = "Page number", example = "0", schema = @Schema(type = "integer"))
    @Parameter(name = "size", description = "Number of elements per page", example = "10")
    @GetMapping("/public")
    public ResponseEntity<List<ReviewPublicDto>> getAll(Pageable page,
                                                        @RequestParam(required = false) String userId,
                                                        @RequestParam(required = false) Long mediaId) {
        return new ResponseEntity<>(reviewService.getAll(userId, mediaId, page), HttpStatus.OK);
    }

    @Operation(summary = "Create a new review",
            description = "Creates a new review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Review created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewPublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input or user already reviewed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Media or user not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @PostMapping("/private")
    public ResponseEntity<ReviewPublicDto> create(@Valid @RequestBody ReviewCreateDto review) throws UserNotFoundException, AlreadyReviewedException, MediaNotFoundException {
        return new ResponseEntity<>(reviewService.create(review), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a review",
            description = "Updates a review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Review updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReviewPublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not enough permissions, review not created by user",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Review not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "Review ID", example = "1", required = true)
    @PatchMapping("/private/{id}")
    public ResponseEntity<ReviewPublicDto> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewUpdateDto review) throws ReviewNotFoundException, InvalidPermissionException {
        return new ResponseEntity<>(reviewService.updateReview(id, review), HttpStatus.OK);
    }

    @Operation(summary = "Delete a review",
            description = "Deletes a review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Review deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Not enough permissions, review not created by user",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Review not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))})})
    @Parameter(name = "id", description = "Review ID", example = "1", required = true)
    @DeleteMapping("/private/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws ReviewNotFoundException, InvalidPermissionException {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
