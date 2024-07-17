package org.somuga.service;

import org.somuga.converter.LikeConverter;
import org.somuga.dto.like.LikeCreateDto;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.entity.Like;
import org.somuga.entity.Media;
import org.somuga.entity.User;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.like.AlreadyLikedException;
import org.somuga.exception.like.LikeNotFoundException;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.exception.user.UserNotFoundException;
import org.somuga.repository.LikeRepository;
import org.somuga.service.interfaces.ILikeService;
import org.somuga.service.interfaces.IMediaService;
import org.somuga.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.somuga.util.message.Messages.*;

@Service
public class LikeService implements ILikeService {

    private final LikeRepository likeRepo;
    private final IUserService userService;
    private final IMediaService mediaService;

    @Autowired
    public LikeService(LikeRepository likeRepo, IUserService userService, IMediaService mediaService) {
        this.likeRepo = likeRepo;
        this.userService = userService;
        this.mediaService = mediaService;
    }

    @Override
    public List<LikePublicDto> getAllByUserId(String userId, Pageable page) {
        return LikeConverter.fromEntityListToPublicDtoList(likeRepo.findByUserId(userId, page).toList());
    }

    @Override
    public List<LikePublicDto> getAllByMediaId(Long mediaId, Pageable page) {
        return LikeConverter.fromEntityListToPublicDtoList(likeRepo.findByMediaId(mediaId, page).toList());
    }

    @Override
    public LikePublicDto create(LikeCreateDto likeDto) throws UserNotFoundException, AlreadyLikedException, MediaNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        Optional<Like> duplicateLike = likeRepo.findByMediaIdAndUserId(likeDto.mediaId(), userId);
        if (duplicateLike.isPresent()) {
            throw new AlreadyLikedException(ALREADY_LIKED);
        }
        User user = userService.findById(userId);
        Media media = mediaService.findById(likeDto.mediaId());
        Like like = LikeConverter.fromCreateDtoToEntity(user, media);
        return LikeConverter.fromEntityToPublicDto(likeRepo.save(like));
    }

    @Override
    public void delete(Long id) throws LikeNotFoundException, InvalidPermissionException {
        Like like = likeRepo.findById(id).orElseThrow(() -> new LikeNotFoundException(LIKE_NOT_FOUND + id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!like.getUser().getId().equals(auth.getName())) {
            throw new InvalidPermissionException(UNAUTHORIZED_DELETE);
        }
        likeRepo.deleteById(id);
    }
}
