package org.somuga.repository;

import org.somuga.entity.Game;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends MediaBaseRepository<Game>, JpaSpecificationExecutor<Game> {

}
