package org.somuga.repo;

import jakarta.transaction.Transactional;
import org.somuga.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikeTestRepository extends JpaRepository<Like, Long> {

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE reviews ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    void resetAutoIncrement();
}
