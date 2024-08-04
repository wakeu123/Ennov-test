package com.ennov.tech.repositories;

import com.ennov.tech.domains.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByUsernameOrEmail(String username, String email);
}
