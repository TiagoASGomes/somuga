package org.somuga.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.somuga.dto.crew_role.CrewRolePublicDto;
import org.somuga.dto.crew_role.MovieRolePublicDto;
import org.somuga.dto.movie.MovieCreateDto;
import org.somuga.dto.movie.MovieLikePublicDto;
import org.somuga.dto.movie.MoviePublicDto;
import org.somuga.entity.Movie;
import org.somuga.entity.MovieCrew;
import org.somuga.entity.MovieCrewRole;
import org.somuga.enums.MediaType;
import org.somuga.enums.MovieRole;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
class MovieConverterTest {

    private Movie getMovie() {
        Movie movie = Movie.builder()
                .id(1L)
                .title("Movie Title")
                .releaseDate(new Date())
                .description("Movie Description")
                .duration(120)
                .mediaUrl("http://media.com")
                .imageUrl("http://image.com")
                .mediaType(MediaType.MOVIE)
                .build();

        MovieCrew movieCrew = MovieCrew.builder()
                .id(1L)
                .fullName("John Doe")
                .birthDate(new Date())
                .build();

        movie.addMovieCrew(movieCrew, MovieRole.DIRECTOR, "Character Name");

        return movie;
    }


    @Test
    @DisplayName("Test fromEntityToPublicDto should convert entity to public dto")
    void fromEntityToPublicDto() {
        Movie movie = getMovie();

        MoviePublicDto moviePublicDto = MovieConverter.fromEntityToPublicDto(movie);

        assertEquals(movie.getId(), moviePublicDto.id());
        assertEquals(movie.getTitle(), moviePublicDto.title());
        assertEquals(movie.getReleaseDate(), moviePublicDto.releaseDate());
        assertEquals(movie.getDescription(), moviePublicDto.description());
        assertEquals(movie.getDuration(), moviePublicDto.duration());
        assertEquals(movie.getMediaUrl(), moviePublicDto.mediaUrl());
        assertEquals(movie.getImageUrl(), moviePublicDto.imageUrl());
        assertEquals(movie.getLikes().size(), moviePublicDto.likes());
        assertEquals(movie.getReviews().size(), moviePublicDto.reviews());
        assertEquals(1, moviePublicDto.crew().size());
        assertEquals(1L, moviePublicDto.crew().get(0).id());
    }

    @Test
    @DisplayName("Test fromEntityToPublicDto should return null when entity is null")
    void fromEntityToPublicDtoNull() {
        assertNull(MovieConverter.fromEntityToPublicDto(null));
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should convert entity list to public dto list")
    void fromEntityListToPublicDtoList() {
        List<Movie> movies = List.of(getMovie(), getMovie());

        List<MoviePublicDto> moviePublicDtos = MovieConverter.fromEntityListToPublicDtoList(movies);

        assertEquals(movies.size(), moviePublicDtos.size());
        assertEquals(movies.get(0).getId(), moviePublicDtos.get(0).id());
        assertEquals(movies.get(1).getId(), moviePublicDtos.get(1).id());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when entity list is null")
    void fromEntityListToPublicDtoListNull() {
        assertEquals(0, MovieConverter.fromEntityListToPublicDtoList(null).size());
    }

    @Test
    @DisplayName("Test fromEntityListToPublicDtoList should return empty list when entity list is empty")
    void fromEntityListToPublicDtoListEmpty() {
        assertEquals(0, MovieConverter.fromEntityListToPublicDtoList(List.of()).size());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should convert create dto to entity")
    void fromCreateDtoToEntity() {
        MovieCreateDto movieCreateDto = new MovieCreateDto(
                "Movie Title",
                new Date(),
                "Movie Description",
                120,
                List.of(),
                "http://media.com",
                "http://image.com"
        );

        Movie movie = MovieConverter.fromCreateDtoToEntity(movieCreateDto);

        assertNull(movie.getId());
        assertEquals(movieCreateDto.title(), movie.getTitle());
        assertEquals(movieCreateDto.releaseDate(), movie.getReleaseDate());
        assertEquals(movieCreateDto.description(), movie.getDescription());
        assertEquals(movieCreateDto.duration(), movie.getDuration());
        assertEquals(movieCreateDto.mediaUrl(), movie.getMediaUrl());
        assertEquals(movieCreateDto.imageUrl(), movie.getImageUrl());
    }

    @Test
    @DisplayName("Test fromCreateDtoToEntity should return null when create dto is null")
    void fromCreateDtoToEntityNull() {
        assertNull(MovieConverter.fromCreateDtoToEntity(null));
    }

    @Test
    @DisplayName("Test fromEntityToMovieRolePublicDto should convert entity to movie role public dto")
    void fromEntityToMovieRolePublicDto() {
        MovieCrewRole role = getMovie().getMovieCrew().get(0);

        MovieRolePublicDto movieRolePublicDto = MovieConverter.fromEntityToMovieRolePublicDto(role);

        assertEquals(role.getMovieCrew().getId(), movieRolePublicDto.id());
        assertEquals(role.getMovieCrew().getFullName(), movieRolePublicDto.fullName());
        assertEquals(role.getMovieCrew().getBirthDate(), movieRolePublicDto.birthDate());
        assertEquals(role.getMovieRole().name(), movieRolePublicDto.movieRole());
        assertEquals(role.getCharacterName(), movieRolePublicDto.characterName());
    }

    @Test
    @DisplayName("Test fromEntityToMovieRolePublicDto should return null when entity is null")
    void fromEntityToMovieRolePublicDtoNull() {
        assertNull(MovieConverter.fromEntityToMovieRolePublicDto(null));
    }

    @Test
    @DisplayName("Test fromEntityListToMovieRolePublicDtoList should convert entity list to movie role public dto list")
    void fromEntityListToMovieRolePublicDtoList() {
        MovieCrewRole role = getMovie().getMovieCrew().get(0);
        List<MovieCrewRole> roles = List.of(role, role);

        List<MovieRolePublicDto> movieRolePublicDtos = MovieConverter.fromEntityListToMovieRolePublicDtoList(roles);

        assertEquals(roles.size(), movieRolePublicDtos.size());
        assertEquals(roles.get(0).getMovieCrew().getId(), movieRolePublicDtos.get(0).id());
        assertEquals(roles.get(1).getMovieCrew().getId(), movieRolePublicDtos.get(1).id());
    }

    @Test
    @DisplayName("Test fromEntityListToMovieRolePublicDtoList should return empty list when entity list is null")
    void fromEntityListToMovieRolePublicDtoListNull() {
        assertEquals(0, MovieConverter.fromEntityListToMovieRolePublicDtoList(null).size());
    }

    @Test
    @DisplayName("Test fromEntityListToMovieRolePublicDtoList should return empty list when entity list is empty")
    void fromEntityListToMovieRolePublicDtoListEmpty() {
        assertEquals(0, MovieConverter.fromEntityListToMovieRolePublicDtoList(List.of()).size());
    }

    @Test
    @DisplayName("Test fromEntityListToCrewRolePublicDtoList should convert entity list to crew role public dto list")
    void fromEntityListToCrewRolePublicDtoList() {
        MovieCrewRole role = getMovie().getMovieCrew().get(0);
        List<MovieCrewRole> roles = List.of(role, role);

        List<CrewRolePublicDto> crewRolePublicDtos = MovieConverter.fromEntityListToCrewRolePublicDtoList(roles);

        assertEquals(roles.size(), crewRolePublicDtos.size());
        assertEquals(roles.get(0).getMovieRole().name(), crewRolePublicDtos.get(0).movieRole());
        assertEquals(roles.get(1).getMovieRole().name(), crewRolePublicDtos.get(1).movieRole());
    }

    @Test
    @DisplayName("Test fromEntityListToCrewRolePublicDtoList should return empty list when entity list is null")
    void fromEntityListToCrewRolePublicDtoListNull() {
        assertEquals(0, MovieConverter.fromEntityListToCrewRolePublicDtoList(null).size());
    }

    @Test
    @DisplayName("Test fromEntityListToCrewRolePublicDtoList should return empty list when entity list is empty")
    void fromEntityListToCrewRolePublicDtoListEmpty() {
        assertEquals(0, MovieConverter.fromEntityListToCrewRolePublicDtoList(List.of()).size());
    }

    @Test
    @DisplayName("Test fromEntityToLikePublicDto should convert entity to movie like public dto")
    void fromEntityToLikePublicDto() {
        Movie movie = getMovie();

        MovieLikePublicDto movieLikePublicDto = MovieConverter.fromEntityToLikePublicDto(movie, true);

        assertEquals(movie.getId(), movieLikePublicDto.movie().id());
        assertEquals(movie.getTitle(), movieLikePublicDto.movie().title());
        assertTrue(movieLikePublicDto.liked());
    }

    @Test
    @DisplayName("Test fromEntityToLikePublicDto should return null when entity is null")
    void fromEntityToLikePublicDtoNull() {
        assertNull(MovieConverter.fromEntityToLikePublicDto(null, true));
    }
}