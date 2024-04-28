package org.somuga.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "movies")
public class Movie extends Media {

    @ManyToMany(mappedBy = "movies")
    private Set<MovieCrew> movieCrew;

    public Set<MovieCrew> getMovieCrew() {
        return movieCrew;
    }

    public void setMovieCrew(Set<MovieCrew> movieCrew) {
        this.movieCrew = movieCrew;
    }
}
