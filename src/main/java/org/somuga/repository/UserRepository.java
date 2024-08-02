package org.somuga.repository;


import org.somuga.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserNameIgnoreCase(String userName);

    Page<User> findAllByUserNameContainingIgnoreCaseAndActiveTrue(String userName, Pageable page);

    Page<User> findAllByActiveTrue(Pageable page);
}
