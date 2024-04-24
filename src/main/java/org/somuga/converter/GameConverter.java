package org.somuga.converter;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Game;

import java.util.List;

public class GameConverter {
    public static GamePublicDto fromEntityToPublicDto(Game game) {
        return new GamePublicDto(
                game.getId(),
                game.getTitle(),
                game.getReleaseDate(),
                DeveloperConverter.fromEntityToPublicDto(game.getDeveloper()),
                game.getGenres().stream().map(GameGenreConverter::fromEntityToPublicDto).toList(),
                game.getPlatforms().stream().map(PlatformConverter::fromEntityToPublicDto).toList(),
                game.getPrice(),
                game.getDescription(),
                game.getReviews().size(),
                game.getLikes().size()
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
        game.setPrice(gameDto.price());
        game.setDescription(gameDto.description());
        return game;
    }
}
