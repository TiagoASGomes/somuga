package org.somuga.entity;

import jakarta.persistence.Entity;

import java.util.Set;

@Entity
public class Game extends Media {

    private String company;
    private String genre;
    private Set<String> platforms;

    public Game() {
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Set<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<String> platforms) {
        this.platforms = platforms;
    }
}
