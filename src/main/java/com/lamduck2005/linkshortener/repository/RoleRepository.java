package com.lamduck2005.linkshortener.repository;

import com.lamduck2005.linkshortener.entity.ERole;
import com.lamduck2005.linkshortener.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
