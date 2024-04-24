package org.somuga.repository;

import org.somuga.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MediaBaseRepository<Game> {

    @Query(value = """
            SELECT * FROM games g
            INNER JOIN media m ON g.id = m.id
            INNER JOIN platforms_games pg ON g.id = pg.games_id
            INNER JOIN platforms p ON p.id = pg.platforms_id
            WHERE p.platform_name = ?1""", nativeQuery = true)
    Page<Game> findByPlatform(String platform, Pageable page);
}
