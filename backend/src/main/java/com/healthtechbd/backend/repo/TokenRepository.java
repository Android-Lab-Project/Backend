package com.healthtechbd.backend.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthtechbd.backend.entity.Token;

public interface TokenRepository extends JpaRepository<Token,Long> {
    
    Token findByToken(String token);

    void removeByToken(String token);
}
