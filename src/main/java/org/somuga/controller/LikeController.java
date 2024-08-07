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
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.dto.like.LikeCreateDto;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.like.AlreadyLikedException;
import org.somuga.exception.like.LikeNotFoundException;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.exception.user.UserNotFoundException;
import org.somuga.service.interfaces.ILikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/like")
@Tag(name = "Like", description = "The like API")
public class LikeController {

    private final ILikeService likeService;

    @Autowired
    public LikeController(ILikeService likeService) {
        this.likeService = likeService;
    }

    @Operation(summary = "Get all likes",
            description = "Returns a list of likes, optionally filtered by user id or media id.")
    @ApiResponse(responseCode = "200",
            description = "List of likes",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = GameGenrePublicDto.class)))})
    @Parameter(name = "size", description = "The number of elements to return", example = "10")
    @Parameter(name = "page", description = "The page number to return", example = "0", schema = @Schema(type = "integer"))
    @Parameter(name = "userId", description = "The user id", example = "auth0|1234567890")
    @Parameter(name = "mediaId", description = "The media id", example = "1")
    @GetMapping("/public")
    public ResponseEntity<List<LikePublicDto>> getAll(Pageable page,
                                                      @RequestParam(required = false) String userId,
                                                      @RequestParam(required = false) Long mediaId) {
        return new ResponseEntity<>(likeService.getAll(userId, mediaId, page), HttpStatus.OK);
    }

    @Operation(summary = "Create a like",
            description = "Creates a like.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "The like",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LikePublicDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input or already liked",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User or media not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))
            )})
    @PostMapping("/private")
    public ResponseEntity<LikePublicDto> create(@Valid @RequestBody LikeCreateDto like) throws UserNotFoundException, AlreadyLikedException, MediaNotFoundException {
        return new ResponseEntity<>(likeService.create(like), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a like",
            description = "Deletes a like.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "The like was deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated",
                    content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Invalid permission, like does not belong to user",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Like not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDto.class))
            )})
    @Parameter(name = "id", description = "The like id", example = "1", required = true)
    @DeleteMapping("/private/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws LikeNotFoundException, InvalidPermissionException {
        likeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
