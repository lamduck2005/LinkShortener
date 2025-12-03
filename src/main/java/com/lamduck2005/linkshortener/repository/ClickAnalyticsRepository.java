package com.lamduck2005.linkshortener.repository;

import com.lamduck2005.linkshortener.entity.ClickAnalytics;
import com.lamduck2005.linkshortener.entity.Snippet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ClickAnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {

    long countBySnippetId(Long snippetId);

    long countByClickTimeBetween(Instant from, Instant to);

    /**
     * Top snippet theo tổng số click (toàn bộ lịch sử).
     */
    @Query("SELECT ca.snippet AS snippet, COUNT(ca) AS clickCount " +
            "FROM ClickAnalytics ca " +
            "GROUP BY ca.snippet " +
            "ORDER BY clickCount DESC")
    List<TopSnippetClicksProjection> findTopSnippetsAllTime(Pageable pageable);

    /**
     * Top snippet theo số click trong khoảng [from, to).
     */
    @Query("SELECT ca.snippet AS snippet, COUNT(ca) AS clickCount " +
            "FROM ClickAnalytics ca " +
            "WHERE ca.clickTime >= :from AND ca.clickTime < :to " +
            "GROUP BY ca.snippet " +
            "ORDER BY clickCount DESC")
    List<TopSnippetClicksProjection> findTopSnippetsInPeriod(
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );

    interface TopSnippetClicksProjection {
        Snippet getSnippet();

        long getClickCount();
    }
}
