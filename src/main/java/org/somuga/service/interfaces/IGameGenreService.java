package org.somuga.service.interfaces;

import org.somuga.dto.game_genre.GameGenreCreateDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.entity.GameGenre;
import org.somuga.exception.game_genre.GenreAlreadyExistsException;
import org.somuga.exception.game_genre.GenreNotFoundException;

import java.util.List;

public interface IGameGenreService {
    List<GameGenrePublicDto> getAll(String name);

    GameGenrePublicDto getById(Long id) throws GenreNotFoundException;

    GameGenrePublicDto create(GameGenreCreateDto genreDto) throws GenreAlreadyExistsException;

    GameGenre findByGenre(String genre) throws GenreNotFoundException;

    GameGenrePublicDto update(Long id, GameGenreCreateDto genreDto) throws GenreAlreadyExistsException, GenreNotFoundException;

    void delete(Long id) throws GenreNotFoundException;

    GameGenre findById(Long id) throws GenreNotFoundException;
}
