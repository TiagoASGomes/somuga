package org.somuga.service.interfaces;

import org.somuga.dto.game_genre.GameGenreCreateDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.entity.GameGenre;
import org.somuga.exception.game_genre.GenreAlreadyExistsException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IGameGenreService {
    List<GameGenrePublicDto> getAll(Pageable page);

    GameGenrePublicDto getById(Long id) throws GenreNotFoundException;

    List<GameGenrePublicDto> searchByName(String name, Pageable page);

    GameGenrePublicDto create(GameGenreCreateDto genreDto) throws GenreAlreadyExistsException;

    GameGenre findByGenre(String genre) throws GenreNotFoundException;

}
