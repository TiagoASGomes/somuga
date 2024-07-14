package org.somuga.repository;

import org.somuga.entity.GameGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameGenreRepository extends JpaRepository<GameGenre, Long> {
    List<GameGenre> findByGenreContainingIgnoreCase(String genre);

    Optional<GameGenre> findByGenreIgnoreCase(String genre);

}
