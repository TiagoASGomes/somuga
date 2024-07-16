package org.somuga.util.id_class;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieCrewRoleId implements Serializable {

    @Column(name = "movie_id")
    private Long movieId;
    @Column(name = "movie_crew_id")
    private Long movieCrewId;

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
