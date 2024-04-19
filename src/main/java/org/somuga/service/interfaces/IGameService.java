package org.somuga.service.interfaces;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Media;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGameService {

    List<GamePublicDto> getAll(Pageable page);

    GamePublicDto getById(Long id);

    GamePublicDto create(GameCreateDto game);

    GamePublicDto update(Long id, GameCreateDto game);

    void delete(Long id);

    Media findById(Long id);
}
