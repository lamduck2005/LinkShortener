package com.lamduck2005.linkshortener.repository;

import com.lamduck2005.linkshortener.entity.ClickAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {

    long countBySnippetId(Long snippetId);
}
