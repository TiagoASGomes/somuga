package org.somuga.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@Table(name = "medias")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private MediaType mediaType;
    @OneToMany(mappedBy = "media")
    private Set<Like> likes;
    @OneToMany(mappedBy = "media")
    private Set<Rating> ratings;
}
