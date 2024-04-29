package org.somuga.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity(name = "MovieCrew")
@Table(name = "movie_crew")
@NaturalIdCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MovieCrew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NaturalId
    private String fullName;
    private Date birthDate;
    @OneToMany(mappedBy = "movieCrew",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<MovieCrewRole> roles = new ArrayList<>();

    public MovieCrew() {
    }

    public MovieCrew(String fullName, Date birthDate) {
        this.fullName = fullName;
        this.birthDate = birthDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public List<MovieCrewRole> getRoles() {
        return roles;
    }

    public void setRoles(List<MovieCrewRole> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieCrew movieCrew = (MovieCrew) o;
        return Objects.equals(id, movieCrew.id) && Objects.equals(fullName, movieCrew.fullName) && Objects.equals(birthDate, movieCrew.birthDate) && Objects.equals(roles, movieCrew.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, birthDate, roles);
    }
}
