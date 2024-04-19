package org.somuga.service.interfaces;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.entity.Game;
import org.somuga.entity.Media;

public interface IGameService {
    Game create(GameCreateDto game);

    Game getById(Long id);

    Media findById(Long id);
}
