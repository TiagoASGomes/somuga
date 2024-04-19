package org.somuga.service.interfaces;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.entity.Game;

public interface IGameService {
    Game create(GameCreateDto game);

    Game getById(Long id);
}
