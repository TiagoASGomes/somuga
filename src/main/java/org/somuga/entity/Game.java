package org.somuga.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "games")
public class Game extends Media {

    @Column(name = "price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double price;
    @ManyToOne
    private Developer developer;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "games")
    private Set<GameGenre> genres;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "games")
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

    public void removePlatform(Platform platform) {
        if (platforms != null) {
            platforms.remove(platform);
        }
    }

    public void removeGenre(GameGenre genre) {
        if (genres != null) {
            genres.remove(genre);
        }
    }
}
