package org.somuga.repository;

import org.somuga.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Page<Like> findByUserId(Long userId, Pageable page);

    Page<Like> findByMediaId(Long mediaId, Pageable page);

    Optional<Like> findByMediaIdAndUserId(Long mediaId, Long userId);
}
