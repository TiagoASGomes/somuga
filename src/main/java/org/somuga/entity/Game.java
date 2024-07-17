package org.somuga.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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
    private List<GameGenre> genres;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "games")
    private List<Platform> platforms;

    public void addPlatform(Platform platform) {
        if (platforms == null) {
            platforms = new ArrayList<>();
        }
        platforms.add(platform);
        platform.addGame(this);

    }

    public void addGenre(GameGenre genre) {
        if (genres == null) {
            genres = new ArrayList<>();
        }
        genres.add(genre);
        genre.addGame(this);
    }

    public void removePlatform(Platform platform) {
        if (platforms == null) {
            return;
        }
        platforms.remove(platform);
        platform.removeGame(this);
    }

    public void removeGenre(GameGenre genre) {
        if (genres == null) {
            return;
        }
        genres.remove(genre);
        genre.removeGame(this);
    }
}
