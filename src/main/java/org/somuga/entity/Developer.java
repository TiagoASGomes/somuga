package org.somuga.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "developers")
public class Developer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String developerName;
    private String developerCreatorId;
    private List<String> socials;
    @OneToMany(mappedBy = "developer")
    private List<Game> games;

    public Developer() {
    }

    public Developer(String developerName, List<String> socials, String developerCreatorId) {
        this.developerName = developerName;
        this.socials = socials;
        this.developerCreatorId = developerCreatorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public List<String> getSocials() {
        return socials;
    }

    public void setSocials(List<String> socials) {
        this.socials = socials;
    }

    public String getDeveloperCreatorId() {
        return developerCreatorId;
    }

    public void setDeveloperCreatorId(String developerCreatorId) {
        this.developerCreatorId = developerCreatorId;
    }
}
