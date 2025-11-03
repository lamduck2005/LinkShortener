package com.lamduck2005.linkshortener.repository;

import com.lamduck2005.linkshortener.entity.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {
    Optional<Snippet> findByShortCode(String shortCode);
}
