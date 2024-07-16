package org.somuga.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "review_score", nullable = false)
    private Integer reviewScore;
    @Column(length = 1024, name = "written_review", nullable = false)
    private String writtenReview;
    @ManyToOne
    private User user;
    @ManyToOne
    private Media media;

}
