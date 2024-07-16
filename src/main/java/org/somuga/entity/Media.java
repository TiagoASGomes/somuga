package org.somuga.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.somuga.enums.MediaType;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "media")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(nullable = false, name = "title")
    private String title;
    private Date releaseDate;
    @Column(length = 1000, nullable = false, name = "description")
    private String description;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "media_url", nullable = false)
    private String mediaUrl;
    @Column(name = "media_creator_id", nullable = false)
    private String mediaCreatorId;
    @OneToMany(mappedBy = "media",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Like> likes;
    @OneToMany(mappedBy = "media",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Review> reviews;

}
