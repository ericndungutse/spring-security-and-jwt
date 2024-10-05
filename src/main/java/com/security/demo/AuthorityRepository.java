package com.security.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, AuthorityId> {
    List<User> findByAuthority(String authority);
}
