package org.somuga.entity;

import jakarta.persistence.*;
import org.somuga.enums.MovieRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "Movie")
@Table(name = "movies")
public class Movie extends Media {

    @Column(name = "duration")
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
        MovieCrewRole movieCrewRole = new MovieCrewRole(movieCrew, this, movieRole, characterName);
        this.movieCrew.add(movieCrewRole);
    }

    public void removeMovieCrew(MovieCrewRole movieCrew) {
        if (this.movieCrew == null) {
            return;
        }
        this.movieCrew.remove(movieCrew);
        movieCrew.getMovieCrew().removeRole(movieCrew);
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
