package com.healthtechbd.backend.service;

import com.healthtechbd.backend.entity.AppUser;
import com.healthtechbd.backend.entity.Token;
import com.healthtechbd.backend.repo.TokenRepository;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Service
public class TokenService {
    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);
    private static final Charset US_ASCII = StandardCharsets.US_ASCII;
    @Autowired
    TokenRepository tokenRepository;
    @Value("${jdj.secure.token.validity}")
    private int tokenValidityInSeconds;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token createToken(AppUser user) {
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), US_ASCII);
        Token token = new Token();
        token.setToken(tokenValue);
        token.setExpireTime(LocalDateTime.now().plusSeconds(getTokenValidityInSeconds()));
        token.setAppUser(user);
        saveToken(token);

        return token;
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
