package com.lamduck2005.linkshortener.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "snippets")
public class Snippet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 20)
    private String shortCode;

    @Enumerated(EnumType.STRING) // <-- BÁO JPA LƯU DẠNG STRING
    @Column(name = "content_type", nullable = false, length = 10)
    private ContentType contentType;

    @Column(name = "content_data", nullable = false, columnDefinition = "TEXT")
    private String contentData;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "password_hash")
    private String passwordHash;

    // Mối quan hệ Nhiều-Một với User (Snippet này thuộc về User nào)
    @ManyToOne(fetch = FetchType.LAZY) // LAZY là tốt nhất
    @JoinColumn(name = "user_id") // Tên cột khóa ngoại trong bảng snippets
    private User user;

    // Mối quan hệ Một-Nhiều với ClickAnalytics (để lấy lịch sử click của snippet này)
    // 'mappedBy' trỏ đến tên trường 'snippet' trong class ClickAnalytics
    @OneToMany(mappedBy = "snippet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ClickAnalytics> clicks = new HashSet<>();


    public Snippet(String shortCode, ContentType contentType, String contentData, User user) {
        this.shortCode = shortCode;
        this.contentType = contentType;
        this.contentData = contentData;
        this.user = user; // Có thể null nếu là snippet công khai
    }

    // Nên override equals() và hashCode() nếu dùng Set trong @OneToMany
    // Hoặc dùng Lombok @EqualsAndHashCode(exclude = "clicks")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snippet snippet = (Snippet) o;
        return id != null && id.equals(snippet.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}