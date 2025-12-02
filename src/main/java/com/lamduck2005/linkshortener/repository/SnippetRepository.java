package com.lamduck2005.linkshortener.repository;

import com.lamduck2005.linkshortener.entity.Snippet;
import com.lamduck2005.linkshortener.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {

    Optional<Snippet> findByShortCode(String shortCode);

    Page<Snippet> findAllByUser(User user, Pageable pageable);
}
