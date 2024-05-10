package org.somuga.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @OneToMany(mappedBy = "user")
    private Set<Like> likes;
    @OneToMany(mappedBy = "user")
    private Set<Review> reviews;
    private Date joinDate;
    private boolean active;

    public User() {
    }

    public User(String id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Set<Like> getLikes() {
        return likes;
    }

    public void setLikes(Set<Like> likes) {
        this.likes = likes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
