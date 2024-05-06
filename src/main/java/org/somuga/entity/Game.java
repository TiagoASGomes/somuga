package org.somuga.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "games")
public class Game extends Media {

    private Double price;
    @ManyToOne
    private Developer developer;
    @ManyToMany(mappedBy = "games",
            cascade = CascadeType.REMOVE)
    private Set<GameGenre> genres;
    @ManyToMany(mappedBy = "games",
            cascade = CascadeType.REMOVE)
    private Set<Platform> platforms;

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
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
