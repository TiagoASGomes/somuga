package org.somuga.service;

import org.somuga.converter.GameConverter;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Game;
import org.somuga.exception.game.GameNotFoundException;
import org.somuga.repository.GameRepository;
import org.somuga.service.interfaces.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static org.somuga.message.Messages.GAME_NOT_FOUND;

@Service
public class GameService implements IGameService {

    private final GameRepository gameRepo;

    @Autowired
    public GameService(GameRepository gameRepo) {
        this.gameRepo = gameRepo;
    }


    @Override
    public List<GamePublicDto> getAll(Pageable page) {
        return GameConverter.fromEntityListToPublicDtoList(gameRepo.findAll(page).toList());
    }

    @Override
    public GamePublicDto getById(Long id) throws GameNotFoundException {
        return GameConverter.fromEntityToPublicDto(findById(id));
    }

    @Override
    public GamePublicDto create(GameCreateDto gameDto) {
        Game game = GameConverter.fromCreateDtoToEntity(gameDto);
        return GameConverter.fromEntityToPublicDto(gameRepo.save(game));
    }

    @Override
    public GamePublicDto update(Long id, GameCreateDto gameDto) throws GameNotFoundException {
        Game game = findById(id);
        game.setTitle(gameDto.title());
        game.setReleaseDate(gameDto.releaseDate());
        game.setCompany(gameDto.company());
        game.setGenre(gameDto.genre());
        game.setPlatforms(new HashSet<>(gameDto.platforms()));
        return GameConverter.fromEntityToPublicDto(gameRepo.save(game));
    }

    @Override
    public void delete(Long id) throws GameNotFoundException {
        findById(id);
        gameRepo.deleteById(id);
    }

    @Override
    public Game findById(Long id) throws GameNotFoundException {
        return gameRepo.findById(id).orElseThrow(() -> new GameNotFoundException(GAME_NOT_FOUND));
    }
}
