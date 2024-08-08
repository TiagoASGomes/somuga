package org.somuga.converter;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GameLikePublicDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Game;

import java.util.ArrayList;
import java.util.List;

public class GameConverter {

    private GameConverter() {
    }

    public static GamePublicDto fromEntityToPublicDto(Game game) {
        if (game == null) return null;
        if (game.getLikes() == null) game.setLikes(new ArrayList<>());
        return new GamePublicDto(
                game.getId(),
                game.getTitle(),
                game.getReleaseDate(),
                DeveloperConverter.fromEntityToPublicDto(game.getDeveloper()),
                GameGenreConverter.fromEntityListToPublicDtoList(game.getGenres()),
                PlatformConverter.fromEntityListToPublicDtoList(game.getPlatforms()),
                game.getPrice(),
                game.getDescription(),
                game.getAverageRating(),
                game.getLikes().size(),
                game.getMediaUrl(),
                game.getImageUrl()
        );
    }

    public static List<GamePublicDto> fromEntityListToPublicDtoList(List<Game> games) {
        if (games == null) return new ArrayList<>();
        return games.stream()
                .map(GameConverter::fromEntityToPublicDto)
                .toList();
    }

    public static Game fromCreateDtoToEntity(GameCreateDto gameDto) {
        if (gameDto == null) return null;
        return Game.builder()
                .title(gameDto.title())
                .releaseDate(gameDto.releaseDate())
                .price(gameDto.price())
                .description(gameDto.description())
                .mediaUrl(gameDto.mediaUrl())
                .imageUrl(gameDto.imageUrl())
                .averageRating(0)
                .build();
    }

    public static GameLikePublicDto fromEntityToPublicLikeDto(Game game, boolean isLiked) {
        GamePublicDto gamePublicDto = fromEntityToPublicDto(game);
        return new GameLikePublicDto(
                gamePublicDto,
                isLiked
        );
    }
}
