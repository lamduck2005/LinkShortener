package com.lamduck2005.linkshortener.repository;

import com.lamduck2005.linkshortener.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    long countByCreatedAtBetween(Instant from, Instant to);
}
