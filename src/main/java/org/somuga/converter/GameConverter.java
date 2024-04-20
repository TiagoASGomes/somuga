package org.somuga.converter;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GameConverter {
    public static GamePublicDto fromEntityToPublicDto(Game game) {
        return new GamePublicDto(
                game.getId(),
                game.getTitle(),
                game.getReleaseDate(),
                game.getMediaType().name(),
                game.getCompany(),
                game.getGenre(),
                new ArrayList<>(game.getPlatforms())
        );
    }

    public static List<GamePublicDto> fromEntityListToPublicDtoList(List<Game> games) {
        return games.stream()
                .map(GameConverter::fromEntityToPublicDto)
                .toList();
    }

    public static Game fromCreateDtoToEntity(GameCreateDto gameDto) {
        Game game = new Game();
        game.setTitle(gameDto.title());
        game.setReleaseDate(gameDto.releaseDate());
        game.setCompany(gameDto.company());
        game.setGenre(gameDto.genre());
        game.setPlatforms(new HashSet<>(gameDto.platforms()));
        return game;
    }
}
