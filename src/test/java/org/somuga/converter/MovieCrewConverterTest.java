package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.entity.MovieCrew;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ActiveProfiles("test")
class MovieCrewConverterTest {

    @Test
    @DisplayName("Test fromEntityToPublicDto should convert entity to public dto")
    void fromEntityToPublicDto() {
        MovieCrew movieCrew = MovieCrew.builder()
                .id(1L)
                .fullName("John Doe")
                .birthDate(new Date())
                .build();

        MovieCrewPublicDto movieCrewPublicDto = MovieCrewConverter.fromEntityToPublicDto(movieCrew);

        assertEquals(movieCrew.getId(), movieCrewPublicDto.id());
        assertEquals(movieCrew.getFullName(), movieCrewPublicDto.name());
        assertEquals(movieCrew.getBirthDate(), movieCrewPublicDto.birthDate());
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when entity is null")
    void fromEntityToPublicDtoNull() {
        assertNull(MovieCrewConverter.fromEntityToPublicDto(null));
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should convert entity list to public dto list")
    void fromEntityListToPublicDtoList() {
        List<MovieCrew> movieCrewList = List.of(
                MovieCrew.builder()
                        .id(1L)
                        .fullName("John Doe")
                        .birthDate(new Date())
                        .build(),
                MovieCrew.builder()
                        .id(2L)
                        .fullName("Jane Doe")
                        .birthDate(new Date())
                        .build()
        );

        List<MovieCrewPublicDto> movieCrewPublicDtoList = MovieCrewConverter.fromEntityListToPublicDtoList(movieCrewList);

        assertEquals(movieCrewList.size(), movieCrewPublicDtoList.size());
        for (int i = 0; i < movieCrewList.size(); i++) {
            assertEquals(movieCrewList.get(i).getId(), movieCrewPublicDtoList.get(i).id());
            assertEquals(movieCrewList.get(i).getFullName(), movieCrewPublicDtoList.get(i).name());
            assertEquals(movieCrewList.get(i).getBirthDate(), movieCrewPublicDtoList.get(i).birthDate());
        }
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when entity list is empty")
    void fromEntityListToPublicDtoListEmpty() {
        List<MovieCrewPublicDto> movieCrewPublicDtoList = MovieCrewConverter.fromEntityListToPublicDtoList(List.of());

        assertEquals(0, movieCrewPublicDtoList.size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when entity list is null")
    void fromEntityListToPublicDtoListNull() {
        List<MovieCrewPublicDto> movieCrewPublicDtoList = MovieCrewConverter.fromEntityListToPublicDtoList(null);

        assertEquals(0, movieCrewPublicDtoList.size());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert create dto to entity")
    void fromCreateDtoToEntity() {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(
                "John Doe",
                new Date()
        );

        MovieCrew movieCrew = MovieCrewConverter.fromCreateDtoToEntity(movieCrewCreateDto);

        assertNull(movieCrew.getId());
        assertEquals(movieCrewCreateDto.fullName(), movieCrew.getFullName());
        assertEquals(movieCrewCreateDto.birthDate(), movieCrew.getBirthDate());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return null when create dto is null")
    void fromCreateDtoToEntityNull() {
        assertNull(MovieCrewConverter.fromCreateDtoToEntity(null));
    }
}