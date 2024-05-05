package org.somuga.controller;

import jakarta.validation.Valid;
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
@CrossOrigin(origins = "*")
public class LikeController {

    private final ILikeService likeService;

    @Autowired
    public LikeController(ILikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping("/public/user/{userId}")
    public ResponseEntity<List<LikePublicDto>> getAllByUserId(@PathVariable String userId, Pageable page) {
        return new ResponseEntity<>(likeService.getAllByUserId(userId, page), HttpStatus.OK);
    }

    @GetMapping("/public/media/{mediaId}")
    public ResponseEntity<List<LikePublicDto>> getAllByMediaId(@PathVariable Long mediaId, Pageable page) {
        return new ResponseEntity<>(likeService.getAllByMediaId(mediaId, page), HttpStatus.OK);
    }

    @PostMapping("/private")
    public ResponseEntity<LikePublicDto> create(@Valid @RequestBody LikeCreateDto like) throws UserNotFoundException, AlreadyLikedException, MediaNotFoundException {
        return new ResponseEntity<>(likeService.create(like), HttpStatus.CREATED);
    }

    @DeleteMapping("/private/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) throws LikeNotFoundException, InvalidPermissionException {
        likeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
