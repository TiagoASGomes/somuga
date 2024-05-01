package org.somuga.entity;

import jakarta.persistence.*;
import org.somuga.enums.MovieRole;
import org.somuga.util.id_class.MovieCrewRoleId;

import java.util.Objects;

@Entity(name = "MovieCrewRole")
@Table(name = "movie_crew_role")
public class MovieCrewRole {

    @EmbeddedId
    private MovieCrewRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieCrewId")
    private MovieCrew movieCrew;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("movieId")
    private Movie movie;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "movie_role")
    private MovieRole movieRole;
    @Column(name = "character_name")
    private String characterName;

    public MovieCrewRole() {
    }

    public MovieCrewRole(MovieCrew movieCrew, Movie movie) {
        this.movieCrew = movieCrew;
        this.movie = movie;
        this.id = new MovieCrewRoleId(movie.getId(), movieCrew.getId());
    }

    public MovieCrewRoleId getId() {
        return id;
    }

    public void setId(MovieCrewRoleId id) {
        this.id = id;
    }

    public MovieCrew getMovieCrew() {
        return movieCrew;
    }

    public void setMovieCrew(MovieCrew movieCrew) {
        this.movieCrew = movieCrew;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public MovieRole getMovieRole() {
        return movieRole;
    }

    public void setMovieRole(MovieRole movieRole) {
        this.movieRole = movieRole;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieCrewRole that = (MovieCrewRole) o;
        return Objects.equals(id, that.id) && Objects.equals(movieCrew, that.movieCrew) && Objects.equals(movie, that.movie) && movieRole == that.movieRole && Objects.equals(characterName, that.characterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, movieCrew, movie, movieRole, characterName);
    }
}
