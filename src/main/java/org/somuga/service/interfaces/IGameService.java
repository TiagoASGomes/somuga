package org.somuga.service.interfaces;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Media;
import org.somuga.exception.game.GameNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGameService {

    List<GamePublicDto> getAll(Pageable page);

    GamePublicDto getById(Long id) throws GameNotFoundException;

    GamePublicDto create(GameCreateDto game);

    GamePublicDto update(Long id, GameCreateDto game) throws GameNotFoundException;

    void delete(Long id) throws GameNotFoundException;

    Media findById(Long id) throws GameNotFoundException;
}
