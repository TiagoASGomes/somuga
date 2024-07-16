package org.somuga.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "games")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Game extends Media {

    @Column(name = "price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double price;
    @ManyToOne
    private Developer developer;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "games")
    private Set<GameGenre> genres;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "games")
    private Set<Platform> platforms;

//    public void setPlatforms(Set<Platform> platforms) {
//        this.platforms = platforms;
//        platforms.forEach(platform -> platform.addGame(this));
//    }
//
//    public void setGenres(Set<GameGenre> genres) {
//        this.genres = genres;
//        genres.forEach(genre -> genre.addGame(this));
//    }

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
