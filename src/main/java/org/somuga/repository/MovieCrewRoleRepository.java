package org.somuga.repository;

import org.somuga.entity.MovieCrewRole;
import org.somuga.util.id_class.MovieCrewRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCrewRoleRepository extends JpaRepository<MovieCrewRole, MovieCrewRoleId> {
}
