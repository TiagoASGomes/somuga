package org.somuga.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.somuga.enums.MovieRole;
import org.somuga.util.id_class.MovieCrewRoleId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "Movie")
@Table(name = "movies")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Movie extends Media {

    @Column(name = "duration")
    private Integer duration;

    @OneToMany(mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<MovieCrewRole> movieCrew;

    public void addMovieCrew(MovieCrew movieCrew, MovieRole movieRole, String characterName) {
        MovieCrewRole movieCrewRole = MovieCrewRole.builder()
                .movieCrew(movieCrew)
                .movie(this)
                .movieRole(movieRole)
                .characterName(characterName)
                .id(new MovieCrewRoleId(this.getId(), movieCrew.getId()))
                .build();
        if (this.movieCrew == null) {
            this.movieCrew = new ArrayList<>();
        }
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
