package org.somuga.repository;

import org.somuga.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    List<Developer> findByDeveloperNameContainingIgnoreCase(String developerName);

    Optional<Developer> findByDeveloperNameIgnoreCase(String developerName);
}
