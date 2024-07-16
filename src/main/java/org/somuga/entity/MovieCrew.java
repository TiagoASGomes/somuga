package org.somuga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity(name = "MovieCrew")
@Table(name = "movie_crew")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieCrew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    private Date birthDate;
    private String crewCreatorId;
    @OneToMany(mappedBy = "movieCrew",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<MovieCrewRole> roles = new ArrayList<>();

    public void removeRole(MovieCrewRole role) {
        if (this.roles == null) {
            return;
        }
        this.roles.remove(role);
    }

    public void addRole(MovieCrewRole movieCrewRole) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(movieCrewRole);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieCrew movieCrew = (MovieCrew) o;
        return Objects.equals(id, movieCrew.id) && Objects.equals(fullName, movieCrew.fullName) && Objects.equals(birthDate, movieCrew.birthDate) && Objects.equals(crewCreatorId, movieCrew.crewCreatorId) && Objects.equals(roles, movieCrew.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, birthDate, crewCreatorId, roles);
    }

}
