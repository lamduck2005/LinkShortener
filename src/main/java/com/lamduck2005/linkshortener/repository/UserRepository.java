package com.lamduck2005.linkshortener.repository;

import com.lamduck2005.linkshortener.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByEmail(String email);

    long countByCreatedAtBetween(Instant from, Instant to);

    @EntityGraph(attributePaths = "roles")
    Page<User> findAll(Pageable pageable);
}
