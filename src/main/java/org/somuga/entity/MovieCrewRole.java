package org.somuga.entity;

import jakarta.persistence.*;
import lombok.*;
import org.somuga.enums.MovieRole;

@Entity(name = "MovieCrewRole")
@Table(name = "movie_crew_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCrewRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MovieCrew movieCrew;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "movie_role")
    private MovieRole movieRole;
    @Column(name = "character_name")
    private String characterName;
}
