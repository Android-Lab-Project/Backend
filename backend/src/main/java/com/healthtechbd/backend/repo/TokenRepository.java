package com.healthtechbd.backend.repo;

import com.healthtechbd.backend.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token,Long> {
    
    Token findByToken(String token);

    void removeByToken(String token);
}
