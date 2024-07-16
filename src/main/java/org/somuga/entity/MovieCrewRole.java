package org.somuga.entity;

import jakarta.persistence.*;
import lombok.*;
import org.somuga.enums.MovieRole;
import org.somuga.util.id_class.MovieCrewRoleId;

import java.util.Objects;

@Entity(name = "MovieCrewRole")
@Table(name = "movie_crew_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
