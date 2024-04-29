package org.somuga.repository;

import org.somuga.entity.MovieCrew;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieCrewRepository extends JpaRepository<MovieCrew, Long> {

    Page<MovieCrew> findByFullNameContainingIgnoreCase(String fullName, Pageable page);
}
