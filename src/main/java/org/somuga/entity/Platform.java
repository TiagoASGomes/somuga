package org.somuga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "platforms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String platformName;
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Game> games;

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
