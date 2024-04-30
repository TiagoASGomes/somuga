package org.somuga.repository;

import org.somuga.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends MediaBaseRepository<Movie> {

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable page);


    @Query(value = """
            SELECT * FROM movies m
            INNER JOIN media md ON m.id = md.id
            INNER JOIN movie_crew_role mcr ON m.id = mcr.movie_id
            WHERE mcr.movie_crew_id = ?1""", nativeQuery = true)
    Page<Movie> findByCrewId(Long crewId, Pageable page);
}
