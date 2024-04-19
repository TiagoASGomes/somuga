package org.somuga.service.interfaces;

import org.somuga.dto.like.LikeCreateDto;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.exception.like.AlreadyLikedException;
import org.somuga.exception.user.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ILikeService {
    List<LikePublicDto> getAllByUserId(Long userId, Pageable page);

    List<LikePublicDto> getAllByMediaId(Long mediaId, Pageable page);

    LikePublicDto create(LikeCreateDto like) throws UserNotFoundException, AlreadyLikedException;

    void delete(Long id);
}
