package org.somuga.converter;

import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GameLikePublicDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Game;

import java.util.List;
import java.util.Set;

public class GameConverter {
    public static GamePublicDto fromEntityToPublicDto(Game game) {
        if (game == null) return null;
        if (game.getReviews() == null) game.setReviews(Set.of());
        if (game.getLikes() == null) game.setLikes(Set.of());
        if (game.getGenres() == null) game.setGenres(Set.of());
        if (game.getPlatforms() == null) game.setPlatforms(Set.of());
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
                game.getLikes().size(),
                game.getMediaUrl(),
                game.getImageUrl()
        );
    }

    public static List<GamePublicDto> fromEntityListToPublicDtoList(List<Game> games) {
        return games.stream()
                .map(game -> fromEntityToPublicDto(game))
                .toList();
    }

    public static Game fromCreateDtoToEntity(GameCreateDto gameDto) {
        return Game.builder()
                .title(gameDto.title())
                .releaseDate(gameDto.releaseDate())
                .price(gameDto.price())
                .description(gameDto.description())
                .mediaUrl(gameDto.mediaUrl())
                .imageUrl(gameDto.imageUrl())
                .build();
    }

    public static GameLikePublicDto fromEntityToPublicLikeDto(Game game, boolean isLiked) {
        if (game == null) return null;
        if (game.getReviews() == null) game.setReviews(Set.of());
        if (game.getLikes() == null) game.setLikes(Set.of());
        if (game.getGenres() == null) game.setGenres(Set.of());
        if (game.getPlatforms() == null) game.setPlatforms(Set.of());
        return new GameLikePublicDto(
                game.getId(),
                game.getTitle(),
                game.getReleaseDate(),
                DeveloperConverter.fromEntityToPublicDto(game.getDeveloper()),
                game.getGenres().stream().map(GameGenreConverter::fromEntityToPublicDto).toList(),
                game.getPlatforms().stream().map(PlatformConverter::fromEntityToPublicDto).toList(),
                game.getPrice(),
                game.getDescription(),
                game.getReviews().size(),
                game.getLikes().size(),
                game.getMediaUrl(),
                game.getImageUrl(),
                isLiked
        );
    }
}
