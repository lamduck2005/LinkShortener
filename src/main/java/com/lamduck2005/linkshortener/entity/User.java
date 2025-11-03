package com.lamduck2005.linkshortener.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tương ứng với BIGSERIAL
    private Long id; // BIGSERIAL là BIGINT 64-bit

    @Column(nullable = false, unique = true, length = 255) // Nên giới hạn độ dài email
    private String email;

    @Column(nullable = false, unique = true, length = 100) // Nên giới hạn độ dài username
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Mối quan hệ Nhiều-Nhiều với Role
    @ManyToMany(fetch = FetchType.EAGER) // Load Role cùng User
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Mối quan hệ Một-Nhiều với Snippet (để lấy các snippet của user này)
    // 'mappedBy' trỏ đến tên trường 'user' trong class Snippet
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Snippet> snippets = new HashSet<>();

    public User(String email, String username, String passwordHash) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isActive = false;
    }

    // Nên override equals() và hashCode() nếu dùng Set trong @ManyToMany/@OneToMany
    // Hoặc dùng Lombok @EqualsAndHashCode(exclude = {"roles", "snippets"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}