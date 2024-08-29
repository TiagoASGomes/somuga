package org.somuga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;
    @Column(name = "user_name", nullable = false)
    private String userName;
    @Column(nullable = false, unique = true)
    private String email;
    @OneToMany(mappedBy = "user")
    private List<Like> likes;
    @OneToMany(mappedBy = "user")
    private List<Review> reviews;
    private LocalDate joinDate;
    private boolean active;

}
