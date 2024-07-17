package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.dto.game.GameCreateDto;
import org.somuga.dto.game.GameLikePublicDto;
import org.somuga.dto.game.GamePublicDto;
import org.somuga.entity.Developer;
import org.somuga.entity.Game;
import org.somuga.entity.GameGenre;
import org.somuga.entity.Platform;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class GameConverterTest {
    //TODO mock Developer, GameGenre, Platform

    private final Developer developer = Developer.builder()
            .id(1L)
            .developerName("Developer Name")
            .build();
    private final List<GameGenre> genres = List.of(
            GameGenre.builder()
                    .id(1L)
                    .genre("Genre 1")
                    .build(),
            GameGenre.builder()
                    .id(2L)
                    .genre("Genre 2")
                    .build()
    );
    private final List<Platform> platforms = List.of(
            Platform.builder()
                    .id(1L)
                    .platformName("Platform 1")
                    .build(),
            Platform.builder()
                    .id(2L)
                    .platformName("Platform 2")
                    .build()
    );

    @Test
    @DisplayName("Test fromEntityToPublicDto should convert entity to public dto")
    void fromEntityToPublicDto() {
        Game game = Game.builder()
                .id(1L)
                .title("Game Title")
                .releaseDate(new Date())
                .price(10.0)
                .description("Game Description")
                .mediaUrl("http://media.com")
                .imageUrl("http://image.com")
                .developer(developer)
                .genres(genres)
                .platforms(platforms)
                .build();

        GamePublicDto gamePublicDto = GameConverter.fromEntityToPublicDto(game);

        assertEquals(game.getId(), gamePublicDto.id());
        assertEquals(game.getTitle(), gamePublicDto.title());
        assertEquals(game.getReleaseDate(), gamePublicDto.releaseDate());
        assertEquals(game.getPrice(), gamePublicDto.price());
        assertEquals(game.getDescription(), gamePublicDto.description());
        assertEquals(game.getMediaUrl(), gamePublicDto.mediaUrl());
        assertEquals(game.getImageUrl(), gamePublicDto.imageUrl());
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when entity is null")
    void fromEntityToPublicDtoReturnNull() {
        GamePublicDto gamePublicDto = GameConverter.fromEntityToPublicDto(null);
        assertEquals(null, gamePublicDto);
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return empty entity when entity is empty")
    void fromEntityToPublicDtoReturnEmpty() {
        Game game = new Game();
        GamePublicDto gamePublicDto = GameConverter.fromEntityToPublicDto(game);
        assertNull(gamePublicDto.id());
        assertNull(gamePublicDto.title());
        assertNull(gamePublicDto.releaseDate());
        assertNull(gamePublicDto.price());
        assertNull(gamePublicDto.description());
        assertNull(gamePublicDto.mediaUrl());
        assertNull(gamePublicDto.imageUrl());
        assertEquals(0, gamePublicDto.genres().size());
        assertEquals(0, gamePublicDto.platforms().size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should convert list of entities to list of public dtos")
    void fromEntityListToPublicDtoList() {
        Game game = Game.builder()
                .id(1L)
                .title("Game Title")
                .releaseDate(new Date())
                .price(10.0)
                .description("Game Description")
                .mediaUrl("http://media.com")
                .imageUrl("http://image.com")
                .developer(developer)
                .genres(genres)
                .platforms(platforms)
                .build();

        List<Game> games = List.of(game, game);

        List<GamePublicDto> gamePublicDtos = GameConverter.fromEntityListToPublicDtoList(games);

        assertEquals(games.size(), gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when list of entities is empty")
    void fromEntityListToPublicDtoListReturnEmpty() {
        List<Game> games = List.of();
        List<GamePublicDto> gamePublicDtos = GameConverter.fromEntityListToPublicDtoList(games);
        assertEquals(0, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when list of entities is null")
    void fromEntityListToPublicDtoListReturnNull() {
        List<GamePublicDto> gamePublicDtos = GameConverter.fromEntityListToPublicDtoList(null);
        assertEquals(0, gamePublicDtos.size());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert create dto to entity")
    void fromCreateDtoToEntity() {
        GameCreateDto gameDto = new GameCreateDto(
                "Game Title",
                new Date(),
                1L,
                List.of(1L, 2L),
                List.of(1L, 2L),
                10.0,
                "Game Description",
                "http://media.com",
                "http://image.com"
        );

        Game game = GameConverter.fromCreateDtoToEntity(gameDto);

        assertEquals(gameDto.title(), game.getTitle());
        assertEquals(gameDto.releaseDate(), game.getReleaseDate());
        assertEquals(gameDto.price(), game.getPrice());
        assertEquals(gameDto.description(), game.getDescription());
        assertEquals(gameDto.mediaUrl(), game.getMediaUrl());
        assertEquals(gameDto.imageUrl(), game.getImageUrl());
        assertNull(game.getDeveloper());
        assertNull(game.getGenres());
        assertNull(game.getPlatforms());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return null when create dto is null")
    void fromCreateDtoToEntityReturnEmpty() {
        Game game = GameConverter.fromCreateDtoToEntity(null);
        assertNull(game);
    }

    @Test
    @DisplayName("Test fromEntityToPublicLikeDto should convert entity to public like dto")
    void fromEntityToPublicLikeDto() {
        Game game = Game.builder()
                .id(1L)
                .title("Game Title")
                .releaseDate(new Date())
                .price(10.0)
                .description("Game Description")
                .mediaUrl("http://media.com")
                .imageUrl("http://image.com")
                .developer(developer)
                .genres(genres)
                .platforms(platforms)
                .build();

        GameLikePublicDto gameLikePublicDto = GameConverter.fromEntityToPublicLikeDto(game, true);

        assertEquals(game.getId(), gameLikePublicDto.game().id());
        assertEquals(game.getTitle(), gameLikePublicDto.game().title());
        assertEquals(game.getReleaseDate(), gameLikePublicDto.game().releaseDate());
        assertEquals(game.getPrice(), gameLikePublicDto.game().price());
        assertEquals(game.getDescription(), gameLikePublicDto.game().description());
        assertEquals(game.getMediaUrl(), gameLikePublicDto.game().mediaUrl());
        assertEquals(game.getImageUrl(), gameLikePublicDto.game().imageUrl());
        assertEquals(game.getDeveloper().getId(), gameLikePublicDto.game().developer().id());
        assertEquals(game.getGenres().size(), gameLikePublicDto.game().genres().size());
        assertEquals(game.getPlatforms().size(), gameLikePublicDto.game().platforms().size());
        assertTrue(gameLikePublicDto.liked());

    }
}