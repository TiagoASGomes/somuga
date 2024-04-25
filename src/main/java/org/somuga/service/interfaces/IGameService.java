package org.somuga.service.interfaces;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Media;
import org.somuga.exception.developer.DeveloperNotFoundException;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.exception.platform.PlatformNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGameService {

    List<GamePublicDto> getAll(Pageable page);

    List<GamePublicDto> getByPlatform(String platformName, Pageable page);

    List<GamePublicDto> getByGenre(String genreName, Pageable page);

    List<GamePublicDto> getByDeveloper(String developerName, Pageable page);

    List<GamePublicDto> searchByName(String name, Pageable page);

    GamePublicDto getById(Long id) throws GameNotFoundException;

    GamePublicDto create(GameCreateDto game) throws GenreNotFoundException, DeveloperNotFoundException, PlatformNotFoundException;

    GamePublicDto update(Long id, GameCreateDto game) throws GameNotFoundException, DeveloperNotFoundException, PlatformNotFoundException, GenreNotFoundException;

    void delete(Long id) throws GameNotFoundException;

    Media findById(Long id) throws GameNotFoundException;

}

