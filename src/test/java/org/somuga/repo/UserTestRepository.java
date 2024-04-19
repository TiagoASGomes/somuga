package org.somuga.repo;

import jakarta.transaction.Transactional;
import org.somuga.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserTestRepository extends JpaRepository<User, Long> {

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    void resetAutoIncrement();
}
