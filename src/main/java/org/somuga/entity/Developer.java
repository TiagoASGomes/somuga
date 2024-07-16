package org.somuga.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "developers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Developer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, name = "developer_name", nullable = false)
    private String developerName;
    private List<String> socials;
    @OneToMany(mappedBy = "developer",
            fetch = FetchType.LAZY)
    private List<Game> games;
}
