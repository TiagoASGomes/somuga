package org.somuga.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_genres")
public class GameGenre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 50, name = "genre")
    private String genre;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Game> games;

    public GameGenre() {
    }

    public GameGenre(String genre) {
        this.genre = genre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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
            game.removeGenre(this);
        }
    }
}
