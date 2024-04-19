package org.somuga.repository;

import org.somuga.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MediaBaseRepository<T extends Media> extends JpaRepository<T, Long> {
}
