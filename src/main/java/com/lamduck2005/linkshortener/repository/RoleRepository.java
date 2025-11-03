package com.lamduck2005.linkshortener.repository;

import com.lamduck2005.linkshortener.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RoleRepository extends JpaRepository<Role, Integer> {
}
