package org.somuga.repository;

import org.somuga.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MediaBaseRepository<Game>, JpaSpecificationExecutor<Game> {

    @Query(value = """
            SELECT * FROM games g
            INNER JOIN media m ON g.id = m.id
            INNER JOIN platforms_games pg ON g.id = pg.games_id
            INNER JOIN platforms p ON p.id = pg.platforms_id
            WHERE p.platform_name = ?1""", nativeQuery = true)
    Page<Game> findByPlatform(String platform, Pageable page);

    @Query(value = """
            SELECT * FROM games g
            INNER JOIN media m ON g.id = m.id
            INNER JOIN game_genres_games gg ON g.id = gg.games_id
            INNER JOIN game_genres ge ON ge.id = gg.genres_id
            WHERE ge.genre = ?1""", nativeQuery = true)
    Page<Game> findByGenre(String genre, Pageable page);

    @Query(value = """
            SELECT * FROM games g
            INNER JOIN media m ON g.id = m.id
            INNER JOIN developers d ON d.id = g.developer_id
            WHERE d.developer_name = ?1""", nativeQuery = true)
    Page<Game> findByDeveloper(String developer, Pageable page);

    Page<Game> findByTitleContainingIgnoreCase(String title, Pageable page);
}
