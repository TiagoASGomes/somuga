package org.somuga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "MovieCrew")
@Table(name = "movie_crew")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MovieCrew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    private Date birthDate;
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

}
