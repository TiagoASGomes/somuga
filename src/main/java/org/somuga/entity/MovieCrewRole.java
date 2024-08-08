package org.somuga.entity;

import jakarta.persistence.*;
import lombok.*;
import org.somuga.enums.MovieRole;
import org.somuga.util.id_class.MovieCrewRoleId;

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
}
