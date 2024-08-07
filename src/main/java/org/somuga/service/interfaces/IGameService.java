package org.somuga.service.interfaces;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GameLikePublicDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Media;
import org.somuga.exception.InvalidPermissionException;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGameService {

    List<GamePublicDto> getAll(Pageable page, String title, List<String> platform, List<String> genre, String developer);

    GameLikePublicDto getById(Long id) throws GameNotFoundException;

    GamePublicDto create(GameCreateDto game) throws GenreNotFoundException, DeveloperNotFoundException, PlatformNotFoundException;

    GamePublicDto update(Long id, GameCreateDto game) throws GameNotFoundException, DeveloperNotFoundException, PlatformNotFoundException, GenreNotFoundException, InvalidPermissionException;

    void delete(Long id) throws GameNotFoundException, InvalidPermissionException;

    Media findById(Long id) throws GameNotFoundException;
}

