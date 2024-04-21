package org.somuga.repository;

import org.somuga.entity.GameGenre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameGenreRepository extends JpaRepository<GameGenre, Long> {
    Page<GameGenre> findByGenreContaining(String genre, Pageable page);

    Optional<GameGenre> findByGenre(String genre);

}
