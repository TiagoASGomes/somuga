package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.somuga.dto.game_genre.GameGenreCreateDto;
import org.somuga.dto.game_genre.GameGenrePublicDto;
import org.somuga.entity.GameGenre;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class GameGenreConverterTest {

    @Test
    @DisplayName("Test fromEntityToPublicDto should convert entity to public dto")
    void fromEntityToPublicDto() {
        GameGenre gameGenre = GameGenre.builder()
                .id(1L)
                .genre("Action")
                .build();

        GameGenrePublicDto gameGenrePublicDto = GameGenreConverter.fromEntityToPublicDto(gameGenre);

        assertEquals(gameGenre.getId(), gameGenrePublicDto.id());
        assertEquals(gameGenre.getGenre(), gameGenrePublicDto.genreName());
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when entity is null")
    void fromEntityToPublicDtoNull() {
        GameGenrePublicDto gameGenrePublicDto = GameGenreConverter.fromEntityToPublicDto(null);

        assertNull(gameGenrePublicDto);
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should convert list of entities to list of public dtos")
    void fromEntityListToPublicDtoList() {
        List<GameGenre> gameGenres = List.of(
                GameGenre.builder()
                        .id(1L)
                        .genre("Action")
                        .build(),
                GameGenre.builder()
                        .id(2L)
                        .genre("Adventure")
                        .build()
        );

        List<GameGenrePublicDto> gameGenrePublicDtos = GameGenreConverter.fromEntityListToPublicDtoList(gameGenres);

        assertEquals(gameGenres.size(), gameGenrePublicDtos.size());
        assertEquals(gameGenres.get(0).getId(), gameGenrePublicDtos.get(0).id());
        assertEquals(gameGenres.get(1).getId(), gameGenrePublicDtos.get(1).id());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when input list is empty")
    void fromEntityListToPublicDtoListEmpty() {
        List<GameGenrePublicDto> gameGenrePublicDtos = GameGenreConverter.fromEntityListToPublicDtoList(List.of());

        assertEquals(0, gameGenrePublicDtos.size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when input list is null")
    void fromEntityListToPublicDtoListNull() {
        List<GameGenrePublicDto> gameGenrePublicDtos = GameGenreConverter.fromEntityListToPublicDtoList(null);

        assertEquals(0, gameGenrePublicDtos.size());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert create dto to entity")
    void fromCreateDtoToEntity() {
        GameGenreCreateDto gameGenreCreateDto = new GameGenreCreateDto("Action");

        GameGenre gameGenre = GameGenreConverter.fromCreateDtoToEntity(gameGenreCreateDto);

        assertEquals(gameGenreCreateDto.genreName(), gameGenre.getGenre());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return null when create dto is null")
    void fromCreateDtoToEntityNull() {
        GameGenre gameGenre = GameGenreConverter.fromCreateDtoToEntity(null);

        assertNull(gameGenre);
    }
}