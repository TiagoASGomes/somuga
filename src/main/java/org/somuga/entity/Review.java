package org.somuga.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer reviewScore;
    @Column(length = 1024)
    private String writtenReview;
    @ManyToOne
    private User user;
    @ManyToOne
    private Media media;

    public Review(Integer reviewScore, String writtenReview, User user, Media media) {
        this.reviewScore = reviewScore;
        this.writtenReview = writtenReview;
        this.user = user;
        this.media = media;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Integer getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(Integer reviewScore) {
        this.reviewScore = reviewScore;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getWrittenReview() {
        return writtenReview;
    }

    public void setWrittenReview(String writtenReview) {
        this.writtenReview = writtenReview;
    }
}
