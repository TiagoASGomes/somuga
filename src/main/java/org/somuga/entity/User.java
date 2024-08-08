package org.somuga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
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
    @OneToMany(mappedBy = "user")
    private List<Like> likes;
    @OneToMany(mappedBy = "user")
    private List<Review> reviews;
    private Date joinDate;
    private boolean active;

}
