package com.lamduck2005.linkshortener.repository;

import com.lamduck2005.linkshortener.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends JpaRepository<User, Long> {
}
