package org.somuga.repository;

import org.somuga.entity.Movie;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends MediaBaseRepository<Movie>, JpaSpecificationExecutor<Movie> {

}
