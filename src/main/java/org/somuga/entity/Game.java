package org.somuga.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "games")
public class Game extends Media {

    private String description;
    private Double price;
    @OneToMany(mappedBy = "games")
    private Developer developer;
    @ManyToMany(mappedBy = "games")
    private Set<GameGenre> genres;
    @ManyToMany(mappedBy = "games")
    private Set<Platform> platforms;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<Platform> platforms) {
        this.platforms = platforms;
    }

    public Set<GameGenre> getGenres() {
        return genres;
    }

    public void setGenres(Set<GameGenre> genres) {
        this.genres = genres;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
