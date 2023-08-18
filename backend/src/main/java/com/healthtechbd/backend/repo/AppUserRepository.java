package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String userEmail);

    boolean existsByEmail(String email);

    boolean existsByFirstName(String firstName);

    boolean existsByLastName(String lastName);
}
