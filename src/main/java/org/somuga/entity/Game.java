package org.somuga.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "games")
public class Game extends Media {

    private String description;
    private Double price;
    @ManyToOne
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
        platforms.forEach(platform -> platform.addGame(this));
    }

    public Set<GameGenre> getGenres() {
        return genres;
    }

    public void setGenres(Set<GameGenre> genres) {
        this.genres = genres;
        genres.forEach(genre -> genre.addGame(this));
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
        developer.addGame(this);
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
