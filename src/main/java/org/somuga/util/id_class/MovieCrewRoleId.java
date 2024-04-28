package org.somuga.util.id_class;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MovieCrewRoleId implements Serializable {

    @Column(name = "movie_id")
    private Long movieId;
    @Column(name = "movie_crew_id")
    private Long movieCrewId;

    public MovieCrewRoleId() {
    }

    public MovieCrewRoleId(Long movieId, Long movieCrewId) {
        this.movieId = movieId;
        this.movieCrewId = movieCrewId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getMovieCrewId() {
        return movieCrewId;
    }

    public void setMovieCrewId(Long movieCrewId) {
        this.movieCrewId = movieCrewId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieCrewRoleId that = (MovieCrewRoleId) o;
        return Objects.equals(movieId, that.movieId) && Objects.equals(movieCrewId, that.movieCrewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, movieCrewId);
    }
}
