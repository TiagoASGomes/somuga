package org.somuga.service;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Media;
import org.somuga.repository.GameRepository;
import org.somuga.service.interfaces.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService implements IGameService {

    private final GameRepository gameRepo;

    @Autowired
    public GameService(GameRepository gameRepo) {
        this.gameRepo = gameRepo;
    }


    @Override
    public List<GamePublicDto> getAll(Pageable page) {
        return List.of();
    }

    @Override
    public GamePublicDto getById(Long id) {
        return null;
    }

    @Override
    public GamePublicDto create(GameCreateDto game) {
        return null;
    }

    @Override
    public GamePublicDto update(Long id, GameCreateDto game) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Media findById(Long id) {
        return null;
    }
}
