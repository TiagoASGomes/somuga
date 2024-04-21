package org.somuga.converter;

import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.entity.GameGenre;

import java.util.List;

public class GameGenreConverter {

    public static GameGenrePublicDto fromEntityToPublicDto(GameGenre gameGenre) {
        return new GameGenrePublicDto(
                gameGenre.getId(),
                gameGenre.getGenre()
        );
    }

    public static List<GameGenrePublicDto> fromEntityListToPublicDtoList(List<GameGenre> gameGenres) {
        return gameGenres.stream()
                .map(GameGenreConverter::fromEntityToPublicDto)
                .toList();
    }
}
