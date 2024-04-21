package org.somuga.repository;

import org.somuga.entity.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    Page<Platform> findByPlatformNameContaining(String platformName, Pageable page);

    Optional<Platform> findByPlatformName(String platformName);
}
