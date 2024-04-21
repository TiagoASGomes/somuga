package org.somuga.repository;

import org.somuga.entity.Developer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    Page<Developer> findByDeveloperNameContaining(String developerName, Pageable page);

    Optional<Developer> findByDeveloperName(String developerName);
}
