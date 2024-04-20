package org.somuga.service;

import org.somuga.converter.LikeConverter;
import org.somuga.dto.like.LikeCreateDto;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.entity.Like;
import org.somuga.entity.Media;
import org.somuga.entity.User;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.somuga.message.Messages.ALREADY_LIKED;
import static org.somuga.message.Messages.LIKE_NOT_FOUND;

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
    public List<LikePublicDto> getAllByUserId(Long userId, Pageable page) {
        return LikeConverter.fromEntityListToPublidDtoList(likeRepo.findByUserId(userId, page).toList());
    }

    @Override
    public List<LikePublicDto> getAllByMediaId(Long mediaId, Pageable page) {
        return LikeConverter.fromEntityListToPublidDtoList(likeRepo.findByMediaId(mediaId, page).toList());
    }

    @Override
    public LikePublicDto create(LikeCreateDto likeDto) throws UserNotFoundException, AlreadyLikedException, MediaNotFoundException {
        Optional<Like> duplicateLike = likeRepo.findByMediaIdAndUserId(likeDto.mediaId(), likeDto.userId());
        if (duplicateLike.isPresent()) {
            throw new AlreadyLikedException(ALREADY_LIKED);
        }
        User user = userService.findById(likeDto.userId());
        Media media = mediaService.findById(likeDto.mediaId());
        Like like = new Like(user, media);
        return LikeConverter.fromEntityToPublicDto(likeRepo.save(like));
    }

    @Override
    public void delete(Long id) throws LikeNotFoundException {
        likeRepo.findById(id).orElseThrow(() -> new LikeNotFoundException(LIKE_NOT_FOUND + id));
        //TODO verificar se é o user que criou
        likeRepo.deleteById(id);
    }
}
