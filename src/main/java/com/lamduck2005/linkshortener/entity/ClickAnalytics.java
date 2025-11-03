package com.lamduck2005.linkshortener.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "click_analytics")
public class ClickAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mối quan hệ Nhiều-Một với Snippet (Click này thuộc về Snippet nào)
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Bắt buộc phải có snippet_id
    @JoinColumn(name = "snippet_id", nullable = false)
    private Snippet snippet;

    @CreationTimestamp
    @Column(name = "click_time", nullable = false, updatable = false)
    private Instant clickTime;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    public ClickAnalytics(Snippet snippet, String ipAddress, String userAgent) {
        this.snippet = snippet;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    // Với entity đơn giản không có Set, thường không cần override equals/hashCode
}