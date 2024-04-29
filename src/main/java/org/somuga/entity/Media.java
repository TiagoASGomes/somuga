package org.somuga.entity;

import jakarta.persistence.*;
import org.somuga.enums.MediaType;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "media")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "media_id_generator")
    @TableGenerator(name = "media_id_generator",
            table = "id_gen",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            pkColumnValue = "media_id",
            initialValue = 1,
            allocationSize = 1)
    private Long id;
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;
    private String title;
    private Date releaseDate;
    private String description;
    @OneToMany(mappedBy = "media",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Like> likes;
    @OneToMany(mappedBy = "media",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Review> reviews;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Set<Like> getLikes() {
        return likes;
    }

    public void setLikes(Set<Like> likes) {
        this.likes = likes;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
