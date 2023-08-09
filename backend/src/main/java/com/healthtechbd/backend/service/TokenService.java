package com.healthtechbd.backend.service;

import com.healthtechbd.backend.entity.Token;
import com.healthtechbd.backend.repo.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    @Autowired
    TokenRepository tokenRepository;
    @Value("${application.security.jwt.secret-key}")
    private String jwtSecret;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${jdj.secure.token.validity}")
    private int tokenValidityInSeconds;


    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void saveToken(Token token) {
        tokenRepository.save(token);
    }

    public int getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }

    public Token findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void removeToken(Token token) {
        tokenRepository.delete(token);
    }

    public void removeToken(String token) {
        tokenRepository.removeByToken(token);
    }


}
