package org.somuga.entity;

import jakarta.persistence.*;
import org.somuga.enums.MovieRole;

import java.util.Set;

@Entity
@Table(name = "movie_crew")
public class MovieCrew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MovieRole role;

    private String name;

    private String character;

    @ManyToMany
    private Set<Movie> movies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovieRole getRole() {
        return role;
    }

    public void setRole(MovieRole role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }
}
