package com.lamduck2005.linkshortener.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tương ứng với SERIAL
    private Integer id; // SERIAL là INT 32-bit

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false, unique = true)
    private ERole name;

    // Thêm mối quan hệ ngược lại với User (không bắt buộc, nhưng tiện để truy vấn)
    // 'mappedBy' trỏ đến tên trường 'roles' trong class User
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    public Role(ERole name) {
        this.name = name;
    }

    // Nên override equals() và hashCode() nếu dùng Set trong @ManyToMany
    // Hoặc dùng Lombok @EqualsAndHashCode(exclude = "users")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id != null && id.equals(role.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}