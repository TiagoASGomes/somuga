package org.somuga.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "platforms")
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String platformName;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Game> games;

    public Platform() {
    }

    public Platform(String platformName) {
        this.platformName = platformName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public void addGame(Game game) {
        if (games == null) {
            games = new ArrayList<>();
            games.add(game);
        } else if (!games.contains(game)) {
            games.add(game);
        }
    }

    public void removeGame(Game game) {
        if (games != null) {
            games.remove(game);
            game.removePlatform(this);
        }
    }
}
