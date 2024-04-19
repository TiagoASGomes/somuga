package org.somuga.entity;

import jakarta.persistence.Entity;

import java.util.Set;

@Entity
public class Movie extends Media {

    private Set<String> actors;
    private String producer;

    public Movie() {
    }

    public Set<String> getActors() {
        return actors;
    }

    public void setActors(Set<String> actors) {
        this.actors = actors;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }
}
