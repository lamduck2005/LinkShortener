package com.lamduck2005.linkshortener.repository;

import com.lamduck2005.linkshortener.entity.Snippet;
import com.lamduck2005.linkshortener.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long>, JpaSpecificationExecutor<Snippet> {

    Optional<Snippet> findByCustomAlias(String customAlias);

    Page<Snippet> findAllByUser(User user, Pageable pageable);

    long countByCreatedAtBetween(Instant from, Instant to);
}
