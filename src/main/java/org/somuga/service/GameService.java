package org.somuga.service;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.entity.Game;
import org.somuga.entity.Media;
import org.somuga.enums.MediaType;
import org.somuga.repository.GameRepository;
import org.somuga.service.interfaces.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class GameService implements IGameService {

    private final GameRepository gameRepo;

    @Autowired
    public GameService(GameRepository gameRepo) {
        this.gameRepo = gameRepo;
    }


    @Override
    public Game create(GameCreateDto gameDto) {
        Game game = new Game();
        game.setReleaseDate(gameDto.releaseDate());
        game.setTitle(gameDto.title());
        game.setCompany(gameDto.company());
        game.setGenre(gameDto.genre());
        game.setPlatforms(new HashSet<>(gameDto.platforms()));
        game.setReleaseDate(gameDto.releaseDate());
        game.setMediaType(MediaType.GAME);
        return gameRepo.save(game);
    }

    @Override
    public Game getById(Long id) {
        return gameRepo.findById(id).orElse(null);
    }

    @Override
    public Media findById(Long id) {
        return null;
    }
}
