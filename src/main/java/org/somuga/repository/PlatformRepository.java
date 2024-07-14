package org.somuga.repository;

import org.somuga.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {

    List<Platform> findByPlatformNameContainingIgnoreCase(String platformName);

    Optional<Platform> findByPlatformNameIgnoreCase(String platformName);
}
