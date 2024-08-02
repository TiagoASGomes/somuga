package org.somuga.service;

import org.somuga.converter.GameGenreConverter;
import org.somuga.dto.game_genre.GameGenreCreateDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.entity.GameGenre;
import org.somuga.exception.game_genre.GenreAlreadyExistsException;
import org.somuga.exception.game_genre.GenreNotFoundException;
import org.somuga.repository.GameGenreRepository;
import org.somuga.service.interfaces.IGameGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.somuga.util.message.Messages.GENRE_ALREADY_EXISTS;
import static org.somuga.util.message.Messages.GENRE_NOT_FOUND;

@Service
public class GameGenreService implements IGameGenreService {

    private final GameGenreRepository gameGenreRepo;

    @Autowired
    public GameGenreService(GameGenreRepository gameGenreRepo) {
        this.gameGenreRepo = gameGenreRepo;
    }

    @Override
    public List<GameGenrePublicDto> getAll(String name) {
        if (name != null) {
            return GameGenreConverter.fromEntityListToPublicDtoList(gameGenreRepo.findByGenreContainingIgnoreCase(name));
        }
        return GameGenreConverter.fromEntityListToPublicDtoList(gameGenreRepo.findAll());
    }

    @Override
    public GameGenrePublicDto getById(Long id) throws GenreNotFoundException {
        return GameGenreConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public GameGenrePublicDto create(GameGenreCreateDto genreDto) throws GenreAlreadyExistsException {
        Optional<GameGenre> duplicateGenre = findByGenreName(genreDto.genreName());
        if (duplicateGenre.isPresent()) {
            throw new GenreAlreadyExistsException(GENRE_ALREADY_EXISTS + genreDto.genreName());
        }
        GameGenre genre = GameGenreConverter.fromCreateDtoToEntity(genreDto);
        return GameGenreConverter.fromEntityToPublicDto(gameGenreRepo.save(genre));
    }

    @Override
    public GameGenrePublicDto update(Long id, GameGenreCreateDto genreDto) throws GenreAlreadyExistsException, GenreNotFoundException {
        Optional<GameGenre> duplicateGenre = findByGenreName(genreDto.genreName());
        if (duplicateGenre.isPresent() && !duplicateGenre.get().getId().equals(id)) {
            throw new GenreAlreadyExistsException(GENRE_ALREADY_EXISTS + genreDto.genreName());
        }
        GameGenre genre = findById(id);
        genre.setGenre(genreDto.genreName());
        return GameGenreConverter.fromEntityToPublicDto(gameGenreRepo.save(genre));
    }

    @Override
    public void delete(Long id) throws GenreNotFoundException {
        findById(id);
        gameGenreRepo.deleteById(id);
    }

    @Override
    public GameGenre findById(Long id) throws GenreNotFoundException {
        return gameGenreRepo.findById(id).orElseThrow(() -> new GenreNotFoundException(GENRE_NOT_FOUND + id));
    }

    private Optional<GameGenre> findByGenreName(String genre) {
        return gameGenreRepo.findByGenreIgnoreCase(genre);
    }
}
