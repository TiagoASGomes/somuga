package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.somuga.dto.movie_crew.MovieCrewCreateDto;
import org.somuga.dto.movie_crew.MovieCrewListDto;
import org.somuga.dto.movie_crew.MovieCrewPublicDto;
import org.somuga.entity.MovieCrew;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class MovieCrewConverterTest {

    @Test
    @DisplayName("Test fromEntityToPublicDto should convert entity to public dto")
    void fromEntityToPublicDto() {
        MovieCrew movieCrew = MovieCrew.builder()
                .id(1L)
                .fullName("John Doe")
                .build();

        MovieCrewPublicDto movieCrewPublicDto = MovieCrewConverter.fromEntityToPublicDto(movieCrew);

        assertEquals(movieCrew.getId(), movieCrewPublicDto.id());
        assertEquals(movieCrew.getFullName(), movieCrewPublicDto.name());
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
                        .build(),
                MovieCrew.builder()
                        .id(2L)
                        .fullName("Jane Doe")
                        .build()
        );

        MovieCrewListDto movieCrewPublicDtoList = MovieCrewConverter.fromEntityListToPublicDtoList(movieCrewList, 2L);

        assertEquals(movieCrewList.size(), movieCrewPublicDtoList.movieCrews().size());
        for (int i = 0; i < movieCrewList.size(); i++) {
            assertEquals(movieCrewList.get(i).getId(), movieCrewPublicDtoList.movieCrews().get(i).id());
            assertEquals(movieCrewList.get(i).getFullName(), movieCrewPublicDtoList.movieCrews().get(i).name());
        }
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when entity list is empty")
    void fromEntityListToPublicDtoListEmpty() {
        MovieCrewListDto movieCrewPublicDtoList = MovieCrewConverter.fromEntityListToPublicDtoList(List.of(), 0L);

        assertEquals(0, movieCrewPublicDtoList.movieCrews().size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when entity list is null")
    void fromEntityListToPublicDtoListNull() {
        MovieCrewListDto movieCrewPublicDtoList = MovieCrewConverter.fromEntityListToPublicDtoList(null, 0L);

        assertEquals(0, movieCrewPublicDtoList.movieCrews().size());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert create dto to entity")
    void fromCreateDtoToEntity() {
        MovieCrewCreateDto movieCrewCreateDto = new MovieCrewCreateDto(
                "John Doe"
        );

        MovieCrew movieCrew = MovieCrewConverter.fromCreateDtoToEntity(movieCrewCreateDto);

        assertNull(movieCrew.getId());
        assertEquals(movieCrewCreateDto.fullName(), movieCrew.getFullName());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return null when create dto is null")
    void fromCreateDtoToEntityNull() {
        assertNull(MovieCrewConverter.fromCreateDtoToEntity(null));
    }
}