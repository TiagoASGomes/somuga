package org.somuga.service.interfaces;

import org.somuga.dto.like.LikeCreateDto;
import org.somuga.dto.like.LikePublicDto;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.like.AlreadyLikedException;
import org.somuga.exception.like.LikeNotFoundException;
import org.somuga.exception.media.MediaNotFoundException;
import org.somuga.exception.user.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ILikeService {
    List<LikePublicDto> getAll(String userId, Long mediaId, Pageable page);

    LikePublicDto create(LikeCreateDto like) throws UserNotFoundException, AlreadyLikedException, MediaNotFoundException;

    void delete(Long id) throws LikeNotFoundException, InvalidPermissionException;
}
