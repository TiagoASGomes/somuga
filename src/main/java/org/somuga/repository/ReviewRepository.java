package org.somuga.repository;

import org.somuga.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByUserId(Long userId, Pageable page);

    Page<Review> findByMediaId(Long mediaId, Pageable page);

    Optional<Review> findByMediaIdAndUserId(Long mediaId, Long userId);
}
