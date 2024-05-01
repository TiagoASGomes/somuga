package org.somuga.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.somuga.enums.MovieRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "Movie")
@Table(name = "movies")
public class Movie extends Media {

    private Integer duration;

    @OneToMany(mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<MovieCrewRole> movieCrew = new ArrayList<>();

    public List<MovieCrewRole> getMovieCrew() {
        return movieCrew;
    }

    public void setMovieCrew(List<MovieCrewRole> movieCrew) {
        this.movieCrew = movieCrew;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void addMovieCrew(MovieCrew movieCrew, MovieRole movieRole, String characterName) {
        MovieCrewRole movieCrewRole = new MovieCrewRole(movieCrew, this);
        movieCrewRole.setMovieRole(movieRole);
        movieCrewRole.setCharacterName(characterName);
        this.movieCrew.add(movieCrewRole);
        movieCrew.getRoles().add(movieCrewRole);
    }

    public void removeMovieCrew(MovieCrew movieCrew) {
        for (MovieCrewRole movieCrewRole : this.movieCrew) {
            if (movieCrewRole.getMovie().equals(this) &&
                    movieCrewRole.getMovieCrew().equals(movieCrew)) {
                this.movieCrew.remove(movieCrewRole);
                movieCrew.getRoles().remove(movieCrewRole);
                movieCrewRole.setMovie(null);
                movieCrewRole.setMovieCrew(null);
            }

        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(duration, movie.duration) && Objects.equals(movieCrew, movie.movieCrew);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, movieCrew);
    }
}
