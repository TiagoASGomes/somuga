package org.somuga.converter;

import org.somuga.dto.game_genre.GameGenreCreateDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.entity.GameGenre;

import java.util.ArrayList;
import java.util.List;

public class GameGenreConverter {

    private GameGenreConverter() {
    }

    public static GameGenrePublicDto fromEntityToPublicDto(GameGenre gameGenre) {
        if (gameGenre == null) return null;
        return new GameGenrePublicDto(
                gameGenre.getId(),
                gameGenre.getGenre()
        );
    }

    public static List<GameGenrePublicDto> fromEntityListToPublicDtoList(List<GameGenre> gameGenres) {
        if (gameGenres == null) return new ArrayList<>();
        return gameGenres.stream()
                .map(GameGenreConverter::fromEntityToPublicDto)
                .toList();
    }

    public static GameGenre fromCreateDtoToEntity(GameGenreCreateDto genreDto) {
        if (genreDto == null) return null;
        return GameGenre.builder()
                .genre(genreDto.genreName())
                .build();
    }
}
