package org.somuga.repository;

import org.somuga.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    Page<Review> findByUserId(String userId, Pageable page);

    Page<Review> findByMediaId(Long mediaId, Pageable page);

    Optional<Review> findByMediaIdAndUserId(Long mediaId, String userId);
}
